package com.coders.TaskApp.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.coders.TaskApp.Activity.HomeActivity;
import com.coders.TaskApp.Receiver.NotificationReceiver;

public class NotificationHelper {

    public static void schedule(Context context, int id, long reminder) {
        if (reminder < System.currentTimeMillis())
            return;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("id", id);
        intent.setAction("Reminder");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setPackage("com.coders.TaskApp.Receiver");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent showIntent = new Intent(context, HomeActivity.class);
        PendingIntent pendingShowIntent = PendingIntent.getActivity(context, 0, showIntent, 0);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(reminder, pendingShowIntent);
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
    }

    public static void cancel(Context context, int notification_Id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notification_Id, intent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null)
            alarmManager.cancel(pendingIntent);
    }

    public static int generateID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("ID", Context.MODE_PRIVATE);
        int id = preferences.getInt("Id", 0) + 1;
        preferences.edit().putInt("Id", id).commit();
        return id;
    }
}
