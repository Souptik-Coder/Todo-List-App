package com.coders.TaskApp.Activity;

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
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Adapter.RecyclerViewItem;
import com.coders.TaskApp.Adapter.TodoAdapter;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Utils.TodoTouchHelperCallback;
import com.coders.TaskApp.ViewModel.HomeActivityViewModel;
import com.coders.TaskApp.models.Header;
import com.coders.TaskApp.models.Todo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    SearchView search;
    RecyclerView recyclerView;
    TodoAdapter adapter;
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
        adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new TodoTouchHelperCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                        ItemTouchHelper.RIGHT, adapter, this, findViewById(R.id.root)));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        viewModel.getAllTask().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                if (FirstTime) return;
                if (todos.size() == 0) {
                    empty_task_animation.setVisibility(View.VISIBLE);
                } else {
                    empty_task_animation.setVisibility(View.INVISIBLE);
                }
                adapter.submitList(getFinalList(todos));
            }
        });


        fab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(fab, (int) fab.getX(), (int) fab.getY(), fab.getMeasuredWidth(), fab.getMeasuredHeight());
            startActivity(intent, optionsCompat.toBundle());
        });


        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.sort) {
                return true;
            } else if (itemId == R.id.showInstruction) {
                ShowInstruction();
                return true;
            }
            return false;
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

    private List<RecyclerViewItem> getFinalList(List<Todo> todos) {
        boolean todaySet = false, tomorrowSet = false, overdueSet = false, upcomingSet = false, completedSet = false, noDateSet = false;
        List<RecyclerViewItem> finalList = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        Calendar itemDate = Calendar.getInstance();
        for (Todo todo : todos) {
            if (todo.isDateSet() && !todo.isCompleted()) {
                itemDate.setTimeInMillis(todo.getDueDate());
                if (itemDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    if (!todaySet)
                        finalList.add(new Header("Today"));
                    finalList.add(todo);
                    todaySet = true;
                } else if (itemDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) + 1) {
                    if (!tomorrowSet)
                        finalList.add(new Header("Tomorrow"));
                    finalList.add(todo);
                    tomorrowSet = true;
                } else if (itemDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR)) {
                    if (!overdueSet)
                        finalList.add(new Header("Overdue"));
                    finalList.add(todo);
                    overdueSet = true;
                } else if (itemDate.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR) + 1) {
                    if (!upcomingSet)
                        finalList.add(new Header("Upcoming"));
                    finalList.add(todo);
                    upcomingSet = true;
                }
            }

            if (todo.isCompleted()) {
                if (!completedSet)
                    finalList.add(new Header("Completed"));
                finalList.add(todo);
                completedSet = true;
            }

            if (!todo.isDateSet() && !todo.isCompleted()) {
                if (!noDateSet)
                    finalList.add(new Header("No Date Set"));
                finalList.add(todo);
                noDateSet = true;
            }
        }
        return finalList;
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
        fab = findViewById(R.id.floatingActionButton);
        empty_task_animation = findViewById(R.id.task_empty);
    }
}