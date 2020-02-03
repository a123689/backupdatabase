package com.example.dat.backupdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BackupData.OnBackupListener{

    private ItemNoteAdapter adapter;
    private List<Note> listNote = new ArrayList<>();

    private Context context;

    private DatabaseHelper db;

    private BackupData backupData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        db = new DatabaseHelper(context);
        backupData = new BackupData(context);
        backupData.setOnBackupListener(this);

        connectView();
    }



    private void connectView() {

        // find Float Action Button
        findViewById(R.id.fab).setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_note);

        // If the size of views will not change as the data changes.
        recyclerView.setHasFixedSize(true);

        // Setting the LayoutManager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Setting the adapter.
        adapter = new ItemNoteAdapter(context, listNote);
        recyclerView.setAdapter(adapter);
    }

    /**
     * update list note when resume (open app or finish NoteActivity)
     */
    public void onResume() {
        super.onResume();
        updateListNote();
    }

    /**
     * select all note from database and set to ls
     * use for loop to add into listNote.
     * We must add all item in ls into listNote then adapter can update
     * we add reverse ls to show new note at top of list
     */
    private void updateListNote() {
        // clear old list
        listNote.clear();
        // add all notes from database, reverse list
        ArrayList<Note> ls = db.getListNote("SELECT * FROM " + DatabaseHelper.TABLE_NOTE);

        // reverse list
        for (int i = ls.size() - 1; i >= 0; i--) {
            listNote.add(ls.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * display note have id
     */
    public static void showNote(Context context, long id) {
        Intent intent = new Intent(context, NoteActivity.class);

        // send id to NoteActivity
        intent.putExtra(NoteActivity.ID, id);

        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                showNote(context, NoteActivity.NEW_NOTE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_backup:
                backupData.exportToSD();
                break;
            case R.id.menu_import:
                backupData.importFromSD();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishExport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Export success";
        }
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishImport(String error) {
        String notify = error;
        if (error == null) {
            notify = "Import success";
            updateListNote();
        }
        Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
    }

}
