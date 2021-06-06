package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.DownloadTask;
import com.example.srk.adapters.NoteAdapter;
import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Note;
import com.example.srk.model.Scheme;
import com.example.srk.model.Switchboard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KKSCodeActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView codeLabel;
    private String currentKKSCode;
    private NoteAdapter noteAdapter;
    private DatabaseReference databaseNotes;
    private String kksCode;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kks_code);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.srk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        codeLabel = findViewById(R.id.textview_code_label);
        TextView nameLabel = findViewById(R.id.textview_device_name_label);
        TextView switchboardLabelTextView = findViewById(R.id.textview_switchboard_label);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        ListView notesListView = findViewById(R.id.listview_notes);
        databaseNotes = FirebaseDatabase.getInstance().getReference("Notes");
        Bundle extras;

        if(savedInstanceState == null){
           extras = getIntent().getExtras();
           if(extras != null){
               kksCode = getKKSCode(extras);
               try {
                   KKSCode code = databaseHelper.getKKSCodeByLabel(kksCode);
                   switchboardLabelTextView.setText(databaseHelper.getSwitchboardById(code.getSwitchboardId()).getLabel());
                   nameLabel.setText(databaseHelper.getCodeDescriptionByLabel(kksCode));
                   codeLabel.setText(kksCode);
               } catch (SQLException e) {
                   e.printStackTrace();
               }
            }
        }

        addListener();
        currentKKSCode = kksCode.toLowerCase();

        setSwitchboardLabelListener(switchboardLabelTextView);

        ArrayList<Note> notes = new ArrayList<>();
        addNotes(notes);

        noteAdapter = new NoteAdapter(this, notes);
        notesListView.setAdapter(noteAdapter);
        addNotesListener(notesListView);

        if(!schemeExists()){
            downloadScheme();
        }

    }

    private void setSwitchboardLabelListener(TextView switchboardLabelTextView){
        switchboardLabelTextView.setOnClickListener(view -> {

            try {
                Switchboard switchboard = databaseHelper.getSwitchboardByLabel(switchboardLabelTextView.getText().toString());
                Intent intent = new Intent(getApplicationContext(), SwitchboardActivity.class);

                intent.putExtra("switchboardLabel", switchboardLabelTextView.getText());
                ArrayList<String> codes = new ArrayList<>();
                for(KKSCode code : databaseHelper.getKKSCodes()){
                    if(code.getSwitchboardId() == switchboard.getId()){
                        codes.add(code.getLabel());
                    }
                }

                intent.putExtra("kksCodes", codes);
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void addNotes(ArrayList<Note> notes){
        try {
            for(Note note : databaseHelper.getNotes()){
                if(note.getKksCodeLabel().equals(codeLabel.getText().toString())){
                    notes.add(note);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setProgressDialog(){
        mProgressDialog = new ProgressDialog(KKSCodeActivity.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage(getString(R.string.scheme_downloading));
    }

    private void downloadScheme(){

        if(!isInternetConnection()){
            Toast.makeText(getApplicationContext(), R.string.internet_connection_needed, Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(KKSCodeActivity.this);
        alertDialogBuilder.setMessage(R.string.download_scheme);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(R.string.continue_scheme, (dialog, which) -> {
            setProgressDialog();
            int counter = 0;
            try {
                Scheme scheme = databaseHelper.getSchemeByLabel(kksCode);
                new DownloadTask(mProgressDialog, counter).execute(scheme.getUrl(), scheme.getLabel() + ".png");
                counter++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.open_browser, (dialogInterface, i) -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(databaseHelper.getSchemeByLabel(kksCode).getUrl()));
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnNegative.getLayoutParams();

        layoutParams.weight = 5;

        btnNegative.setLayoutParams(layoutParams);
    }

    private boolean schemeExists() {
        if (isStoragePermissionGranted()) {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schemes/");
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.getName().equals(kksCode + ".png")) {
                    return true;
                }
            }
        }

        return false;
    }

    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        addListener();
        ArrayList<Note> notes = new ArrayList<>();
        try {
            for(Note note : databaseHelper.getNotes()){
                if(note.getKksCodeLabel().equals(codeLabel.getText().toString())){
                    notes.add(note);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        noteAdapter.clear();
        noteAdapter.addAll(notes);
        noteAdapter.notifyDataSetChanged();
    }

    private String getKKSCode(Bundle bundle){
        return bundle.getString("kksCode");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        if(menuItem.getItemId() == R.id.add_note){
            addNote();
        }else if(menuItem.getItemId() == R.id.showScheme){
            showSchemes();
        }else if(menuItem.getItemId() == R.id.home){
            finish();
        }

        return true;
    };

    private void addNoteToFirebase(Note note){
        String id = databaseNotes.push().getKey();
        note.setFirebaseId(id);
        databaseNotes.child(id).setValue(note);
    }

    private void addNotesListener(ListView notesListView){
        notesListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
            intent.putExtra("content", noteAdapter.getItem(i).getContent());
            try {
                Note currentNote = databaseHelper.getNoteByContent(noteAdapter.getItem(i).getContent());
                intent.putExtra("createdBy", currentNote.getCreatedBy());
                intent.putExtra("creationDate", currentNote.getCreationTime());
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public  boolean isInternetConnection()
    {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception ignored) {

        }
        return connected;
    }

    private void addListener(){

        databaseNotes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        try {
                            if (dataSnapshot.child("kksCodeLabel").getValue().toString().equals(codeLabel.getText().toString())) {

                                Note note = dataSnapshot.getValue(Note.class);

                                if (databaseHelper.noteExists(note)) {
                                    continue;
                                }

                                note.setFirebaseId(note.getFirebaseId());
                                databaseHelper.createNote(note);
                                noteAdapter.notifyDataSetChanged();
                                noteAdapter.add(note);

                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showSchemes(){

        if(!schemeExists()){
            downloadScheme();
            return;
        }

        CharSequence[] items = {getString(R.string.electrical_scheme), getString(R.string.power_supply_scheme)};

        AlertDialog.Builder builder = new AlertDialog.Builder(KKSCodeActivity.this);
        builder.setTitle(R.string.schemes)
                .setItems(items, (dialog, which) -> {
                    if(which == 0){
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schemes/" + currentKKSCode + ".png"), "image/*");
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    private void addNote(){
        if(!isInternetConnection()){
            Toast.makeText(getApplicationContext(), R.string.notes_internet_needed, Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(KKSCodeActivity.this);
        alert.setCancelable(true);


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_note, null);
        EditText input = dialogView.findViewById(R.id.edit_note);

        alert.setView(dialogView);

        alert.setPositiveButton(R.string.add, (dialog, whichButton) -> {

            Note note = new Note();
            note.setContent(input.getText().toString());
            note.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String creationTime = dateFormat.format(Calendar.getInstance().getTime());
            note.setCreationTime(creationTime);
            try {
                note.setKksCodeLabel(codeLabel.getText().toString());

                addNoteToFirebase(note);
                databaseHelper.createNote(note);
                List<Note> notes = databaseHelper.getNotes();
                ArrayList<String> codes = new ArrayList<>();

                for(Note content : notes){
                    if(content.getKksCodeLabel().equals(codeLabel.getText().toString())) {
                        if(content.getContent().length() > 10){
                            String part = content.getContent().substring(0, 10) + "...";
                            codes.add(part);
                        }else {
                            codes.add(content.getContent());
                        }
                    }
                }

                noteAdapter.clear();
                noteAdapter.addAll(notes);
                noteAdapter.notifyDataSetChanged();

            } catch (SQLException e) {
                e.printStackTrace();
            }



        });

        alert.setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
        });

        alert.show();
    }
}