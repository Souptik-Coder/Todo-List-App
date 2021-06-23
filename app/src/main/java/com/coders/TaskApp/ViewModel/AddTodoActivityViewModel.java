package com.coders.TaskApp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.Utils.NotificationHelper;
import com.coders.TaskApp.models.Todo;

public class AddTodoActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<String> task;
    private final MutableLiveData<Long> dueDate, reminder;
    TaskRepository repository;


    public AddTodoActivityViewModel(@NonNull Application application) {
        super(application);
        task = new MutableLiveData<>("");
        dueDate = new MutableLiveData<>(0L);
        reminder = new MutableLiveData<>(0L);
        repository = new TaskRepository(application);
    }

    public MutableLiveData<String> getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task.setValue(task);
    }

    public MutableLiveData<Long> getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate.setValue(dueDate);
    }

    public MutableLiveData<Long> getReminder() {
        return reminder;
    }

    public void setReminder(Long reminder) {
        this.reminder.setValue(reminder);
    }

    public void saveTask() {
        Todo item = new Todo(task.getValue(), false, dueDate.getValue(), reminder.getValue());
        item.setNid(NotificationHelper.generateID());
        repository.insert(new Todo(task.getValue(), false, dueDate.getValue(), reminder.getValue()));
        if(item.isTimeSet())
        NotificationHelper.schedule(getApplication(), item.getNid(), item.getText(), item.getReminder());
    }

    public void updateTask(Todo previous) {
        Todo item = new Todo(previous.getUid(), task.getValue(), false, dueDate.getValue(), reminder.getValue());
        item.setNid(NotificationHelper.generateID());
        repository.update(item);
        NotificationHelper.cancel(getApplication(), previous.getNid());
        if(item.isTimeSet())
        NotificationHelper.schedule(getApplication(), item.getNid(), item.getText(), item.getReminder());
    }
}
