package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.srk.adapters.HistoryAdapter;
import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.History;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.scan_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView historyListView = findViewById(R.id.history_listview);
        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_history);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        try {
            ArrayList<History> histories = (ArrayList<History>) databaseHelper.getHistory();

            if(histories.isEmpty()){
                Toast toast = Toast.makeText(getApplicationContext(), R.string.no_scan_history, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            historyAdapter = new HistoryAdapter(this, histories);
            historyListView.setAdapter(historyAdapter);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.delete_history){
                try {
                    databaseHelper.deleteHistory();
                    historyAdapter.notifyDataSetChanged();
                    historyAdapter.clear();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else if(menuItem.getItemId() == R.id.home){
                finish();
            }

            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}