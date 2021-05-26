package com.coders.TaskApp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.coders.TaskApp.models.Todo;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                , "Reminder");

        Todo item=(Todo) intent.getSerializableExtra("item");

        Log.e("kk",intent.hasExtra("item")+"");

        builder.setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon))
                .setShowWhen(true)
                .setContentTitle(item==null?"Sample Task":item.getText())
//                .setContentText(new FormatDate(item.getMillis()).format())
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Reminder", "Reminder"
                    , NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);

        }
        notificationManager.notify(item.getNid(), builder.build());


    }
}
