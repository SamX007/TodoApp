package com.example.todoapp.tasks;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todoapp.database.AppDatabase;
import com.example.todoapp.database.Repository;
import com.example.todoapp.database.TaskEntry;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    Repository repository;

   private  LiveData<List<TaskEntry>> tasks;



    public  MainActivityViewModel(Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new Repository(database);
        tasks = repository.getTasks();
    }

    public LiveData<List<TaskEntry>> getTasks(){
        return tasks;
    }

    public void deleteTask(TaskEntry task){
        repository.deleteTask(task);
    }

}
