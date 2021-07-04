package com.coders.TaskApp.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.coders.TaskApp.Database.TodoDao;
import com.coders.TaskApp.Database.TodoRoomDatabase;
import com.coders.TaskApp.models.Todo;

import java.util.List;

public class TaskRepository {
    private TodoDao todoDao;
    private LiveData<List<Todo>> allTodo;
    private static volatile TaskRepository INSTANCE;

    private TaskRepository(Context context) {
        TodoRoomDatabase db = TodoRoomDatabase.getInstance(context);
        todoDao = db.getTodoDao();
    }

    public static TaskRepository getInstance(Context context){
        if (INSTANCE == null) {
            synchronized (TaskRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskRepository(context);
                }
            }
        }

        return INSTANCE;
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

    public Todo findTodoByNotificationId(int nid) {
        return todoDao.findTodoByNotificationId(nid);
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
