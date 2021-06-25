package com.coders.TaskApp.Utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Utils {
    public static List<String> weekName = Arrays.asList("", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

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
        return calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR) <0;
    }
}
