package com.coders.TaskApp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Activity.AddTodoActivity;
import com.coders.TaskApp.Adapter.TodoAdapter;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.Utils.TodoTouchHelperCallback;
import com.coders.TaskApp.Utils.Utils;
import com.coders.TaskApp.ViewModel.HomeActivitySharedViewModel;
import com.coders.TaskApp.databinding.FragmentHomeBinding;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    TodoAdapter adapter;
    HomeActivitySharedViewModel viewModel;
    boolean isFirstTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivitySharedViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater);
        adapter = new TodoAdapter(getContext(), binding.emptyTaskAnimation);
        binding.recyclerView.setAdapter(adapter);
        TodoTouchHelperCallback todoTouchHelperCallback = new TodoTouchHelperCallback(0,
                ItemTouchHelper.RIGHT, adapter, getContext());
        todoTouchHelperCallback.setSwipeListener(new OnTodoSwipeListener());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(todoTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getAllTask().observe(getViewLifecycleOwner(), this::OnListChanged);
        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), s -> adapter.getFilter().filter(s));
        binding.floatingActionButton.setOnClickListener(new OnFabClick());
        adapter.setTodoCallback(new OnTodoCallback());
        return binding.getRoot();
    }

    private void OnListChanged(List<Todo> todos) {
        if (isFirstTime && todos.size() == 0) return;
        new Utils(getContext()).getFinalListAsync(todos, finalList -> {
            adapter.setAllTask(finalList);
            adapter.getFilter().filter(viewModel.getSearchQuery().getValue());
        });
    }

    private class OnTodoSwipeListener implements TodoTouchHelperCallback.OnSwipeListener {

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof TodoAdapter.ViewHolder && direction == ItemTouchHelper.RIGHT) {

                int position = viewHolder.getAbsoluteAdapterPosition();
                Todo item = (Todo) adapter.getCurrentList().get(position);
                viewModel.delete(item);
                Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, "Task deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", v -> viewModel.insert(item));
                snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        NotificationHelper.cancel(requireContext(), item.getUid());
                    }
                });
                snackbar.show();
            }
        }
    }

    private class OnFabClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), AddTodoActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(
                    binding.floatingActionButton, (int) binding.floatingActionButton.getX(), (int) binding.floatingActionButton.getY(), binding.floatingActionButton.getMeasuredWidth(), binding.floatingActionButton.getMeasuredHeight());
            startActivity(intent, optionsCompat.toBundle());
        }
    }

    private class OnTodoCallback implements TodoAdapter.TodoCallback {
        @Override
        public void OnCheckboxClick(Todo item, boolean isChecked) {
            item.setCompleted(isChecked);
            viewModel.update(item);
        }

        @Override
        public void OnClick(Todo item, TodoAdapter.ViewHolder holder) {
            Intent intent = new Intent(getContext(), AddTodoActivity.class);
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(holder.itemView,
                    (int) holder.itemView.getX(), (int) holder.itemView.getY(), holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            intent.setAction("Intent.ACTION_EDIT");
            intent.putExtra("item", item);
            startActivity(intent, optionsCompat.toBundle());
        }
    }
}