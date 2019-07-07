package com.example.notepad;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.db.NotesDB;
import com.example.notepad.db.NotesDao;
import com.example.notepad.model.Note;

import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

     private EditText inputNote;
     private NotesDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

         inputNote = findViewById(R.id.input_note);
         dao = NotesDB.getInstance(this).notesDao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.save_note)
            onSaveNote();

        return super.onOptionsItemSelected(item);
    }

    // to insert new note
    private void onSaveNote() {

        String text = inputNote.getText().toString();

        if(!text.isEmpty())
        {
            long date = new Date().getTime(); // to get date and time
            Note note = new Note(text,date);  // create new note
            dao.insertNode(note);  // insert node

            finish(); // return to Main Activity
        }
    }
}