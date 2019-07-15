package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.adapters.NotesAdapter;
import com.example.notepad.callbacks.MainActionModeCallback;
import com.example.notepad.callbacks.noteEventListener;
import com.example.notepad.db.NotesDB;
import com.example.notepad.db.NotesDao;
import com.example.notepad.model.Note;
import com.example.notepad.utils.NoteUtils;
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
    private FloatingActionButton fab;
    private MainActionModeCallback actionModeCallback;
    private ActionMode action;
    private int checkCount = 0;

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
        fab = findViewById(R.id.fab);
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

        showEmptyView();

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
    }

    // when note is there to display show a message
    private void showEmptyView()
    {
        if(notes.size() == 0)
        {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);
        }
        else
        {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }
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


    @SuppressLint("RestrictedApi")
    @Override
    public void onNoteLongClick(Note note) {
        note.setChecked(true);
        checkCount = 1;
        adapter.setMultiCheckedNode(true);

        // set new listener to adapter intend off MainActivity listener that we have implement
        adapter.setListener(new noteEventListener() {
            @Override
            public void onNoteClick(Note note) {

                System.out.println("eaa");

                note.setChecked(!note.isChecked());
                if(note.isChecked())
                {
                    System.out.println("check");
                    checkCount++;
                }
                else
                {
                    checkCount--;
                }

                // if count is greater than 1 we will hide share button
                if(checkCount > 1)
                {
                    actionModeCallback.hideShareButton(false);
                }

                // else we will show share button
                else
                {
                    actionModeCallback.hideShareButton(true);
                }

                // finish action bar if no note is selected
                if(checkCount == 0)
                {
                    actionModeCallback.getAction().finish();
                }


                adapter.notifyDataSetChanged();
                actionModeCallback.setCount(checkCount + "/" + notes.size());

            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.action_delete_notes)
                    deleteMultipleNotes();
                else if(menuItem.getItemId() == R.id.action_share_notes)
                    shareNote();

                actionMode.finish();
                return false;
            }

        };

        // start action mode
        startActionMode(actionModeCallback);
        // set checked count of notes
        actionModeCallback.setCount(checkCount + "/" + notes.size());
        // hide fab button
        fab.setVisibility(View.GONE);

    }

    private void deleteMultipleNotes()
    {

        List<Note> checkedNotes = adapter.getCheckedNotes();
        if(checkedNotes.size() != 0)
        {
            for(Note n : checkedNotes)
            {
                dao.deleteNode(n);
            }
            loadNotes();
            Toast.makeText(this,checkedNotes.size() + "Note(s) are deleted", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"No Notes are Selected",Toast.LENGTH_SHORT).show();
        }

    }

    private void shareNote()
    {
        Note note = adapter.getCheckedNotes().get(0);

        Intent s = new Intent(Intent.ACTION_SEND);
        s.setType("text/plain");
        String notetext = note.getNoteText() + "\\n " +
                "Created On : " + NoteUtils.dateFromLong(note.getNoteDate());
        s.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(s);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        adapter.setMultiCheckedNode(false);
        adapter.setListener(this);  // set back old listener
        fab.setVisibility(View.VISIBLE);
    }

    // swipe toleft or right to delete a note
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if(notes != null)
            {
                Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                if(swipedNote != null)
                swipeToDelete(swipedNote,viewHolder);
            }

        }
    });

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Delete Note.?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dao.deleteNode(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());

                    }
                })
                .setCancelable(false)
                .create().show();
    }

    //method for back button
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}