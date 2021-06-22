package com.coders.TaskApp;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.Utils.Constants;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomAdapter extends ListAdapter<Todo, CustomAdapter.ViewHolder> implements Filterable {
    private final static DiffUtil.ItemCallback<Todo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getText().equals(newItem.getText()) && oldItem.getDueDate() == newItem.getDueDate()
                    && oldItem.isTimeSet() == newItem.isTimeSet() && oldItem.isDateSet() == newItem.isDateSet();
        }
    };
    private List<Todo> allTask;
    LinearLayout emptyAnimation;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Todo> filtered = new ArrayList<>();

            if (constraint.toString().trim().isEmpty())
                filtered.addAll(allTask);

            else {

                for (Todo item : allTask) {
                    if (item.getText().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filtered.add(item);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            submitList((List<Todo>) results.values);
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    public CustomAdapter(LinearLayout emptyAnimation) {
        super(DIFF_CALLBACK);
        this.emptyAnimation = emptyAnimation;
    }


    public void setAllTask(List<Todo> allTask) {
        this.allTask = allTask;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        int count = getCurrentList().size();
        if (count == 0)
            emptyAnimation.setVisibility(View.VISIBLE);
        else
            emptyAnimation.setVisibility(View.INVISIBLE);
        return count;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Todo item = getItem(position);
        holder.getCheckBox().setChecked(item.isCompleted());

        if (item.isCompleted()) {
            holder.getTodoTextView().setText(Html.fromHtml
                    ("<strike>" + item.getText() + "</strike>",
                            Html.FROM_HTML_OPTION_USE_CSS_COLORS));
            holder.getCheckBox().setChecked(true);
        } else {
            holder.getTodoTextView()
                    .setText(item.getText());
            holder.getCheckBox().setChecked(false);
        }

        if (item.isDateSet()) {
            holder.getDateTextView().setText(DateTimeFormatter.formatDate(item.getDueDate()));
            holder.getDateTextView().setVisibility(View.VISIBLE);
        } else
            holder.getDateTextView().setVisibility(View.GONE);

        if (item.isTimeSet()) {
//            holder.getTimeTextView().setText(new FormatTime(item.getMillis()).format());
            holder.getDateTextView().setVisibility(View.VISIBLE);
        } else
            holder.getTimeTextView().setVisibility(View.GONE);
    }

    public void setTodoCallback(TodoCallback callback) {
        this.callback = callback;
    }

    interface TodoCallback {
        public void OnCheckboxClick(Todo item, boolean isChecked);

        public void OnClick(Todo item, ViewHolder holder);
    }

    TodoCallback callback;

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialCheckBox checkBox;
        TextView title, date, time;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.TodoText);
            time = view.findViewById(R.id.time);
            date = view.findViewById(R.id.date);
            checkBox = view.findViewById(R.id.todo_checkbox);
            if (callback != null) {
                view.setOnClickListener(v -> {
                    callback.OnClick(getItem(getAdapterPosition()), this);
                });

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    checkBox.setChecked(isChecked);
                    if (isChecked)
                        title.setText(Html.fromHtml
                                ("<strike>" + title.getText() + "</strike>",
                                        Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    else
                        title.setText(title.getText().toString());

                    callback.OnCheckboxClick(getItem(getAdapterPosition()), isChecked);
                });
            }
        }

        public TextView getTodoTextView() {
            return title;
        }

        public TextView getDateTextView() {
            return date;
        }

        public TextView getTimeTextView() {
            return time;
        }

        public MaterialCheckBox getCheckBox() {
            return checkBox;
        }

    }

    public void addSampleTask() {
        Todo sample = new Todo();
        sample.setText("Sample Task");

        ArrayList<Todo> currentList = new ArrayList<>(getCurrentList());
        currentList.add(0, sample);

        submitList(currentList);
    }

    public void removeSampleTask() {
        ArrayList<Todo> currentList = new ArrayList<>(getCurrentList());
        currentList.remove(0);

        submitList(currentList);
    }

//    public void showAll() {
//        submitList(allTask);
//    }
//
//    public void showToday() {
//        List<Todo> newList = new ArrayList<Todo>();
//        Calendar today = Calendar.getInstance();
//        for (Todo item : allTask) {
//            Calendar date = Calendar.getInstance();
//            date.setTimeInMillis(item.getMillis());
//
//            if (today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
//                newList.add(item);
//        }
//        submitList(newList);
//    }

//    public void showTomorrow() {
//        List<Todo> newList = new ArrayList<Todo>();
//        Calendar today = Calendar.getInstance();
//        for (Todo item : allTask) {
//            Calendar date = Calendar.getInstance();
//            date.setTimeInMillis(item.getMillis());
//
//            if (today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) + 1)
//                newList.add(item);
//        }
//        submitList(newList);
//    }

//    public void showCompleted() {
//        List<Todo> newList = new ArrayList<Todo>();
//        for (Todo item : allTask) {
//            if (item.isCompleted())
//                newList.add(item);
//        }
//        submitList(newList);
//    }


    Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Todo> filtered = new ArrayList<>();

            if (constraint.equals(Constants.ALL))
                filtered.addAll(allTask);

            else if (constraint.equals(Constants.COMPLETED)) {
                for (Todo item : allTask) {
                    if (item.isCompleted())
                        filtered.add(item);
                }
            } else if (constraint.equals(Constants.TODAY)) {
                Calendar today = Calendar.getInstance();
                Calendar date = Calendar.getInstance();
                for (Todo item : allTask) {
                    date.setTimeInMillis(item.getDueDate());

                    if (today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR))
                        filtered.add(item);
                }
            } else if (constraint.equals(Constants.TOMORROW)) {
                Calendar today = Calendar.getInstance();
                Calendar date = Calendar.getInstance();
                for (Todo item : allTask) {
                    date.setTimeInMillis(item.getDueDate());

                    if (today.get(Calendar.DAY_OF_YEAR) + 1 == date.get(Calendar.DAY_OF_YEAR))
                        filtered.add(item);
                }
            }


            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            submitList((List<Todo>) results.values);
        }
    };

    public Filter getCustomFilter() {
        return customFilter;
    }
}
