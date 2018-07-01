package com.alcwithgoogle.journalapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alcwithgoogle.journalapp.database.JournalDatabase;
import com.alcwithgoogle.journalapp.database.JournalEntry;
import com.alcwithgoogle.journalapp.database.JournalExecutors;
import com.alcwithgoogle.journalapp.utils.DateTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.alcwithgoogle.journalapp.EntryViewActivity.JOURNAL_ENTRY_ID_EXTRA;

public class NewJournalEntryActivity extends AppCompatActivity {
    private JournalDatabase mDatabase;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private static final String JOURNAL_DB_REF = "Journal";
    Button btnSave;

    TextView txtDate;
    EditText txtTitle;
    EditText txtJournalText;

    TextView txtTitleCounter;
    TextView txtTextCounter;

    Calendar calendar;
    String entryTitle = "";
    String entryText = "";

    private int DEFAULT_ENTRY_ID = -1;
    int journalId = DEFAULT_ENTRY_ID;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        mDatabase = JournalDatabase.getInstance(getApplicationContext());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        calendar = Calendar.getInstance();

        txtDate = findViewById(R.id.txt_date);
        txtTitle = findViewById(R.id.txt_journal_title);
        txtJournalText = findViewById(R.id.txt_journal_text);

        txtTitleCounter = findViewById(R.id.txt_title_counter);
        txtTextCounter = findViewById(R.id.txt_text_counter);

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEntry();
            }
        });

        String entryDate = DateTemplate.format(calendar, "%DDD%, %MMM% %dd%");
        txtDate.setText(entryDate);
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int titleLength = editable.toString().length();
                if (titleLength > 20) {
                    txtTitleCounter.setTextColor(Color.RED);
                } else {
                    txtTitleCounter.setTextColor(ContextCompat.getColor(getApplicationContext(),
                            R.color.textSecondary));
                }
                String titleCounter = titleLength + "/20";
                txtTitleCounter.setText(titleCounter);
            }
        });

        txtJournalText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int textLength = editable.toString().length();
                String journalLength = textLength + "  Chars";
                txtTextCounter.setText(journalLength);
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(JOURNAL_ENTRY_ID_EXTRA)) {
            if (journalId == DEFAULT_ENTRY_ID) {

                journalId = intent.getIntExtra(JOURNAL_ENTRY_ID_EXTRA, DEFAULT_ENTRY_ID);

                JournalExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final JournalEntry entry = mDatabase.dao().retrieveEntryById(journalId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateEntryUi(entry);
                            }
                        });
                    }
                });
            }
        }
    }

    private void validateEntry() {
        entryTitle = txtTitle.getText().toString();
        entryText = txtJournalText.getText().toString();
        if (entryTitle.isEmpty()) {
            showSnackbar("Enter a title for this journal entry");
        } else if (entryTitle.length() > 20) {
            showSnackbar("Title should not exceed 20 chars");
        } else if (entryText.isEmpty()) {
            showSnackbar("Enter the journal content");
        } else {
            saveJournalEntry();
        }
    }

    private void saveJournalEntry() {
        long entryDate = calendar.getTimeInMillis();
        final JournalEntry entry = new JournalEntry(entryTitle, entryText, entryDate);
        JournalExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (journalId == DEFAULT_ENTRY_ID) {
                    mDatabase.dao().insertJournalEntry(entry);
                } else {
                    entry.setId(journalId);
                    mDatabase.dao().updateEntry(entry);
                }
            }
        });

        syncJournalWithFirebase(entry);
    }

    private void syncJournalWithFirebase(final JournalEntry entry) {
        DatabaseReference mReference = mFirebaseDatabase.getReference(JOURNAL_DB_REF);
        if (journalId == DEFAULT_ENTRY_ID) {
            String entryId = mReference.push().getKey();
            FirebaseUser user = mAuth.getCurrentUser();

            assert user != null;
            String uid = user.getUid();

            if (entryId != null) {
                mReference.child(entryId).child(uid).setValue(entry);
            }
        } else {
            mReference.orderByChild("id")
                    .equalTo(journalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().setValue(entry);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        this.setResult(RESULT_OK);
        finish();
    }

    private void showSnackbar(String message) {
        Snackbar.make(btnSave, message, Snackbar.LENGTH_SHORT).show();
    }

    private void updateEntryUi(JournalEntry entry) {
        calendar.setTimeInMillis(entry.getEntryDate());
        String entryDate = DateTemplate.format(calendar, "%DDD%, %MMM% %dd%");

        txtDate.setText(entryDate);
        txtTitle.setText(entry.getTitle());
        txtJournalText.setText(entry.getDescription());
    }

}
