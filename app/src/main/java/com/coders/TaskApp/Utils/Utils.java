package com.coders.TaskApp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Filter;

import com.coders.TaskApp.Adapter.RecyclerViewItem;
import com.coders.TaskApp.models.Header;
import com.coders.TaskApp.models.Todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Utils {
    public static List<String> weekName = Arrays.asList("", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
    private final Context context;
    private Header today, tomorrow, overdue, upcoming, completed, nodateset;

    public Utils(Context context) {
        this.context = context;
    }

    public static boolean isDateToday(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateYesterday(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == -1;
    }

    public static boolean isDateTomorrow(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == 1;
    }

    public static boolean isPastDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) < 0;
    }

    public void getFinalListAsync(List<Todo> todos, onFinalListListener finalListListener) {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                today = new Header("Today", 0);
                tomorrow = new Header("Tomorrow", 1);
                overdue = new Header("Overdue", 2);
                upcoming = new Header("Upcoming", 3);
                completed = new Header("Completed", 4);
                nodateset = new Header("No Date Set", 5);
                SharedPreferences preferences = context.getSharedPreferences("Header", Context.MODE_PRIVATE);

                today.isExpanded = preferences.getBoolean(today.id + "", true);
                tomorrow.isExpanded = preferences.getBoolean(tomorrow.id + "", true);
                overdue.isExpanded = preferences.getBoolean(overdue.id + "", true);
                upcoming.isExpanded = preferences.getBoolean(upcoming.id + "", true);
                completed.isExpanded = preferences.getBoolean(completed.id + "", true);
                nodateset.isExpanded = preferences.getBoolean(nodateset.id + "", true);

                today.clearChildItems();
                tomorrow.clearChildItems();
                upcoming.clearChildItems();
                nodateset.clearChildItems();
                overdue.clearChildItems();
                completed.clearChildItems();
                boolean todaySet = false, tomorrowSet = false, overdueSet = false, upcomingSet = false, completedSet = false, noDateSet = false;
                List<RecyclerViewItem> finalList = new ArrayList<>();
                Calendar todayCalendar = Calendar.getInstance();
                Calendar itemDate = Calendar.getInstance();
                for (Todo todo : todos) {
                    if (todo.isDateSet() && !todo.isCompleted()) {
                        itemDate.setTimeInMillis(todo.getDueDate());
                        if (itemDate.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
                            if (!todaySet)
                                finalList.add(today);
                            today.addChildItem(todo);
//                    todo.setParentHeader(this.today);
                            todaySet = true;
                        } else if (itemDate.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR) + 1) {
                            if (!tomorrowSet)
                                finalList.add(tomorrow);
//                    todo.setParentHeader(this.today);
                            tomorrow.addChildItem(todo);
                            tomorrowSet = true;
                        } else if (itemDate.get(Calendar.DAY_OF_YEAR) < todayCalendar.get(Calendar.DAY_OF_YEAR)) {
                            if (!overdueSet)
                                finalList.add(overdue);
//                    todo.setParentHeader(overdue);
                            overdue.addChildItem(todo);
                            overdueSet = true;
                        } else if (itemDate.get(Calendar.DAY_OF_YEAR) > todayCalendar.get(Calendar.DAY_OF_YEAR) + 1) {
                            if (!upcomingSet)
                                finalList.add(upcoming);
//                    todo.setParentHeader(upcoming);
                            upcoming.addChildItem(todo);
                            upcomingSet = true;
                        }
                    }

                    if (todo.isCompleted()) {
                        if (!completedSet)
                            finalList.add(completed);
//                todo.setParentHeader(completed);
                        completed.addChildItem(todo);
                        completedSet = true;
                    }

                    if (!todo.isDateSet() && !todo.isCompleted()) {
                        if (!noDateSet)
                            finalList.add(nodateset);
//                todo.setParentHeader(nodateset);
                        nodateset.addChildItem(todo);
                        noDateSet = true;
                    }
                }

                if (today.isExpanded && todaySet)
                    finalList.addAll(finalList.indexOf(today) + 1, today.getChildItems());
                if (tomorrow.isExpanded && tomorrowSet)
                    finalList.addAll(finalList.indexOf(tomorrow) + 1, tomorrow.getChildItems());
                if (completed.isExpanded && completedSet)
                    finalList.addAll(finalList.indexOf(completed) + 1, completed.getChildItems());
                if (overdue.isExpanded && overdueSet)
                    finalList.addAll(finalList.indexOf(overdue) + 1, overdue.getChildItems());
                if (nodateset.isExpanded && noDateSet)
                    finalList.addAll(finalList.indexOf(nodateset) + 1, nodateset.getChildItems());
                if (upcoming.isExpanded && upcomingSet)
                    finalList.addAll(finalList.indexOf(upcoming) + 1, upcoming.getChildItems());


                FilterResults filterResults = new FilterResults();
                filterResults.values = finalList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                finalListListener.onFinalList((List<RecyclerViewItem>) results.values);
            }
        };
        filter.filter("");
    }

    public interface onFinalListListener {
        void onFinalList(List<RecyclerViewItem> finalList);
    }
}
