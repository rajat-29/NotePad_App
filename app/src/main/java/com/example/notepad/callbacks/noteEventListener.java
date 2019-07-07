package com.example.notepad.callbacks;

import com.example.notepad.model.Note;

public interface noteEventListener
{
    // when specific note is clicked
    void onNoteClick(Note note);

    // when specific note is clicked for long
    void onNoteLongClick(Note note);

}
