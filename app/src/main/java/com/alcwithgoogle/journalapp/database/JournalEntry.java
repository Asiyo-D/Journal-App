package com.alcwithgoogle.journalapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by root on 6/25/18 for LoqourSys
 */

@Entity(tableName = "journal")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;

    @ColumnInfo(name = "entry_date")
    private long entryDate;

    @Ignore
    public JournalEntry() {
    }

    @Ignore
    public JournalEntry(String title, String description, long entryDate) {
        this.title = title;
        this.description = description;
        this.entryDate = entryDate;
    }

    public JournalEntry(int id, String title, String description, long entryDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.entryDate = entryDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setEntryDate(long entryDate) {
        this.entryDate = entryDate;
    }

    public long getEntryDate() {
        return entryDate;
    }
}
