package com.coders.TaskApp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.models.Todo;

import java.util.ArrayList;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TaskRepository repository = TaskRepository.getInstance(context);
        Todo boot = new Todo();
        boot.setText("Boot on " + DateTimeFormatter.formatDate(System.currentTimeMillis()) + DateTimeFormatter.formatTime(System.currentTimeMillis()));
        repository.insert(boot);
        List<Todo> todos = new ArrayList<>(repository.getAllTask().getValue());
        for (Todo todo : todos) {
            if (todo.isTimeSet()) {
                NotificationHelper.schedule(context, todo.getNid(), todo.getReminder());
            }
        }
    }
}
