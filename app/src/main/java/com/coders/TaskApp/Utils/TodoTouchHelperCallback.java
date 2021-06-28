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

public class TodoTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    TodoAdapter adapter;
    Context context;
    TaskRepository repository;
    OnSwipeListener onSwipeListener;

    public TodoTouchHelperCallback(int dragDirs, int swipeDirs, TodoAdapter adapter, Context context) {
        super(dragDirs, swipeDirs);
        this.adapter = adapter;
        this.context = context;
        this.repository = new TaskRepository(context);
    }

    public void setSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
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

            if (left + ((int) dX) > left + iconWidth + iconMargin + 20) {
                d.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                d.draw(c);
            }
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (onSwipeListener != null)
            onSwipeListener.onSwiped(viewHolder, direction);
    }

    public interface OnSwipeListener {
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction);
    }
}
