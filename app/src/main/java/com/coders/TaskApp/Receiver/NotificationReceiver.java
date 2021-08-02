package com.coders.TaskApp.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.coders.TaskApp.Activity.HomeActivity;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.Utils.TimeConverter;
import com.coders.TaskApp.models.Todo;
import com.muddzdev.styleabletoast.StyleableToast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("Receiver", "Received " + intent.getIntExtra("id", 0));

        if (intent.getAction() == null)
            return;

        TaskRepository repository = TaskRepository.getInstance(context);
        Todo todo = repository.findTodoByNotificationId(intent.getIntExtra("id", 0));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        switch (intent.getAction()) {
            case "Reminder":

                Intent contentIntent = new Intent(context, HomeActivity.class);
                contentIntent.setAction("Action/default");
                PendingIntent pendingContentIntent = PendingIntent.getActivity(context, NotificationHelper.generateID(context), contentIntent, PendingIntent.FLAG_ONE_SHOT);

                Intent actionComplete = new Intent(context, NotificationReceiver.class);
                actionComplete.putExtra("id", todo.getNid());
                actionComplete.setAction("Action/complete");
                PendingIntent pendingActionComplete = PendingIntent.getBroadcast(context, NotificationHelper.generateID(context), actionComplete, PendingIntent.FLAG_ONE_SHOT);

                Intent actionSnooze = new Intent(context, NotificationReceiver.class);
                actionSnooze.putExtra("id", todo.getNid());
                actionSnooze.setAction("Action/snooze");
                PendingIntent pendingActionSnooze = PendingIntent.getBroadcast(context, NotificationHelper.generateID(context), actionSnooze, PendingIntent.FLAG_ONE_SHOT);

                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("Reminder", "Reminder"
                            , NotificationManager.IMPORTANCE_HIGH);
                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    channel.setDescription("Display Notification for your tasks");
                    channel.enableLights(true);
                    channel.setBypassDnd(true);
                    channel.setSound(alarmUri, audioAttributes);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                    notificationManager.createNotificationChannel(channel);
                }

                String message = "Title- " + todo.getText() + "\n";
                if (todo.isDateSet())
                    message += "Due- " + Html.fromHtml(DateTimeFormatter.formatDate(todo.getDueDate())) + "\n";

                if (!todo.getNote().isEmpty())
                    message += "Note- " + todo.getNote() + "\n";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                        , "Reminder");

                message += "Tap to open";
                builder.setSmallIcon(R.mipmap.app_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon3))
                        .setShowWhen(true)
                        .setContentTitle("Your reminder for \"" + todo.getText() + "\"")
                        .setContentText("Tap to open")
                        .setContentIntent(pendingContentIntent)
                        .setSound(alarmUri)
                        .addAction(R.drawable.notification_done_icon, "Completed", pendingActionComplete)
                        .addAction(R.drawable.notification_done_icon, "Snooze for 5 minutes", pendingActionSnooze)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(Notification.PRIORITY_HIGH);

                notificationManager.notify(todo.getNid(), builder.build());

                break;
            case "Action/complete":
                notificationManager.cancel(todo.getNid());
                new StyleableToast
                        .Builder(context)
                        .text("Task marked as completed")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        .show();
                todo.setCompleted(true);
                repository.update(todo);
                break;
            case "Action/snooze":
                notificationManager.cancel(todo.getNid());
                NotificationHelper.schedule(context, todo.getNid(), System.currentTimeMillis() + TimeConverter.MinuteToMillis(5));
                new StyleableToast
                        .Builder(context)
                        .text("Task snoozed for 5 minutes")
                        .textColor(Color.WHITE)
                        .backgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        .show();
                break;

            default:
                Log.e("Receiver", intent.getAction());
        }
    }
}
