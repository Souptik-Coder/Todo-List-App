package com.coders.TaskApp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.coders.TaskApp.Adapter.RecyclerViewItem;

import java.io.Serializable;

@Entity(tableName = "Todo_table")
public class Todo implements Serializable, RecyclerViewItem {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Todo_ID")
    private int uid;
    @ColumnInfo(name = "Todo_Text")
    private String text;
    @ColumnInfo(name = "Todo_IsCompleted")
    private boolean isCompleted;
    @ColumnInfo(name = "Todo_IsDateSet")
    private boolean isDateSet;
    @ColumnInfo(name = "DueDate")
    private long dueDate;
    @ColumnInfo(name = "Reminder")
    private long reminder;
    @ColumnInfo(name = "Notification_ID")
    private int nid = uid;
    @ColumnInfo(name = "Note")
    private String note;
    @ColumnInfo(name = "created_on")
    private long createdOnMillis;

    public Todo() {
        this.text = "";
        this.isCompleted = false;
        this.dueDate = 0;
    }

    public Todo(String text, boolean isCompleted, long dueDate, long reminder) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.isDateSet = dueDate != 0;
        this.reminder = reminder;
    }

    public Todo(int uid, String text, boolean isCompleted, long dueDate, long reminder) {
        this.uid = uid;
        this.text = text;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.isDateSet = dueDate != 0;
        this.reminder = reminder;
    }

    public long getCreatedOnMillis() {
        return createdOnMillis;
    }

    public void setCreatedOnMillis(long createdOnMillis) {
        this.createdOnMillis = createdOnMillis;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
        return reminder != 0;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public boolean isNotificationSet() {
        return nid != 0;
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

}
