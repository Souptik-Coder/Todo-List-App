package com.coders.TaskApp;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.coders.TaskApp.models.Todo;

import java.util.List;

public class HomeActivityViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<Todo>> allTask;
    private String searchQuery;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public HomeActivityViewModel(Application application){
        super(application);
        taskRepository=new TaskRepository(application);
        allTask=taskRepository.getAllTask();
        searchQuery="";
    }

    LiveData<List<Todo>> getAllTask(){
        return  allTask;
    }

    public void insert(Todo todo){
        taskRepository.insert(todo);
    }

    public void delete(Todo todo){
        taskRepository.delete(todo);
    }

    public void update(Todo todo){
        taskRepository.update(todo);
    }

}