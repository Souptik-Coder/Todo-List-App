package com.coders.TaskApp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.Utils.ScheduleNotification;
import com.coders.TaskApp.Utils.TimeConverter;
import com.coders.TaskApp.databinding.ActivityAddTodoBinding;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;

public class AddTodoActivity extends AppCompatActivity {

    Todo todo;
    Intent editingIntent;
    TaskRepository repository;
    MaterialDatePicker<Long> datePicker, reminderDatePicker;
    MaterialDatePicker.Builder<Long> datePickerBuilder;
    MaterialTimePicker.Builder timePickerBuilder;
    MaterialTimePicker timePicker;
    boolean isEditing = false;
    AddTodoActivityViewModel viewModel;
    ActivityAddTodoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        viewModel = new ViewModelProvider(this).get(AddTodoActivityViewModel.class);
        viewModel.getTask().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.taskInputEdittext.setText(s);
            }
        });

        viewModel.getDueDate().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong == 0) {
                    binding.removeDueDateIcon.setVisibility(View.INVISIBLE);
                    binding.dueDateTextView.setText(null);
                } else {
                    binding.removeDueDateIcon.setVisibility(View.VISIBLE);
                    binding.dueDateTextView.setText(DateTimeFormatter.formatDate(aLong));
                }
            }
        });

        viewModel.getReminder().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Toast.makeText(AddTodoActivity.this, "val "+aLong, Toast.LENGTH_SHORT).show();
                if (aLong == 0.0) {
                    binding.removeReminderIcon.setVisibility(View.INVISIBLE);
                    binding.reminderDateTextView.setText(null);
                    binding.reminderTimeTextView.setText(null);
                }
                else {
                    binding.removeReminderIcon.setVisibility(View.VISIBLE);
                    binding.reminderDateTextView.setText(DateTimeFormatter.formatDate(aLong));
                    binding.reminderTimeTextView.setText(DateTimeFormatter.formatTime(aLong));
                }
            }
        });

        binding.saveTask.setOnClickListener(new SaveTaskListener());
        binding.DueDateLayout.setOnClickListener(new DueDateOnclickListener());


        binding.removeDueDateIcon.setOnClickListener(v -> {
            viewModel.setDueDate((long) 0);
        });

        binding.removeReminderIcon.setOnClickListener(v -> {
            viewModel.setReminder((long) 0);
        });

        editingIntent = getIntent();
        if (editingIntent.getAction() != null && editingIntent.getAction().equals(Intent.ACTION_EDIT))
            getEditingInfo();

        datePickerBuilder.setTitleText("Choose a date");
        datePicker = datePickerBuilder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            viewModel.setDueDate(selection);
        });




        timePicker = timePickerBuilder.build();
        timePicker.addOnPositiveButtonClickListener(v -> {
            viewModel.setReminder(viewModel.getReminder().getValue()+TimeConverter.HourToMillis(timePicker.getHour()) + TimeConverter.MinuteToMillis(timePicker.getMinute()));
        });


        binding.ReminderLayout.setOnClickListener(v -> {
            datePickerBuilder.setSelection(viewModel.getReminder().getValue() != 0 ? viewModel.getReminder().getValue()
                    : System.currentTimeMillis());
            reminderDatePicker=datePickerBuilder.build();
            reminderDatePicker.addOnPositiveButtonClickListener(selection ->{
                    viewModel.setReminder(selection); timePicker.show(getSupportFragmentManager(), null);});
            reminderDatePicker.show(getSupportFragmentManager(),null);
        });


    }

    @Override
    public void onBackPressed() {
        if (binding.taskInputEdittext.getText().toString().trim().isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to quit without saving?")
                    .setPositiveButton("Ok", (dialog, which) -> onNavigateUp())
                    .setNegativeButton("Cancel", (dialog, which) -> {
                    });
            builder.create().show();
        }
    }

    private void init() {
        datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        timePickerBuilder = new MaterialTimePicker.Builder();
        repository = new TaskRepository(this);
        todo = new Todo();
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("SetTextI18n")
    private void getEditingInfo() {
        isEditing = true;
        todo = (Todo) editingIntent.getSerializableExtra("item");
        binding.taskInputEdittext.setText(todo.getText());
        if (todo.isDateSet()) {
            datePickerBuilder.setSelection(todo.getDueDate());
            viewModel.setDueDate(todo.getDueDate());
        }

        if (todo.isTimeSet()) {
            datePickerBuilder.setSelection(todo.getReminder());
            viewModel.setReminder(todo.getReminder());
        }
    }

    private class DueDateOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            datePicker.show(getSupportFragmentManager(), null);
        }
    }

    private class SaveTaskListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (binding.taskInputEdittext.getText().toString().trim().isEmpty()) {
                binding.taskInputLayout.setError("Task cannot be empty");
                return;
            } else if (isEditing) {
                //Cancelling the previously set notification
                if (todo.isNotificationSet()) {
                    ScheduleNotification.cancel(AddTodoActivity.this, todo.getNid());
                    todo.setNotificationSet(false);
                }

                todo.setText(binding.taskInputEdittext.getText().toString());
                repository.update(todo);
            } else {
                todo.setText(binding.taskInputEdittext.getText().toString());
                repository.insert(todo);
            }

            if (todo.isTimeSet()) {
                ScheduleNotification.schedule(AddTodoActivity.this, todo.getNid(), todo.getReminder(), todo);
                todo.setNotificationSet(true);
            }
            getOnBackPressedDispatcher().onBackPressed();
        }
    }

}