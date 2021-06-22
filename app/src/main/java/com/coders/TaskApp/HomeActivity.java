package com.coders.TaskApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Utils.Constants;
import com.coders.TaskApp.models.Todo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    ChipGroup filter;
    MaterialToolbar toolbar;
    SearchView search;
    RecyclerView recyclerView;
    CustomAdapter adapter;
    FloatingActionButton fab;
    HomeActivityViewModel viewModel;
    LinearLayout empty_task_animation;
    boolean FirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllId();
        setSupportActionBar(toolbar);
        adapter = new CustomAdapter(empty_task_animation);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new TodoTouchHelperCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                        ItemTouchHelper.RIGHT, adapter, this, findViewById(R.id.constraint)));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        viewModel.getAllTask().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                if (FirstTime) return;
                adapter.setAllTask(todos);
                adapter.getFilter().filter(viewModel.getSearchQuery());
                int id = filter.getCheckedChipId();

                if (id == R.id.all)
                    adapter.getCustomFilter().filter(Constants.ALL);
                else if (id == R.id.today)
                    adapter.getCustomFilter().filter(Constants.TODAY);
                else if (id == R.id.tomorrow)
                    adapter.getCustomFilter().filter(Constants.TOMORROW);
                else if (id == R.id.completed)
                    adapter.getCustomFilter().filter(Constants.COMPLETED);
            }
        });


        filter.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId == R.id.all) {
                    adapter.getCustomFilter().filter(Constants.ALL);
                } else if (checkedId == R.id.today) {
                    adapter.getCustomFilter().filter(Constants.TODAY);
                } else if (checkedId == R.id.tomorrow) {
                    adapter.getCustomFilter().filter(Constants.TOMORROW);

                } else if (checkedId == R.id.completed) {
                    adapter.getCustomFilter().filter(Constants.COMPLETED);

                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(fab, 0, 0, 0, 0);
                startActivity(intent, optionsCompat.toBundle());
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.sort) {
                    return true;
                } else if (itemId == R.id.showInstruction) {
                    ShowInstruction();
                    return true;
                }
                return false;
            }
        });


        if (isFirstTime()) {
            FirstTime = true;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ShowInstruction();
                }
            }, 50);

        }

        adapter.setTodoCallback(new CustomAdapter.TodoCallback() {
            @Override
            public void OnCheckboxClick(Todo item, boolean isChecked) {
                item.setCompleted(isChecked);
                viewModel.update(item);
            }

            @Override
            public void OnClick(Todo item, CustomAdapter.ViewHolder holder) {
                Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.putExtra("item", item);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this, holder.itemView, "shared");
                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem itemSearch = menu.findItem(R.id.search);
        search = (SearchView) itemSearch.getActionView();
        search.setQueryHint("Search...");
        search.setMaxWidth(Integer.MAX_VALUE);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                viewModel.setSearchQuery(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

//        search.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (!search.isIconified()) {
//////                    lottieAnimationView.pauseAnimation();
////
////                    appBarLayout.setExpanded(false, true);
////                } else {
//////                    Toast.makeText(MainActivity.this, "Close", Toast.LENGTH_SHORT).show();
//////                    lottieAnimationView.playAnimation();
////
////                    appBarLayout.setExpanded(true, true);
//                }
//            }
//        });
//        return true;


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
        filter = findViewById(R.id.chip_group);
        fab = findViewById(R.id.floatingActionButton);
        empty_task_animation = findViewById(R.id.task_empty);
    }
}