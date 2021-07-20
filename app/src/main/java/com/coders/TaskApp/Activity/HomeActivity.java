package com.coders.TaskApp.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.coders.TaskApp.Fragment.CategoryFragment;
import com.coders.TaskApp.Fragment.HomeFragment;
import com.coders.TaskApp.R;
import com.coders.TaskApp.ViewModel.HomeActivitySharedViewModel;
import com.coders.TaskApp.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    SearchView searchView;
    HomeActivitySharedViewModel viewModel;
    int searchIconCenterX, searchIconCenterY;
    Fragment homeFragment, categoryFragment, active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSearchToolbar();
        setSupportActionBar(binding.toolbar);
        viewModel = new ViewModelProvider(this).get(HomeActivitySharedViewModel.class);
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            categoryFragment = new CategoryFragment();
            fragmentManager.beginTransaction()
                    .add(binding.fragmentContainerView.getId(), homeFragment)
                    .commit();
            active = homeFragment;
            fragmentManager.beginTransaction()
                    .add(binding.fragmentContainerView.getId(), categoryFragment)
                    .hide(categoryFragment)
                    .commit();
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                fragmentManager.beginTransaction()
                        .hide(active)
                        .show(homeFragment)
                        .commit();
                active = homeFragment;
                return true;
            } else if (item.getItemId() == R.id.category) {
                fragmentManager.beginTransaction()
                        .hide(active)
                        .show(categoryFragment)
                        .commit();
                active = categoryFragment;
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isIconified())
            super.onBackPressed();
        else
            closeSearchToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            View menuView = findViewById(R.id.search);

            int[] itemWindowLocation = new int[2];
            menuView.getLocationInWindow(itemWindowLocation);

            int[] toolbarWindowLocation = new int[2];
            binding.toolbar.getLocationInWindow(toolbarWindowLocation);

            int itemX = itemWindowLocation[0] - toolbarWindowLocation[0];
            int itemY = itemWindowLocation[1] - toolbarWindowLocation[1];
            searchIconCenterX = itemX + menuView.getWidth() / 2;
            searchIconCenterY = itemY + menuView.getHeight() / 2;
            searchView.setIconified(false);

            //Change status bar color to white
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.white));

            //change status bar icon color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


            circleReveal(binding.searchToolbar.searchToolbar, searchIconCenterX, searchIconCenterY, true);
            return true;
        }
        return false;
    }

    private void setSearchToolbar() {
        binding.searchToolbar.searchToolbar.inflateMenu(R.menu.search);
        binding.searchToolbar.searchToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.searchToolbar.searchToolbar.setNavigationOnClickListener(v -> closeSearchToolbar());

        searchView = (SearchView) binding.searchToolbar.searchToolbar.getMenu().findItem(R.id.search_).getActionView();
        searchView.setIconified(true);
        searchView.setFocusable(true);
        searchView.setQueryHint("Search...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //remove search close button
        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        ViewGroup linearLayoutSearchView = (ViewGroup) icon.getParent();
        linearLayoutSearchView.removeView(icon);

        //remove search icon as hint
        SearchView.SearchAutoComplete search = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        search.setHint("Search...");

        //remove default searchView underline
        findViewById(androidx.appcompat.R.id.search_plate).setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

    }

    private void closeSearchToolbar() {
        circleReveal(binding.searchToolbar.searchToolbar, searchIconCenterX, searchIconCenterY, false);

        searchView.clearFocus();
        searchView.setIconified(true);
        //restore status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        //restore status bar icon color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.getDecorView().setSystemUiVisibility(0);
    }


    public void circleReveal(View myView, int cx, int cy, boolean isForward) {

        Animator anim;
        float radius = Math.max(cx, cy);
        if (isForward)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, radius);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, radius, 0);

        anim.setDuration(300);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isForward) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isForward)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();

    }

//    private void ShowInstruction() {
//
//        //Variable accessed from inner class
//        final View[] firstTask = new View[1];
//        final boolean[] isAdded = new boolean[1];
//        Todo sample = new Todo();
//
//        if (recyclerView.findViewHolderForAdapterPosition(0) == null) {
//            adapter.addSampleTask();
//            empty_task_animation.setVisibility(View.INVISIBLE);
//            isAdded[0] = true;
//        }
//
//        recyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(HomeActivity.this, recyclerView.findViewHolderForAdapterPosition(0) + "", Toast.LENGTH_LONG).show();
//                firstTask[0] = recyclerView.findViewHolderForAdapterPosition(0).itemView;
//
//                new TapTargetSequence(HomeActivity.this).targets(
//                        TapTarget.forView(fab, "Add Task!", "Tap to add task and get started")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f),
//
//                        TapTarget.forView(firstTask[0], "Edit Task!", "Tap on any task to edit")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f),
//
//                        TapTarget.forToolbarMenuItem(toolbar, R.id.search, "Search", "Tap to search task")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f),
//
//                        TapTarget.forView(firstTask[0], "Rearrange!", "Long press drag and drop to rearrange task")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f),
//
//                        TapTarget.forToolbarMenuItem(toolbar, R.id.overflow, "More Options!", "Tap here for more options")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f),
//                        TapTarget.forView(firstTask[0], "Delete!", "Swipe right on any task to delete")
//                                .drawShadow(true)
//                                .descriptionTextAlpha(0.7f)
//                                .cancelable(false)
//                                .transparentTarget(true)
//                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
//                                .outerCircleAlpha(0.8f)
//                ).listener(new TapTargetSequence.Listener() {
//                    @Override
//                    public void onSequenceFinish() {
//                        if (isAdded[0]) {
//                            Animation animation = AnimationUtils.makeOutAnimation(HomeActivity.this, true);
//                            firstTask[0].setAnimation(animation);
//                            animation.setDuration(600);
//                            animation.start();
//
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.removeSampleTask();
//                                    empty_task_animation.setVisibility(View.VISIBLE);
//                                }
//                            }, animation.getDuration());
//                        }
//                    }
//
//                    @Override
//                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
//
//                    }
//
//                    @Override
//                    public void onSequenceCanceled(TapTarget lastTarget) {
//
//                    }
//                }).start();
//
//
//            }
//        }, 50);
//    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);

        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }

        return !ranBefore;

    }
}