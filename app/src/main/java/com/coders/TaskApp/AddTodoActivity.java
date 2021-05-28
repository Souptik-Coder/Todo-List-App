package com.coders.TaskApp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.coders.TaskApp.Utils.FormatDate;
import com.coders.TaskApp.Utils.FormatTime;
import com.coders.TaskApp.Utils.ScheduleNotification;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Calendar;

public class AddTodoActivity extends AppCompatActivity {

    Todo todo;
    Intent editingIntent;
    TaskRepository repository;
    FloatingActionButton saveTask;
    MaterialToolbar toolbar;
    TextInputLayout taskLayout;
    EditText task;
    TextView dueDateText, dateView;
    MaterialDatePicker<Long> datePicker;
    TimePickerDialog timePicker;
    RelativeLayout dueDateLayout, setTimeLayout;
    ImageView removeDueDate, removeTime;
    Calendar calendar = Calendar.getInstance();
    int mDay = calendar.get(Calendar.DAY_OF_MONTH), mMonth = calendar.get(Calendar.MONTH), mYear = calendar.get(Calendar.YEAR),
            mHour = calendar.get(Calendar.HOUR_OF_DAY), mMinute = calendar.get(Calendar.MINUTE);
    JSONObject DueDate, Reminder;
    boolean isEditing = false;
    ScheduleNotification notification;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(saveTask, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        getAllId();
        repository = new TaskRepository(this);
        todo = new Todo();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        task.requestFocus();
        saveTask.setOnClickListener(new SaveTaskListener());
        dueDateLayout.setOnClickListener(new DueDateOnclickListener());


        removeDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo.setDateSet(false);
                dueDateText.setText(null);
                removeDueDate.setVisibility(View.INVISIBLE);
                removeTime.performClick();
            }
        });

        removeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todo.setTimeSet(false);
                dateView.setText(null);
                removeTime.setVisibility(View.INVISIBLE);
            }
        });

        editingIntent = getIntent();
        if (editingIntent.hasExtra("item"))
            getEditingInfo();


        MaterialDatePicker.Builder<Long> builder=MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Choose a date");
        datePicker=builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                calendar.setTimeInMillis(selection);
                dueDateText.setText(new FormatDate(calendar.getTimeInMillis()).format());
                removeDueDate.setVisibility(View.VISIBLE);
                todo.setDateSet(true);
                todo.setMillis(calendar.getTimeInMillis());
            }
        });

        timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                dateView.setText(new FormatTime(calendar.getTimeInMillis()).format());
                removeTime.setVisibility(View.VISIBLE);
                todo.setTimeSet(true);
                todo.setMillis(calendar.getTimeInMillis());

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);


        setTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dueDateText.getText().toString().isEmpty())
                    Toast.makeText(AddTodoActivity.this, "Set Due Date first", Toast.LENGTH_LONG).show();
                else
                    timePicker.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (task.getText().toString().trim().isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to quit without saving?").
                    setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onNavigateUp();
                        }
                    }).
                    setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.create().show();
        }
    }

    private void getAllId() {
        task = findViewById(R.id.task_input);
        dueDateLayout = findViewById(R.id.DueDateLayout);
        dueDateText = findViewById(R.id.DueDateText);
        toolbar = findViewById(R.id.toolbar2);
        saveTask = findViewById(R.id.save_task);
        setTimeLayout = findViewById(R.id.set_time_layout);
        dateView = findViewById(R.id.date_view);
        removeTime = findViewById(R.id.remove_time_icon);
        removeDueDate = findViewById(R.id.remove_due_date);
        taskLayout = findViewById(R.id.task_input_layout);
    }

    private void getEditingInfo() {
        isEditing = true;
        todo = (Todo) editingIntent.getSerializableExtra("item");
        task.setText(todo.getText());
        if (todo.isDateSet()) {
            calendar.setTimeInMillis(todo.getMillis());
            dueDateText.setText(new FormatDate(todo.getMillis()).format());
        }

        if (todo.isTimeSet()) {
            calendar.setTimeInMillis(todo.getMillis());
            dateView.setText(new FormatTime(todo.getMillis()).format());
        }
    }

    private class DueDateOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            datePicker.show(getSupportFragmentManager(),null);
        }
    }

    private class SaveTaskListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (task.getText().toString().trim().isEmpty()) {
                taskLayout.setError("Enter Task");
                return;
            } else if (isEditing) {
                //Cancelling the previously set notification
                if (todo.isNotificationSet()) {
                    ScheduleNotification.cancel(AddTodoActivity.this, todo.getNid());
                    todo.setNotificationSet(false);
                }

                todo.setText(task.getText().toString());
                repository.update(todo);
            } else {
                todo.setText(task.getText().toString());
                repository.insert(todo);
            }

            if (todo.isTimeSet()) {
                ScheduleNotification.schedule(AddTodoActivity.this, todo.getNid(), todo.getMillis(), todo);
                todo.setNotificationSet(true);
            }
            getOnBackPressedDispatcher().onBackPressed();
        }
    }

}