package com.coders.TaskApp.Utils;


public class TimeConverter {
    public static long HourToMillis(int hour){
        return hour*3600000;
    }

    public static long MinuteToMillis(int minute){
        return minute*60000 ;
    }
}
