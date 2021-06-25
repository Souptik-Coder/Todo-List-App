package com.coders.TaskApp.Utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateTimeFormatter {
    public static String formatDate(long millis) {
        if (Utils.isDateToday(millis))
            return "Today";
        else if (Utils.isDateYesterday(millis))
            return "<span style=\"color:red\">Yesterday</span>";
        else if (Utils.isDateTomorrow(millis))
            return "Tomorrow";

        SimpleDateFormat dateFormat = new SimpleDateFormat("E,dd MMMM", Locale.getDefault());
        if (Utils.isPastDate(millis))
            return "<span style=\"color:red\">" + dateFormat.format(millis) + "</span>";
        else
            return dateFormat.format(millis);
    }

    public static String formatTime(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        if(millis<System.currentTimeMillis())
            return "<span style=\"color:red\">" + dateFormat.format(millis) + "</span>";
        else
        return dateFormat.format(millis);
    }
}
