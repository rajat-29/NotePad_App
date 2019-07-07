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
     private Note temp;
     public static final String Note_Extra_Key = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

         inputNote = findViewById(R.id.input_note);
         dao = NotesDB.getInstance(this).notesDao();

         if(getIntent().getExtras() != null)
         {
             int id = getIntent().getExtras().getInt(Note_Extra_Key,0);
             temp = dao.getNoteById(id);

             inputNote.setText(temp.getNoteText());
         }
         else
         {
             temp = new Note();
         }
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

            //if note is there we will update else we will create new one
            temp.setNoteText(text);
            temp.setNoteDate(date);

            if(temp.getId() == -1)
            {
                dao.insertNode(temp);
            }
            else
            {
                dao.updateNode(temp);
            }

            finish(); // return to Main Activity
        }
    }
}