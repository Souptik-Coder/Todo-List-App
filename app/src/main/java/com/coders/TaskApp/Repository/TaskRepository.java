package com.coders.TaskApp.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.coders.TaskApp.models.Todo;
import com.coders.TaskApp.Database.TodoDao;
import com.coders.TaskApp.Database.TodoRoomDatabase;

import java.util.List;

public class TaskRepository {
    private TodoDao todoDao;
    private LiveData<List<Todo>> allTodo;

    public TaskRepository(Context context) {
        TodoRoomDatabase db = TodoRoomDatabase.getInstance(context);
        todoDao = db.getTodoDao();
    }

    public LiveData<List<Todo>> getAllTask() {
        allTodo = todoDao.searchTodoByQuery();
        return allTodo;
    }

    public void insert(Todo todo) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                todoDao.insertTodo(todo);
            }
        });
        thread.start();
    }

   public Todo findTodoById(int uid) {
        return todoDao.findTodoById(uid);
    }

   public void delete(Todo todo) {
       Thread thread = new Thread(() -> todoDao.delete(todo));
       thread.start();
    }

    public void update(Todo todo) {
        Thread thread = new Thread(() -> todoDao.updateTodo(todo));
        thread.start();
    }


}
