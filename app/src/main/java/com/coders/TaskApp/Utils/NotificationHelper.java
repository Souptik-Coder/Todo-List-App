package com.coders.TaskApp.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.coders.TaskApp.Receiver.NotificationReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    public static void schedule(Context context, int notification_Id,String title, long reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("id", notification_Id);
        intent.putExtra("title", title);
        intent.putExtra("reminder", reminder);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notification_Id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder, pendingIntent);
    }

    public static void cancel(Context context, int notification_Id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notification_Id, intent, PendingIntent.FLAG_NO_CREATE);

        if(pendingIntent!=null)
        alarmManager.cancel(pendingIntent);
    }

    public static int generateID(){
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss").format(now));
    }
}
