package com.coders.TaskApp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class AddTodoActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<String> task;
    private final MutableLiveData<Long> dueDate, reminder;


    public AddTodoActivityViewModel(@NonNull Application application) {
        super(application);
        task = new MutableLiveData<>("");
        dueDate = new MutableLiveData<>((long) 0);
        reminder = new MutableLiveData<>((long) 0);
    }

    public MutableLiveData<String> getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task.postValue(task);
    }

    public MutableLiveData<Long> getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate.postValue(dueDate);
    }

    public MutableLiveData<Long> getReminder() {
        return reminder;
    }

    public void setReminder(Long reminder) {
        this.reminder.setValue(reminder);
    }
}
