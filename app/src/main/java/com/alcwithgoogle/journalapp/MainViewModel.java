package com.alcwithgoogle.journalapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alcwithgoogle.journalapp.database.JournalDatabase;
import com.alcwithgoogle.journalapp.database.JournalEntry;

import java.util.List;

/**
 * Created by root on 6/25/18 for LoqourSys
 */
public class MainViewModel extends AndroidViewModel {

    private LiveData<List<JournalEntry>> journalEntries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        JournalDatabase database = JournalDatabase.getInstance(application.getApplicationContext());
        journalEntries = database.dao().retrieveAllEntries();
    }

    public LiveData<List<JournalEntry>> getJournalEntries() {
        return journalEntries;
    }
}
