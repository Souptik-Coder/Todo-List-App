package com.coders.TaskApp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.coders.TaskApp.models.Todo;

import java.util.List;


@Dao
public interface TodoDao {

    @Insert
    void insertTodo(Todo todo);

    @Update
    void updateTodo(Todo todo);

    @Delete
    void delete(Todo todo);

    @Query("SELECT * FROM Todo_table ORDER BY Todo_IsCompleted ASC,Todo_IsDateSet DESC,DueDate ASC")
    LiveData<List<Todo>> searchTodoByQuery();

   @Query("SELECT * FROM Todo_table WHERE Todo_ID LIKE :uid")
   Todo findTodoById(int uid);

   @Query("Delete FROM Todo_table")
    void deleteAll();

//   @Query("SELECT * FROM Todo_table ORDER BY Todo ID LIMIT :n ,1")
//    Todo getAtPosition(int n);

}
