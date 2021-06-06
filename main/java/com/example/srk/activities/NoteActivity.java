package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.Note;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.query.In;

import java.sql.SQLException;

public class NoteActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Long currentNoteId;
    private String content;
    private DatabaseReference databaseNotes;
    private String currentNoteFirebaseId;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        EditText editTextNote = findViewById(R.id.edit_text_note);
        TextView textViewDateOfCreation = findViewById(R.id.textview_date_of_creation);
        TextView textViewCreatedBy = findViewById(R.id.textview_created_by);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
        databaseNotes = FirebaseDatabase.getInstance().getReference("Notes");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Bundle extras;
        if(savedInstanceState == null){
            extras = getIntent().getExtras();

            if(extras != null){
                content = extras.getString("content");
                String createdBy = extras.getString("createdBy");
                String creationDate = extras.getString("creationDate");

                editTextNote.setText(content);
                textViewCreatedBy.setText(createdBy);
                textViewDateOfCreation.setText(creationDate);
            }
        }

        try {
            String finalContent = content;
            currentNote = databaseHelper.getNoteByContent(finalContent);
            currentNoteId = currentNote.getId();
            currentNoteFirebaseId = currentNote.getFirebaseId();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        addEditTextListener(editTextNote);
    }

    private void addEditTextListener(EditText editTextNote){

        Long finalCurrentNoteId = currentNoteId;

        if(!isInternetConnection()){
            editTextNote.setEnabled(false);
        }else{
            editTextNote.setEnabled(true);
        }

        editTextNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    databaseHelper.updateNote(finalCurrentNoteId, charSequence.toString());

                    Note changedNote = new Note();
                    changedNote.setFirebaseId(currentNoteFirebaseId);
                    changedNote.setId(finalCurrentNoteId);
                    changedNote.setCreationTime(currentNote.getCreationTime());
                    changedNote.setCreatedBy(currentNote.getCreatedBy());
                    changedNote.setKksCodeLabel(currentNote.getKksCodeLabel());
                    changedNote.setContent(charSequence.toString());
                    databaseNotes.child(currentNote.getFirebaseId()).setValue(changedNote);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_note){

            if(!isInternetConnection()){
                Toast.makeText(getApplicationContext(), "Pre odstránenie poznámky je potrebné internetové pripojenie", Toast.LENGTH_LONG).show();
                return false;
            }

            try {
                databaseHelper.deleteNoteById(currentNoteId);
                databaseNotes.child(currentNoteFirebaseId).removeValue();
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.home){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else if(menuItem.getItemId() == R.id.note_save){
                finish();
            }else if(menuItem.getItemId() == R.id.logout_statistics){

                new AlertDialog.Builder(NoteActivity.this)
                        .setTitle("Odhlásenie")
                        .setMessage("Chcete sa odhlásiť?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton("Odhlásiť sa", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("Zrušiť", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            return true;
        }
    };

    public  boolean isInternetConnection()
    {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {

        }
        return connected;
    }
}