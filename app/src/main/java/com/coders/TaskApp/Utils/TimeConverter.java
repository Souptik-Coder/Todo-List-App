package com.coders.TaskApp.Utils;

import java.text.SimpleDateFormat;

public class TimeConverter {
    public static long HourToMillis(int hour){
        return hour*3600000;
    }

    public static long MinuteToMillis(int hour){
        return hour*60000 ;
    }
}
