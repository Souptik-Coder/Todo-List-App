package com.coders.TaskApp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.coders.TaskApp.Utils.ScheduleNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class TodoData {
    public static final String Task = "task";
    public static final String isChecked = "Checked";
    public static final String Reminder = "Reminder";
    public static final String Due_Date = "DueDate";
    public static final String Request_Code = "requestCode";
    public static final String Item = "item";
    public static final String Position = "pos";

    public static ArrayList<String> data = new ArrayList<String>();
    static List<String> monthName = Arrays.asList("Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec");
    static List<String> weekName = Arrays.asList("", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
    Context context;

    public TodoData(Context context) {
        this.context = context;
    }

    public static String formatDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.DAY_OF_YEAR) - date.get(Calendar.DAY_OF_YEAR) == 0)
            return "Today";
        else if (date.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) == 1)
            return "Tomorrow";
        else if (date.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) == -1)
            return "<span style=\"color:red\">Yesterday</span>";
        else if (date.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) < 0)
            return "<span style=\"color:red\">" + weekName.get(date.get(Calendar.DAY_OF_WEEK)) + "," + day + " " + monthName.get(month) + "</span>";
        return weekName.get(date.get(Calendar.DAY_OF_WEEK)) + "," + day + " " + monthName.get(month);
    }

    public static String formatTime(int hour, int minute) {
        String hrs, mins, suffix = "AM";
        if (hour > 12) {
            hrs = Integer.toString(hour - 12);
            suffix = "PM";

        } else
            hrs = Integer.toString(hour);
        if (hour == 12)
            suffix = "PM";
        mins = Integer.toString(minute);

        if (hrs.length() == 1)
            hrs = "0" + hrs;
        if (mins.length() == 1)
            mins = "0" + mins;

        return hrs + ":" + mins + " " + suffix;

    }

    public static JSONObject builder(String task, JSONObject DueDate, JSONObject Reminder) {
        JSONObject item = new JSONObject();

        try {
            item.put(TodoData.Task, task);
            item.put(TodoData.isChecked, false);
            item.put(TodoData.Due_Date, DueDate);
            item.put(TodoData.Reminder, Reminder);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    void add(String task, JSONObject DueDate, JSONObject Reminder) {
        //Position to add new task
        int pos = 0;
        data.add(pos, builder(task, DueDate, Reminder).toString());
        writeData();
    }

    void add(String item, int pos) {
        data.add(pos, item);
    }

    void remove(int pos) {

        JSONObject item = null;
        try {
            item = new JSONObject(data.get(pos));
            if (item.has(TodoData.Reminder))
                ScheduleNotification.cancel(context, item.getJSONObject(TodoData.Reminder).getInt(TodoData.Request_Code));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        data.remove(pos);
        writeData();
    }

    /*Replace item at pos with new item*/
    void replace(String task, JSONObject DueDate, JSONObject Reminder, boolean checked, int pos) {
        JSONObject item = new JSONObject();

        try {
            item.put(TodoData.Task, task);
            item.put(TodoData.isChecked, checked);
            item.put(TodoData.Due_Date, DueDate);
            item.put(TodoData.Reminder, Reminder);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        replace(item.toString(), pos);
    }

    /*Replace item at pos with new item*/
    void replace(String item, int pos) {
        Log.e("Error at replace", pos + " size=" + data.size());
        data.remove(pos);
        data.add(pos, item);
        writeData();
    }

    void move(int pos1, int pos2) {
        String item = data.get(pos1);
        data.remove(pos1);
        data.add(pos2, item);
        writeData();
    }


    /*Writes data to internal storage*/
    void writeData() {
        try {
            FileOutputStream writer = context.openFileOutput("com.TodoApp.Data.txt", Context.MODE_PRIVATE);
            String json = "";
            for (String item : data) {
                json += item + "#!";
            }

            writer.write(json.getBytes());
            writer.flush();
            writer.close();

        } catch (Exception e) {
            Toast.makeText(context, "Error at TodoData.writeData " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /*Reads data from internal storage*/
    void readData() {
        String mdata = "";

        try {
            FileInputStream inputStream = context.openFileInput("com.TodoApp.Data.txt");
            int receive;
            while ((receive = inputStream.read()) != -1) {
                mdata += (char) receive;
            }
            inputStream.close();

            if (mdata.trim().isEmpty()) return;
            data = new ArrayList<>(Arrays.asList(mdata.split("#!")));
        } catch (Exception e) {
//            Toast.makeText(context, "Error at TodoData.readData " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    /*Sort items by date and time*/
    void sort() {

        data.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    JSONObject ob1 = new JSONObject(o1);
                    JSONObject ob2 = new JSONObject(o2);

                    if (ob1.has("DueDate") && !ob2.has("DueDate"))
                        return -1;
                    else if (!ob1.has("DueDate") && ob2.has("DueDate"))
                        return 1;
                    else {
                        Calendar o1Date, o2Date;
                        o1Date = Calendar.getInstance();
                        o2Date = Calendar.getInstance();

                        o1Date.set(ob1.getJSONObject("DueDate").getInt("year"), ob1.getJSONObject("DueDate").getInt("month"),
                                ob1.getJSONObject("DueDate").getInt("day"));
                        o2Date.set(ob2.getJSONObject("DueDate").getInt("year"), ob2.getJSONObject("DueDate").getInt("month"),
                                ob2.getJSONObject("DueDate").getInt("day"));

                        int difference = o1Date.get(Calendar.DAY_OF_YEAR) - o2Date.get(Calendar.DAY_OF_YEAR);
                        if (difference == 0 && (ob1.has("Reminder") && ob2.has("Reminder"))) {

                            if (ob1.getJSONObject("Reminder").getInt("hour") == ob2.getJSONObject("Reminder").getInt("hour"))
                                return ob1.getJSONObject("Reminder").getInt("minute") - ob2.getJSONObject("Reminder").getInt("minute");
                            else
                                return ob1.getJSONObject("Reminder").getInt("hour") - ob2.getJSONObject("Reminder").getInt("hour");
                        } else
                            return difference;
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return 0;
            }
        });
    }


/*EXPERIMENTAL*/
//
//    public void insertTodo(Todo todo){
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TodoRoomDatabase.getInstance(context).getTodoDao()
//                        .insertTodo(todo);
//            }
//        });
//
//        thread.start();
//    }
//
//    public void getAllTodo(){
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                TodoRoomDatabase.getInstance(context).getTodoDao()
//                        .getAllTodos();
//            }
//        });
//
//        thread.start();
//    }
//
//    public Todo findTodoById(int uid){
//        final Todo[] todo = new Todo[1];
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                todo[0] = TodoRoomDatabase.getInstance(context).getTodoDao()
//                       .findTodoById(uid);
//            }
//        });
//
//         thread.start();
//         return todo[0];
//    }
}


