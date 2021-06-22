package com.coders.TaskApp.Utils;

import java.text.SimpleDateFormat;

public class DateTimeFormatter {
    public static String formatDate(long millis){
        SimpleDateFormat dateFormat=new SimpleDateFormat("E,dd MMMM");
        return dateFormat.format(millis);
    }
    public static String formatTime(long millis){
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
        return dateFormat.format(millis);
    }
}
