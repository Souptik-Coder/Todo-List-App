package com.coders.TaskApp.Adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.coders.TaskApp.R;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.models.Header;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter extends ListAdapter<RecyclerViewItem, RecyclerView.ViewHolder> implements Filterable {
    private final static DiffUtil.ItemCallback<RecyclerViewItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecyclerViewItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecyclerViewItem oldItem, @NonNull RecyclerViewItem newItem) {
            if (oldItem instanceof Todo && newItem instanceof Todo) {
                Todo oldItem1 = (Todo) oldItem;
                Todo newItem1 = (Todo) newItem;
                return oldItem1.getUid() == newItem1.getUid();
            } else if (oldItem instanceof Header && newItem instanceof Header)
                return ((Header) oldItem).title.equals(((Header) newItem).title);
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecyclerViewItem oldItem, @NonNull RecyclerViewItem newItem) {
            if (oldItem instanceof Todo && newItem instanceof Todo) {
                Todo oldItem1 = (Todo) oldItem;
                Todo newItem1 = (Todo) newItem;
                return oldItem1.getText().equals(newItem1.getText()) && oldItem1.getDueDate() == newItem1.getDueDate() && oldItem1.isCompleted() == newItem1.isCompleted()
                        && oldItem1.isTimeSet() == newItem1.isTimeSet() && oldItem1.isDateSet() == newItem1.isDateSet() && oldItem1.getNote().equals(newItem1.getNote());
            } else if (oldItem instanceof Header && newItem instanceof Header)
                return ((Header) oldItem).title.equals(((Header) newItem).title);
            return false;
        }
    };

    TodoCallback callback;
    private List<RecyclerViewItem> allTask;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RecyclerViewItem> filtered = new ArrayList<>();

            if (constraint.toString().trim().isEmpty())
                filtered.addAll(allTask);

            else {

                for (RecyclerViewItem item1 : allTask) {
                    if (item1 instanceof Todo) {
                        Todo item = (Todo) item1;
                        if (item.getText().toLowerCase().contains(constraint.toString().toLowerCase()))
                            filtered.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filtered;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            submitList((List<RecyclerViewItem>) results.values);
        }
    };


    public TodoAdapter() {
        super(DIFF_CALLBACK);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder0, int position) {

        if (holder0 instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) holder0;
            Todo item = (Todo) getItem(position);
            holder.getCheckBox().setChecked(item.isCompleted());

            if (item.isCompleted()) {
                holder.getTodoTextView().setText(Html.fromHtml
                        ("<strike>" + item.getText() + "</strike>"));
                holder.getCheckBox().setChecked(true);
            } else {
                holder.getTodoTextView()
                        .setText(item.getText());
                holder.getCheckBox().setChecked(false);
            }

            if (item.isDateSet()) {
//                holder.getDateTextView().setText(DateTimeFormatter.formatDate(item.getDueDate()));
                holder.getDateTextView().setVisibility(View.VISIBLE);
            } else
                holder.getDateTextView().setVisibility(View.GONE);

            if (item.isTimeSet()) {
                holder.getTimeTextView().setVisibility(View.VISIBLE);
            } else
                holder.getTimeTextView().setVisibility(View.GONE);

            if (!item.isDateSet() && item.isTimeSet()) {
                holder.getTimeTextView().setText(DateTimeFormatter.formatTime(item.getReminder()));
            }

            if (!item.getNote().isEmpty())
                holder.getNoteTextView().setVisibility(View.VISIBLE);
            else
                holder.getNoteTextView().setVisibility(View.GONE);
        } else {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder0;
            Header header = (Header) getItem(position);
            viewHolder.header.setText(header.title);
        }

    }

    public void setTodoCallback(TodoCallback callback) {
        this.callback = callback;
    }

    public void addSampleTask() {
        Todo sample = new Todo();
        sample.setText("Sample Task");

        ArrayList<RecyclerViewItem> currentList = new ArrayList<>(getCurrentList());
        currentList.add(0, sample);

        submitList(currentList);
    }

    public void removeSampleTask() {
        ArrayList<RecyclerViewItem> currentList = new ArrayList<>(getCurrentList());
        currentList.remove(0);

        submitList(currentList);
    }

    public interface TodoCallback {
        public void OnCheckboxClick(Todo item, boolean isChecked);

        public void OnClick(Todo item, ViewHolder holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialCheckBox checkBox;
        TextView title, date, time, note;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.TodoText);
            time = view.findViewById(R.id.time);
            date = view.findViewById(R.id.date);
            checkBox = view.findViewById(R.id.todo_checkbox);
            note = view.findViewById(R.id.note);
            if (callback != null) {
                view.setOnClickListener(v -> {
                    callback.OnClick((Todo) getItem(getAbsoluteAdapterPosition()), this);
                });

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    checkBox.setChecked(isChecked);
                    if (isChecked)
                        title.setText(Html.fromHtml
                                ("<strike>" + title.getText() + "</strike>"));
                    else
                        title.setText(title.getText().toString());

                    callback.OnCheckboxClick((Todo) getItem(getAbsoluteAdapterPosition()), isChecked);
                });
            }
        }

        public TextView getTodoTextView() {
            return title;
        }

        public TextView getNoteTextView() {
            return note;
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
        }
    }
}
