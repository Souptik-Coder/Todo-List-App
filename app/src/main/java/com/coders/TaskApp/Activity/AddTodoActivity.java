package com.coders.TaskApp.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;

import com.coders.TaskApp.R;
import com.coders.TaskApp.Repository.TaskRepository;
import com.coders.TaskApp.Utils.DateTimeFormatter;
import com.coders.TaskApp.Utils.TimeConverter;
import com.coders.TaskApp.Utils.Utils;
import com.coders.TaskApp.ViewModel.AddTodoActivityViewModel;
import com.coders.TaskApp.databinding.ActivityAddTodoBinding;
import com.coders.TaskApp.models.Todo;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

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
    Calendar reminder = Calendar.getInstance();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTodoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        viewModel = new ViewModelProvider(this).get(AddTodoActivityViewModel.class);
        editingIntent = getIntent();
        if (editingIntent.getAction() != null && editingIntent.getAction().equals("Intent.ACTION_EDIT")) {
            binding.toolbar.setTitle("Edit Task");
            getEditingInfo();
        } else if (editingIntent.getAction() != null && editingIntent.getAction().equals(Intent.ACTION_SEND)) {
            if (editingIntent.hasExtra(Intent.EXTRA_TEXT))
                viewModel.setTask(editingIntent.getStringExtra(Intent.EXTRA_TEXT));
        } else if (editingIntent.getAction() != null && editingIntent.getAction().equals(Intent.ACTION_CREATE_REMINDER)) {
            if (editingIntent.hasExtra(Intent.EXTRA_TITLE))
                viewModel.setTask(editingIntent.getStringExtra(Intent.EXTRA_TITLE));
            if (editingIntent.hasExtra(Intent.EXTRA_TEXT))
                viewModel.setNote(editingIntent.getStringExtra(Intent.EXTRA_TEXT));
            if (editingIntent.hasExtra(Intent.EXTRA_TIME) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                viewModel.setReminder(editingIntent.getLongExtra(Intent.EXTRA_TIME, 0));
        }


        binding.createdOnTextview.setText("Created on " + Html.fromHtml(DateTimeFormatter.formatDate(viewModel.getCreatedOn())
                + " at " + DateTimeFormatter.formatTime(viewModel.getCreatedOn())));

        viewModel.getTask().observe(this, s -> binding.taskInputEdittext.setText(s));

        viewModel.getNote().observe(this, s -> binding.noteInputEdittext.setText(s));

        viewModel.getDueDate().observe(this, aLong -> {
            if (aLong == 0) {
                binding.removeDueDateIcon.setVisibility(View.INVISIBLE);
                binding.dueDateTextView.setText(null);
            } else {
                binding.removeDueDateIcon.setVisibility(View.VISIBLE);
                binding.dueDateTextView.setText(Html.fromHtml("Due " + DateTimeFormatter.formatDate(aLong)));
            }
        });

        viewModel.getReminder().observe(this, aLong -> {
            if (aLong == 0.0) {
                binding.removeReminderIcon.setVisibility(View.INVISIBLE);
                binding.reminderDateTextView.setText(null);
                binding.reminderTimeTextView.setText(null);
            } else {
                binding.removeReminderIcon.setVisibility(View.VISIBLE);
                binding.reminderDateTextView.setText(Html.fromHtml(DateTimeFormatter.formatDate(aLong)));
                binding.reminderTimeTextView.setText(Html.fromHtml("Remind me on " + DateTimeFormatter.formatTime(aLong)));
            }
        });

        binding.saveTask.setOnClickListener(v -> {
            viewModel.setTask(binding.taskInputEdittext.getText().toString());
            viewModel.setNote(binding.noteInputEdittext.getText().toString());
            if (binding.taskInputEdittext.getText().toString().trim().isEmpty()) {
                binding.taskInputLayout.setError("Task cannot be empty");
                return;
            } else if (isEditing)
                viewModel.updateTask(todo);
            else
                viewModel.saveTask();
            super.onBackPressed();
        });

        binding.DueDateLayout.setOnClickListener(v -> showDueDatePopUpMenu());

        binding.removeDueDateIcon.setOnClickListener(v -> viewModel.setDueDate((long) 0));

        binding.removeReminderIcon.setOnClickListener(v -> viewModel.setReminder((long) 0));

        if (viewModel.getDueDate().getValue() == 0) {
            datePickerBuilder.setSelection(null);
        } else
            datePickerBuilder.setSelection(viewModel.getDueDate().getValue());

        datePickerBuilder.setTitleText("Choose a date");
        datePicker = datePickerBuilder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> viewModel.setDueDate(selection));

        if (viewModel.getReminder().getValue() == 0) {
            timePickerBuilder.setHour(TimeConverter.MillisToHour(System.currentTimeMillis()));
            timePickerBuilder.setMinute(TimeConverter.MillisToMinute(System.currentTimeMillis()));

        } else {
            timePickerBuilder.setHour(TimeConverter.MillisToHour(viewModel.getReminder().getValue()));
            timePickerBuilder.setMinute(TimeConverter.MillisToMinute(viewModel.getReminder().getValue()));
        }
        timePicker = timePickerBuilder.build();
        timePicker.addOnPositiveButtonClickListener(v -> {
            reminder.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            reminder.set(Calendar.MINUTE, timePicker.getMinute());
            reminder.set(Calendar.SECOND, 0);
            viewModel.setReminder(reminder.getTimeInMillis());
        });
        timePicker.addOnNegativeButtonClickListener(v -> viewModel.setReminder((long) 0));
        binding.ReminderLayout.setOnClickListener(v -> showReminderPopUpMenu());
    }

    private void showDueDatePopUpMenu() {
        Calendar today = Calendar.getInstance();
        PopupMenu popupMenu = new PopupMenu(AddTodoActivity.this, binding.DueDateLayout, Gravity.END);
        popupMenu.inflate(R.menu.duedatepopupmenu);
        Menu menu = popupMenu.getMenu();
        menu.getItem(0).setTitle("Today (" + Utils.weekName.get(today.get(Calendar.DAY_OF_WEEK)) + ")");
        if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            menu.getItem(1).setTitle("Tomorrow (" + Utils.weekName.get(1) + ")");
        else
            menu.getItem(1).setTitle("Tomorrow (" + Utils.weekName.get(today.get(Calendar.DAY_OF_WEEK) + 1) + ")");
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.today) {
                viewModel.setDueDate(today.getTimeInMillis());
                return true;
            } else if (item.getItemId() == R.id.tomorrow) {
                today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) + 1);
                viewModel.setDueDate(today.getTimeInMillis());
                return true;
            } else if (item.getItemId() == R.id.pick) {
                datePicker.show(getSupportFragmentManager(), null);
                return true;
            }
            return false;
        });

        ForceShowPopUpMenuIcons(popupMenu);
        popupMenu.show();
    }

    private void showReminderPopUpMenu() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        PopupMenu popupMenu = new PopupMenu(AddTodoActivity.this, binding.ReminderLayout, Gravity.END);
        popupMenu.inflate(R.menu.reminderpopupmenu);
        Menu menu = popupMenu.getMenu();
        if (today.get(Calendar.HOUR_OF_DAY) > 21)
            menu.getItem(1).setEnabled(false);
        else {
            int time = today.get(Calendar.HOUR_OF_DAY) + 2;
            String suffix = "AM";
            if (time > 12) {
                time = time - 12;
                suffix = "PM";
            }
            menu.getItem(1).setTitle("Later Today (" + time + " " + suffix + ")");

            if (viewModel.getDueDate().getValue() == 0)
                menu.getItem(0).setEnabled(false);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.today) {
                today.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY) + 2);
                viewModel.setReminder(today.getTimeInMillis());
                return true;
            } else if (item.getItemId() == R.id.tomorrow) {
                today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) + 1);
                today.set(Calendar.HOUR_OF_DAY, 9);
                viewModel.setReminder(today.getTimeInMillis());
                return true;
            } else if (item.getItemId() == R.id.same) {
                reminder.setTimeInMillis(viewModel.getDueDate().getValue());
                timePicker.show(getSupportFragmentManager(), null);
                return true;
            } else if (item.getItemId() == R.id.pick) {
                datePickerBuilder.setSelection(viewModel.getReminder().getValue() != 0 ? viewModel.getReminder().getValue()
                        : null);
                reminderDatePicker = datePickerBuilder.build();
                reminderDatePicker.addOnPositiveButtonClickListener(selection -> {
                    reminder.setTimeInMillis(selection);
                    timePicker.show(getSupportFragmentManager(), null);
                });
                reminderDatePicker.show(getSupportFragmentManager(), null);
                return true;
            }
            return false;
        });

        ForceShowPopUpMenuIcons(popupMenu);
        popupMenu.show();
    }

    private void ForceShowPopUpMenuIcons(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.taskInputEdittext.getText().toString().trim().isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to quit without saving?")
                    .setPositiveButton("Ok", (dialog, which) -> super.onBackPressed())
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
        viewModel.setTask(todo.getText());
        viewModel.setCreatedOn(todo.getCreatedOnMillis());
        viewModel.setNote(todo.getNote());
        if (todo.isDateSet()) {
            datePickerBuilder.setSelection(todo.getDueDate());
            viewModel.setDueDate(todo.getDueDate());
        }

        if (todo.isTimeSet()) {
            datePickerBuilder.setSelection(todo.getReminder());
            viewModel.setReminder(todo.getReminder());
        }
    }
}