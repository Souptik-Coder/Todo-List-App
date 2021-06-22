package com.coders.TaskApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Todo_table")
public class Todo implements Serializable {

    public void setUid(int uid) {
        this.uid = uid;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Todo_id")
    private int uid;

    @ColumnInfo(name = "Todo_text")
    private String text;

    @ColumnInfo(name = "Todo_isCompleted")
    private boolean isCompleted;

    @ColumnInfo(name = "DueDate_inMillis")
    private long dueDate;

    @ColumnInfo(name = "Reminder_inMillis")
    private long reminder;

    @ColumnInfo(name = "iSDateSet")
    private boolean isDateSet;

    @ColumnInfo(name = "isTimeSet")
    private boolean isTimeSet;

    @ColumnInfo(name = "notification_id")
    private int nid = uid;

    @ColumnInfo(name = "isNotificationSet")
    private boolean isNotificationSet;

    public int getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public long getReminder() {
        return reminder;
    }

    public void setReminder(long reminder) {
        this.reminder = reminder;
    }

    public boolean isDateSet() {
        return isDateSet;
    }

    public void setDateSet(boolean dateSet) {
        isDateSet = dateSet;
    }

    public boolean isTimeSet() {
        return isTimeSet;
    }

    public void setTimeSet(boolean timeSet) {
        isTimeSet = timeSet;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public boolean isNotificationSet() {
        return isNotificationSet;
    }

    public void setNotificationSet(boolean notificationSet) {
        isNotificationSet = notificationSet;
    }

    public Todo() {
        this.text = "";
        this.isCompleted = false;
        this.dueDate = 0;
        this.isDateSet = false;
        this.isTimeSet = false;
        isNotificationSet = false;
    }

//    public Todo(Todo todo) {
//        this.setUid(todo.getUid());
//        this.setText(todo.getText());
//        this.setCompleted(todo.isCompleted());
//        this.setDueDate(todo.getDueDate());
//        this.setDateSet(todo.isDateSet());
//        this.setTimeSet(todo.isTimeSet);
//        this.setNid(todo.getNid());
//    }
}
