package com.coders.TaskApp.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatDate {
    private final long millis;

    public FormatDate(long millis){
        this.millis=millis;
    }
    public String format(){
       SimpleDateFormat dateFormat=new SimpleDateFormat("E,dd MMMM");
       return dateFormat.format(millis);
    }
}
