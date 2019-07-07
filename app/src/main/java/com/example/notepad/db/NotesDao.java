package com.example.notepad.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notepad.model.Note;

import java.util.List;

@Dao
public interface NotesDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE) //if note exist replace it
    void insertNode(Note note);

    @Delete
    void deleteNode(Note note);

    @Update
    void updateNode(Note note);

    @Query("SELECT * FROM notes") // list all notes from Database
    List<Note> getNotes();

    @Query("SELECT * FROM notes WHERE id = :noteid") // get Note by ID
    Note getNoteById(int noteid);

    @Query("DELETE FROM notes WHERE id = :nodeid")  // delete Note by ID
    void deleteNoteById(int nodeid);
}
