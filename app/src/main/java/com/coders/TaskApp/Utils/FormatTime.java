package com.coders.TaskApp.Utils;

import java.text.SimpleDateFormat;

public class FormatTime {

    private final long millis;

    public FormatTime(long millis){
        this.millis=millis;
    }

    public String format(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
        return dateFormat.format(millis);
    }
}
