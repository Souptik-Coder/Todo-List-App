package com.coders.TaskApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
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
    private  int uid;

    @ColumnInfo(name = "Todo_text")
    private String text;

    @ColumnInfo(name = "Todo_isCompleted")
    private  boolean isCompleted;

    @ColumnInfo(name = "time_inMillis")
    private long millis;

    @ColumnInfo(name = "iSDateSet")
    private boolean isDateSet;

    @ColumnInfo(name = "isTimeSet")
    private boolean isTimeSet;

    @ColumnInfo(name = "notification_id")
    private int nid=uid;

    @ColumnInfo(name = "isNotificationSet")
    private boolean isNotificationSet;


    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public boolean isNotificationSet() {
        return isNotificationSet;
    }

    public void setNotificationSet(boolean notificationSet) {
        isNotificationSet = notificationSet;
    }

    public Todo() {
        this.text = "";
        this.isCompleted=false;
        this.millis=0;
        this.isDateSet=false;
        this.isTimeSet=false;
        isNotificationSet=false;
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

    public Todo(Todo todo){
        this.setUid(todo.getUid());
        this.setText(todo.getText());
        this.setCompleted(todo.isCompleted());
        this.setMillis(todo.getMillis());
        this.setDateSet(todo.isDateSet());
        this.setTimeSet(todo.isTimeSet);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getUid() {
        return uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "uid=" + uid +
                ", text='" + text + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
