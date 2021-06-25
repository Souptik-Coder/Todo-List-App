package com.coders.TaskApp.Utils;


import java.util.Calendar;

public class TimeConverter {
    public static long HourToMillis(int hour) {
        return hour * 3600000;
    }

    public static long MinuteToMillis(int minute) {
        return minute * 60000;
    }

    public static int MillisToHour(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int MillisToMinute(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.get(Calendar.MINUTE);
    }

}
