package com.coders.TaskApp.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Adapter.TodoAdapter;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class TodoTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    TodoAdapter adapter;
    Context context;
    View parent;
    TaskRepository repository;

    public TodoTouchHelperCallback(int dragDirs, int swipeDirs, TodoAdapter adapter, Context context, View parent) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
        this.context = context;
        this.parent = parent;
        this.repository = new TaskRepository(context);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof TodoAdapter.ViewHolder && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Drawable d = ContextCompat.getDrawable(context, R.drawable.delete_icon);
            View item = viewHolder.itemView;// item currently getting scrolled
            int left = item.getLeft(), right = item.getRight(), top = item.getTop(), bottom = item.getBottom(), height = item.getHeight();
            Paint rect = new Paint(Paint.ANTI_ALIAS_FLAG);
            rect.setColor(Color.RED);
            c.drawRect(left, top, left + ((int) dX) + 20, bottom, rect);


            int iconMargin = (height - d.getIntrinsicHeight()) / 2;
            int iconTop = top + iconMargin;
            int iconLeft = (left + (int) dX) - d.getIntrinsicWidth() - 80;
            int iconBottom = bottom - iconMargin;
            int iconRight = iconLeft + d.getIntrinsicWidth();
            int iconHeight = d.getIntrinsicHeight();
            int iconWidth = d.getIntrinsicWidth();
            int iconCentre = iconLeft + (iconWidth / 2);

            if (left + ((int) dX) > left +iconWidth +iconMargin+20) {
                d.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                d.draw(c);
            }
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder instanceof TodoAdapter.ViewHolder && direction == ItemTouchHelper.RIGHT) {

            int position = viewHolder.getAbsoluteAdapterPosition();
            Todo item = (Todo) adapter.getCurrentList().get(position);
            repository.delete(item);
            Snackbar snackbar = Snackbar.make(parent, "Task deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", v -> repository.insert(item));
            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    NotificationHelper.cancel(context,item.getNid());
                }
            });
            snackbar.show();
        }
    }
}
