package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.adapters.NotesAdapter;
import com.example.notepad.callbacks.noteEventListener;
import com.example.notepad.db.NotesDB;
import com.example.notepad.db.NotesDao;
import com.example.notepad.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.notepad.EditNoteActivity.Note_Extra_Key;

public class MainActivity extends AppCompatActivity implements noteEventListener{

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private NotesDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init recycleView
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // init fab button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 05/07/2019 add new note
                 onAddNewNote();
//                Intent intent =  new Intent(getApplicationContext(),EditNoteActivity.class);
//                startActivity(intent);
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }

    private void loadNotes() {

        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();  // get all notes from Database

        this.notes.addAll(list);

        this.adapter = new NotesAdapter(this,notes);

        //set Listener
        this.adapter.setListener(this);

        this.recyclerView.setAdapter(adapter);
    }

    private void onAddNewNote() {

        startActivity(new Intent(this,EditNoteActivity.class));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    // when note is clicked to edit note
    @Override
    public void onNoteClick(Note note) {

       Intent editIntent = new Intent(this,EditNoteActivity.class);
       editIntent.putExtra(Note_Extra_Key, note.getId());
       startActivity(editIntent);

    }

    // when note is long clicked
    @Override
    public void onNoteLongClick(Note note) {

        Log.d(TAG, "onNoteLongClick :" + note.getId());

    }
}