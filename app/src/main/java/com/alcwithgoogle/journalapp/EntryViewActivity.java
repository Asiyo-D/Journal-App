package com.alcwithgoogle.journalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.alcwithgoogle.journalapp.database.JournalDatabase;
import com.alcwithgoogle.journalapp.database.JournalEntry;
import com.alcwithgoogle.journalapp.database.JournalExecutors;
import com.alcwithgoogle.journalapp.utils.DateTemplate;

import java.util.Calendar;

public class EntryViewActivity extends AppCompatActivity {

    public static final String JOURNAL_ENTRY_ID_EXTRA = "journalEntryId";
    private int DEFAULT_ENTRY_ID = -1;
    private JournalDatabase database;
    int journalId = DEFAULT_ENTRY_ID;
    final int RC_EDIT_JOURNAL = 1014;

    Calendar calendar;
    TextView txtDate;
    TextView txtTitle;
    TextView txtBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewJournalEntryActivity.class);
                intent.putExtra(EntryViewActivity.JOURNAL_ENTRY_ID_EXTRA, journalId);
                startActivityForResult(intent, RC_EDIT_JOURNAL);
            }
        });
        calendar = Calendar.getInstance();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = JournalDatabase.getInstance(this);

        txtDate = findViewById(R.id.txt_date);
        txtTitle = findViewById(R.id.txt_title);
        txtBody = findViewById(R.id.txt_body);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(JOURNAL_ENTRY_ID_EXTRA)) {
            if (journalId == DEFAULT_ENTRY_ID) {

                journalId = intent.getIntExtra(JOURNAL_ENTRY_ID_EXTRA, DEFAULT_ENTRY_ID);
                loadJournalEntry();

            }
        }
    }

    private void loadJournalEntry() {
        JournalExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final JournalEntry entry = database.dao().retrieveEntryById(journalId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateEntryUi(entry);
                    }
                });
            }
        });
    }

    private void updateEntryUi(JournalEntry entry) {
        calendar.setTimeInMillis(entry.getEntryDate());
        String entryDate = DateTemplate.format(calendar, "%DDD%, %MMM% %dd%");

        txtDate.setText(entryDate);
        txtTitle.setText(entry.getTitle());
        txtBody.setText(entry.getDescription());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_EDIT_JOURNAL) {
                loadJournalEntry();
            }
        }
    }
}
