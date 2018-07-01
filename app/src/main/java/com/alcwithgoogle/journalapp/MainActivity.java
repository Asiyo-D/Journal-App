package com.alcwithgoogle.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alcwithgoogle.journalapp.database.JournalDatabase;
import com.alcwithgoogle.journalapp.database.JournalEntry;
import com.alcwithgoogle.journalapp.database.JournalExecutors;
import com.alcwithgoogle.journalapp.utils.JournalAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MainActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener {

    RecyclerView journalRecycler;
    JournalAdapter adapter;
    private JournalDatabase database;
    FloatingActionButton fab;
    Handler handler;
    ConstraintLayout contentNoData;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewJournalEntryActivity.class));
            }
        });
        handler = new Handler();

        journalRecycler = findViewById(R.id.recycler);
        contentNoData = findViewById(R.id.content_no_entry);

        adapter = new JournalAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        journalRecycler.setLayoutManager(layoutManager);
        journalRecycler.setAdapter(adapter);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        journalRecycler.addItemDecoration(itemDecoration);

        JournalAdapter.SwipeToDeleteCallback swipeToDeleteCallback = new JournalAdapter
                .SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                saveAndDelete(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(journalRecycler);

        database = JournalDatabase.getInstance(getApplicationContext());
        initViewModel();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Snackbar.make(fab, "Signed in as " + mAuth.getCurrentUser().getEmail(),
                    Snackbar.LENGTH_SHORT).show();
        }

    }

    private void initViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getJournalEntries().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                adapter.setEntries(journalEntries);
                if (journalEntries == null || journalEntries.size() <= 0) {
                    contentNoData.setVisibility(View.VISIBLE);
                } else {
                    contentNoData.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_sign_out) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int entryId) {
        Intent intent = new Intent(this, EntryViewActivity.class);
        intent.putExtra(EntryViewActivity.JOURNAL_ENTRY_ID_EXTRA, entryId);
        startActivity(intent);
    }

    SparseArray<JournalEntry> tempItem = new SparseArray<>();
    Snackbar deleteSnack;
    JournalEntry deletedEntry = null;

    private void saveAndDelete(int pos) {
        if (deleteSnack != null) {
            deleteSnack.dismiss();
        }
        if (deletedEntry != null) {
            handler.post(deleteThread);
        }
        deletedEntry = adapter.entries.get(pos);
        tempItem.clear();
        tempItem.put(pos, deletedEntry);

        adapter.removeItem(pos);
        deleteSnack = Snackbar.make(fab, "Journal entry deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoDelete();
                    }
                });
        deleteSnack.show();

        long DELETE_DELAY = 3500;
        handler.postDelayed(deleteThread, DELETE_DELAY);
    }

    private void undoDelete() {
        if (tempItem != null) {
            handler.removeCallbacks(deleteThread);
            int journalId = tempItem.keyAt(0);
            JournalEntry entry = tempItem.valueAt(0);
            adapter.addItem(journalId, entry);
            deletedEntry = null;
        }
    }

    private Runnable deleteThread = new Runnable() {
        @Override
        public void run() {
            JournalExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    database.dao().deleteEntry(deletedEntry);
                    deletedEntry = null;
                }
            });
        }
    };

}
