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
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.coders.TaskApp.Activity.HomeActivity;
import com.coders.TaskApp.R;
import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.Utils.TimeConverter;
import com.coders.TaskApp.models.Todo;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null)
            return;

        TaskRepository repository = new TaskRepository(context);
        Todo todo = repository.findTodoById(intent.getIntExtra("id", 0));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);

        switch (intent.getAction()) {
            case "Reminder":

                Intent contentIntent = new Intent(context, HomeActivity.class);
                contentIntent.setAction("Action/default");
                PendingIntent pendingContentIntent = PendingIntent.getActivity(context, todo.getUid(), contentIntent, PendingIntent.FLAG_ONE_SHOT);

                Intent actionComplete = new Intent(context, NotificationReceiver.class);
                actionComplete.putExtra("id", todo.getUid());
                actionComplete.setAction("Action/complete");
                PendingIntent pendingActionComplete = PendingIntent.getBroadcast(context, todo.getUid(), actionComplete, PendingIntent.FLAG_ONE_SHOT);

                Intent actionSnooze = new Intent(context, NotificationReceiver.class);
                actionSnooze.putExtra("id", todo.getUid());
                actionSnooze.setAction("Action/snooze");
                PendingIntent pendingActionSnooze = PendingIntent.getBroadcast(context, todo.getUid(), actionSnooze, PendingIntent.FLAG_ONE_SHOT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("Reminder", "Reminder"
                            , NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription("Display Notification for your tasks");
                    channel.enableLights(true);
                    channel.setBypassDnd(true);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context
                        , "Reminder");

                builder.setSmallIcon(R.mipmap.app_icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon))
                        .setShowWhen(true)
                        .setContentTitle("Your reminder for \"" + todo.getText() + "\"")
                        .setContentText(Html.escapeHtml(DateTimeFormatter.formatDate(todo.getReminder())) + " " +
                                Html.escapeHtml(DateTimeFormatter.formatTime(todo.getReminder())))
                        .setContentIntent(pendingContentIntent)
                        .addAction(R.drawable.notification_done_icon, "Completed", pendingActionComplete)
                        .addAction(R.drawable.notification_done_icon, "Snooze for 5 minutes", pendingActionSnooze)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH);

                notificationManager.notify(todo.getUid(), builder.build());


                break;
            case "Action/complete":
                notificationManager.cancel(todo.getUid());
                Toast.makeText(context, "Task marked as completed ", Toast.LENGTH_SHORT).show();
                todo.setCompleted(true);
                repository.update(todo);
                break;
            case "Action/snooze":
                notificationManager.cancel(todo.getUid());
                NotificationHelper.schedule(context, todo.getUid(), todo.getReminder() + TimeConverter.MinuteToMillis(5));
                Toast.makeText(context, "Task Snoozed for 5 minutes ", Toast.LENGTH_SHORT).show();
                break;

            default:
                Log.e("Receiver", intent.getAction());
        }
    }
}
