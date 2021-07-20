package com.coders.TaskApp.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.models.Todo;

import java.util.List;

public class HomeActivitySharedViewModel extends AndroidViewModel {
    private final TaskRepository taskRepository;
    private final LiveData<List<Todo>> allTask;
    private final MutableLiveData<String> searchQuery;

    public HomeActivitySharedViewModel(Application application) {
        super(application);
        taskRepository = TaskRepository.getInstance(application);
        allTask = taskRepository.getAllTask();
        searchQuery = new MutableLiveData<>("");
    }

    public MutableLiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery.postValue(searchQuery);
    }

    public LiveData<List<Todo>> getAllTask() {
        return allTask;
    }

    public void insert(Todo todo) {
        taskRepository.insert(todo);
    }

    public void delete(Todo todo) {
        taskRepository.delete(todo);
    }

    public void update(Todo todo) {
        taskRepository.update(todo);
    }

}