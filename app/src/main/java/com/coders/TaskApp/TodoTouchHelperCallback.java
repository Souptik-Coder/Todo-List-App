package com.coders.TaskApp;

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

import com.coders.TaskApp.models.Todo;
import com.google.android.material.snackbar.Snackbar;

public class TodoTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    CustomAdapter adapter;
    Context context;
    View parent;
    TaskRepository repository;

    public TodoTouchHelperCallback(int dragDirs, int swipeDirs, CustomAdapter adapter, Context context, View parent) {
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            Drawable d = ContextCompat.getDrawable(context, R.drawable.delete_icon);
            Paint rect = new Paint(Paint.ANTI_ALIAS_FLAG);
            rect.setColor(Color.BLACK);
            rect.setAlpha(50);

            View item = viewHolder.itemView;// item currently getting scrolled
            int left = item.getLeft(), right = item.getRight(), top = item.getTop(), bottom = item.getBottom(), height = item.getHeight();
            c.drawRect(left, top, left + ((int) dX) + 20, bottom, rect);


            int iconMargin = (height - d.getIntrinsicHeight()) / 2;
            int iconTop = top + iconMargin;
            int iconLeft = (left + (int) dX) - d.getIntrinsicWidth() - 80;
            int iconBottom = bottom - iconMargin;
            int iconRight = iconLeft + d.getIntrinsicWidth();
            int iconHeight = d.getIntrinsicHeight();
            int iconWidth = d.getIntrinsicWidth();
            int iconCentre = iconLeft + (iconWidth / 2);

            if (left + ((int) dX) > iconWidth + (height / 2) - 25) {

                Paint circle = new Paint(Paint.ANTI_ALIAS_FLAG);
                circle.setColor(Color.RED);

                int radius = (height / 2) - 25;

                if (iconCentre + radius > left + ((int) dX))
                    radius = left + ((int) dX) - iconCentre - 5;

                c.drawCircle(iconLeft + (iconWidth / 2), iconTop + (iconHeight / 2), radius, circle);
                d.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                d.draw(c);
            }
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {

            int position = viewHolder.getAdapterPosition();
            Todo item = adapter.getCurrentList().get(position);
            repository.delete(item);

            Snackbar snackbar = Snackbar.make(parent, "Task deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repository.insert(item);
                }
            });
            snackbar.show();
        }
    }
}
