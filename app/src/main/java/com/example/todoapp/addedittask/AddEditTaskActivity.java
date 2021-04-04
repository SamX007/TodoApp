package com.example.todoapp.addedittask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.todoapp.R;
import com.example.todoapp.database.TaskEntry;
import com.example.todoapp.tasks.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = AddEditTaskActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    Button deleteButton;
    CalendarView calendarView;
    Calendar calendar = Calendar.getInstance();
    private boolean isUpdate = false;


    private int mTaskId = DEFAULT_TASK_ID;


    AddEditTaskViewModel viewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
            isUpdate = savedInstanceState.getBoolean("isUpdate");
            if(isUpdate){
                deleteButton.setVisibility(View.VISIBLE);
            }
        }

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);

            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                deleteButton.setVisibility(View.VISIBLE);
                isUpdate = true;
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getApplication(), mTaskId);
                viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);

                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(final TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntry);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onDeleteButtonClicked(taskEntry);
                            }
                        });
                    }
                });

            }
        }else{
            isUpdate = false;
            AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getApplication(), mTaskId);
            viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        outState.putBoolean("isUpdate", isUpdate);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);
        calendarView = findViewById(R.id.calendarView);
        deleteButton = findViewById(R.id.deleteButton);
        mButton = findViewById(R.id.saveButton);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view12, int year, int month, int dayOfMonth) {
                calendar.clear();
                calendar.set(year, month, dayOfMonth);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void onDeleteButtonClicked(final TaskEntry task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEditTaskActivity.this);
        builder.setMessage("Delete Task?")
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        viewModel.deleteTask(task);
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if(task == null){
            return;
        }
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }



    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        // Not yet implemented
        String description = mEditText.getText().toString();
        int priority = getPriorityFromViews();
        if(description.isEmpty()){
            mEditText.requestFocus();
            mEditText.setError("Task name required");
        }
        else{
            Date date = new Date();
            TaskEntry todo = new TaskEntry(description, priority, date,calendar.getTime());
            if(mTaskId == DEFAULT_TASK_ID)
                viewModel.insertTask(todo);
            else{
                todo.setId(mTaskId);
                viewModel.updateTask(todo);
            }
            finish();
        }
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }
}
