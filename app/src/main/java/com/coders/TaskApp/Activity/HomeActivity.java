package com.coders.TaskApp.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Adapter.TodoAdapter;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.Utils.TodoTouchHelperCallback;
import com.coders.TaskApp.Utils.Utils;
import com.coders.TaskApp.ViewModel.HomeActivityViewModel;
import com.coders.TaskApp.models.Todo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


public class HomeActivity extends AppCompatActivity {

    MaterialToolbar toolbar, searchToolbar;
    SearchView searchView;
    RecyclerView recyclerView;
    TodoAdapter adapter;
    FloatingActionButton fab;
    HomeActivityViewModel viewModel;
    LinearLayout empty_task_animation;
    boolean FirstTime;
    int searchIconCenterX, searchIconCenterY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllId();

        setSupportActionBar(toolbar);
        setSearchToolbar();
        adapter = new TodoAdapter(this);
        recyclerView.setAdapter(adapter);

        TodoTouchHelperCallback todoTouchHelperCallback = new TodoTouchHelperCallback(0,
                ItemTouchHelper.RIGHT, adapter, this);
        todoTouchHelperCallback.setSwipeListener((viewHolder, direction) -> {
            if (viewHolder instanceof TodoAdapter.ViewHolder && direction == ItemTouchHelper.RIGHT) {

                int position = viewHolder.getAbsoluteAdapterPosition();
                Todo item = (Todo) adapter.getCurrentList().get(position);
                viewModel.delete(item);
//                item.getParentHeader().removeChildItem(item);
                Snackbar snackbar = Snackbar.make(findViewById(R.id.root), "Task deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> {
                    viewModel.insert(item);
//                    item.getParentHeader().addChildItem(item);
                });
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        NotificationHelper.cancel(HomeActivity.this, item.getUid());
                    }
                });
                snackbar.show();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(todoTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        viewModel.getAllTask().observe(this, todos -> {
            if (FirstTime) return;
            if (todos.size() == 0) {
                empty_task_animation.setVisibility(View.VISIBLE);
            } else {
                empty_task_animation.setVisibility(View.INVISIBLE);
            }
            new Utils(this).getFinalListAsync(todos, finalList -> {
                adapter.setAllTask(finalList);
                adapter.getFilter().filter(viewModel.getSearchQuery());
            });
        });


        fab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(fab, (int) fab.getX(), (int) fab.getY(), fab.getMeasuredWidth(), fab.getMeasuredHeight());
            startActivity(intent, optionsCompat.toBundle());
        });


        if (isFirstTime()) {
            FirstTime = true;
            recyclerView.postDelayed(() -> ShowInstruction(), 50);

        }

        adapter.setTodoCallback(new TodoAdapter.TodoCallback() {
            @Override
            public void OnCheckboxClick(Todo item, boolean isChecked) {
                item.setCompleted(isChecked);
                viewModel.update(item);
            }

            @Override
            public void OnClick(Todo item, TodoAdapter.ViewHolder holder) {
                Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(holder.itemView,
                        (int) holder.itemView.getX(), (int) holder.itemView.getY(), (int) holder.itemView.getMeasuredWidth(), (int) holder.itemView.getMeasuredHeight());
                intent.setAction("Intent.ACTION_EDIT");
                intent.putExtra("item", item);
                startActivity(intent, optionsCompat.toBundle());
            }
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
            toolbar.getLocationInWindow(toolbarWindowLocation);

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


            circleReveal(searchToolbar, searchIconCenterX, searchIconCenterY, true);
            return true;
        }
        return false;
    }

    private void setSearchToolbar() {
        searchToolbar.inflateMenu(R.menu.search);
        searchToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        searchToolbar.setNavigationOnClickListener(v -> closeSearchToolbar());

        searchView = (SearchView) searchToolbar.getMenu().findItem(R.id.search_).getActionView();
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
                adapter.getFilter().filter(newText);
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

    }

    private void closeSearchToolbar() {
        circleReveal(searchToolbar, searchIconCenterX, searchIconCenterY, false);

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

        anim.setDuration((long) 400);

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

    private void ShowInstruction() {

        //Variable accessed from inner class
        final View[] firstTask = new View[1];
        final boolean[] isAdded = new boolean[1];
        Todo sample = new Todo();

        if (recyclerView.findViewHolderForAdapterPosition(0) == null) {
            adapter.addSampleTask();
            empty_task_animation.setVisibility(View.INVISIBLE);
            isAdded[0] = true;
        }

        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HomeActivity.this, recyclerView.findViewHolderForAdapterPosition(0) + "", Toast.LENGTH_LONG).show();
                firstTask[0] = recyclerView.findViewHolderForAdapterPosition(0).itemView;

                new TapTargetSequence(HomeActivity.this).targets(
                        TapTarget.forView(fab, "Add Task!", "Tap to add task and get started")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f),

                        TapTarget.forView(firstTask[0], "Edit Task!", "Tap on any task to edit")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f),

                        TapTarget.forToolbarMenuItem(toolbar, R.id.search, "Search", "Tap to search task")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f),

                        TapTarget.forView(firstTask[0], "Rearrange!", "Long press drag and drop to rearrange task")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f),

                        TapTarget.forToolbarMenuItem(toolbar, R.id.overflow, "More Options!", "Tap here for more options")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f),
                        TapTarget.forView(firstTask[0], "Delete!", "Swipe right on any task to delete")
                                .drawShadow(true)
                                .descriptionTextAlpha(0.7f)
                                .cancelable(false)
                                .transparentTarget(true)
                                .outerCircleColorInt(Color.parseColor("#cc00cc"))
                                .outerCircleAlpha(0.8f)
                ).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        if (isAdded[0]) {
                            Animation animation = AnimationUtils.makeOutAnimation(HomeActivity.this, true);
                            firstTask[0].setAnimation(animation);
                            animation.setDuration(600);
                            animation.start();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.removeSampleTask();
                                    empty_task_animation.setVisibility(View.VISIBLE);
                                }
                            }, animation.getDuration());
                        }
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }).start();


            }
        }, 50);
    }

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

    private void getAllId() {
        recyclerView = findViewById(R.id.todo);
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.floatingActionButton);
        empty_task_animation = findViewById(R.id.task_empty);
        searchToolbar = findViewById(R.id.search_toolbar);
    }
}