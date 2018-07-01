package com.alcwithgoogle.journalapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by root on 6/25/18 for LoqourSys
 */

@Dao
public interface JournalDao {
    @Query("SELECT * FROM journal ORDER BY entry_date")
    LiveData<List<JournalEntry>> retrieveAllEntries();

    @Insert
    void insertJournalEntry(JournalEntry journalEntry);

    @Update
    void updateEntry(JournalEntry journalEntry);

    @Delete
    void deleteEntry(JournalEntry journalEntry);

    @Query("SELECT * FROM journal WHERE id = :entryId")
    JournalEntry retrieveEntryById(int entryId);
}
