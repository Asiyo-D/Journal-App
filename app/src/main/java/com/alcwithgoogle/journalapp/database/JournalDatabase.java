package com.alcwithgoogle.journalapp.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by root on 6/25/18 for LoqourSys
 */

@Database(entities = {JournalEntry.class}, version = 1, exportSchema = false)
public abstract class JournalDatabase extends RoomDatabase {
    private static String DATABASE_NAME = "journal";
    private static final Object LOCK = new Object();
    private static JournalDatabase instance;

    public static JournalDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                        JournalDatabase.class, JournalDatabase.DATABASE_NAME).build();
            }
        }

        return instance;
    }


    public abstract JournalDao dao();
}
