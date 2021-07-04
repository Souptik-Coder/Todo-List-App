package com.coders.TaskApp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.coders.TaskApp.models.Todo;


@Database(entities = {Todo.class}, version = 1)
public abstract class TodoRoomDatabase extends RoomDatabase {

    private static volatile TodoRoomDatabase INSTANCE;

    public static TodoRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TodoRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TodoRoomDatabase.class, "Todo_Database")
                            .allowMainThreadQueries()
                            .build();

                }
            }
        }

        return INSTANCE;
    }

    public abstract TodoDao getTodoDao();

}
