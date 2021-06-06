package com.example.srk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.srk.R;
import com.example.srk.model.History;
import com.example.srk.model.Note;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {

    private ArrayList<Note> notes;
    private static final int MAX_NOTE_LENGTH  = 20;

    public NoteAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
        this.notes = notes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notes_listview_item, parent, false);
        }

        TextView noteContent = convertView.findViewById(R.id.note_item_content);

        if(note.getContent().length() > MAX_NOTE_LENGTH){
            String part = note.getContent().substring(0 , MAX_NOTE_LENGTH) + "...";
            noteContent.setText(part);

        }else {
            noteContent.setText(note.getContent());
        }

        return convertView;
    }
}
