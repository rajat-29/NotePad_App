package com.example.notepad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.R;
import com.example.notepad.callbacks.noteEventListener;
import com.example.notepad.model.Note;
import com.example.notepad.utils.NoteUtils;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {

    private ArrayList<Note> notes;
    private Context context;
    private noteEventListener listener;
    private boolean multiCheckedNode = false;

    public NotesAdapter(Context context,ArrayList<Note> notes)
    {
        this.context = context;
        this.notes = notes;
    }


    @Override
    public NoteHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.note_layout,parent,false);

        return new NoteHolder(v);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {

        final Note note = getNote(position);
        if(note != null)
        {
            holder.noteText.setText(note.getNoteText());
            holder.noteDate.setText(NoteUtils.dateFromLong(note.getNoteDate()));

            //initilize click listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNoteClick(note);
                }
            });

            //initilize long listener
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onNoteLongClick(note);
                    return false;
                }
            });

            //check wheater checkBox of note is selected

            if(multiCheckedNode) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(note.isChecked());
            }
            else
            {
                holder.checkBox.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private Note getNote(int position)
    {
        return notes.get(position);
    }

    public class NoteHolder extends RecyclerView.ViewHolder
    {

        TextView noteText,noteDate;
        CheckBox checkBox;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            noteDate = itemView.findViewById(R.id.note_date);
            noteText = itemView.findViewById(R.id.note_text);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public void setListener(noteEventListener listener) {
        this.listener = listener;
    }

    public void setMultiCheckedNode(boolean multiCheckedNode) {
        this.multiCheckedNode = multiCheckedNode;
        notifyDataSetChanged();
    }
}