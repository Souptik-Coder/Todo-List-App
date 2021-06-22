package com.coders.TaskApp.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.coders.TaskApp.models.Todo;
import com.coders.TaskApp.NotificationReceiver;

public class ScheduleNotification {

    public static void schedule(Context context, int notification_Id, long millis, Todo item){
//        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent=new Intent(context, NotificationReceiver.class);
//        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,notification_Id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//       alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,millis,pendingIntent);
    }
    public static void cancel(Context context,int requestCode) {
//        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent=new Intent(context,NotificationReceiver.class);
//        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        alarmManager.cancel(pendingIntent);
    }
}
