package com.coders.TaskApp.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.coders.TaskApp.Activity.HomeActivity;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.models.Todo;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent contentIntent=new Intent(context, HomeActivity.class);
        PendingIntent pendingContentIntent=PendingIntent.getActivity(context,intent.getIntExtra("id",0),contentIntent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                , "Reminder");

        builder.setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon))
                .setShowWhen(true)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(DateTimeFormatter.formatDate(intent.getLongExtra("reminder",0))+
                        DateTimeFormatter.formatTime(intent.getLongExtra("reminder",0)))
                .setContentIntent(pendingContentIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Reminder", "Reminder"
                    , NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(intent.getIntExtra("id",0), builder.build());


    }
}
