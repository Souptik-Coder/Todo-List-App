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
    private final MutableLiveData<String> note;
    private final MutableLiveData<Long> dueDate, reminder;
    TaskRepository repository;
    private long createdOn;


    public AddTodoActivityViewModel(@NonNull Application application) {
        super(application);
        task = new MutableLiveData<>("");
        note = new MutableLiveData<>("");
        dueDate = new MutableLiveData<>(0L);
        reminder = new MutableLiveData<>(0L);
        createdOn = System.currentTimeMillis();
        repository = new TaskRepository(application);
    }

    public MutableLiveData<String> getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task.setValue(task);
    }

    public MutableLiveData<String> getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note.setValue(note);
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

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public void saveTask() {
        Todo item = new Todo(task.getValue(), false, dueDate.getValue(), reminder.getValue());
        item.setNote(note.getValue());
        item.setCreatedOnMillis(createdOn);
        item.setNid(NotificationHelper.generateID());
        repository.insert(item);
        if (item.isTimeSet())
            NotificationHelper.schedule(getApplication(), item.getUid(), item.getReminder());
    }

    public void updateTask(Todo previous) {
        Todo item = new Todo(previous.getUid(), task.getValue(), previous.isCompleted(), dueDate.getValue(), reminder.getValue());
        item.setNote(note.getValue());
        item.setCreatedOnMillis(createdOn);
        item.setNid(NotificationHelper.generateID());
        repository.update(item);
        NotificationHelper.cancel(getApplication(), previous.getNid());
        if (item.isTimeSet())
            NotificationHelper.schedule(getApplication(), item.getUid(), item.getReminder());
    }
}
