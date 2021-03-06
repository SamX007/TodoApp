package com.example.todoapp.addedittask;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoapp.database.AppDatabase;
import com.example.todoapp.database.Repository;
import com.example.todoapp.database.TaskEntry;

public class AddEditTaskViewModel extends AndroidViewModel {

    static Repository repository;
    LiveData<TaskEntry> task;

    AddEditTaskViewModel(Application application, int taskId){
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new Repository(database);
        if(taskId != -1)
            task = repository.getTaskById(taskId);
    }


    public LiveData<TaskEntry> getTask(){
        return task;
    }

    public void insertTask(TaskEntry task){
        repository.insertTask(task);
    }

    public void updateTask(TaskEntry task){
        repository.insertTask(task);
    }

    public void deleteTask(TaskEntry task) {
        repository.deleteTask(task);
    }

    public static void deleteAllTask(){
        repository.deleteAllTask();
    }
}
