package com.coders.TaskApp.Utils;

import android.content.Context;

import com.coders.TaskApp.models.Todo;
import com.coders.TaskApp.TaskRepository;

public class SwapTodo {

    private final Todo todo1,todo2;
    private final Context context;

    public SwapTodo(Context context,Todo todo1, Todo todo2){
        this.context=context;
        this.todo1=todo1;
        this.todo2=todo2;
    }

    public void swap(){
        int temp=todo1.getUid();
        todo1.setUid(todo2.getUid());
        todo2.setUid(temp);
        TaskRepository repository=new TaskRepository(context);
        repository.update(todo1);
        repository.update(todo2);
    }
}
