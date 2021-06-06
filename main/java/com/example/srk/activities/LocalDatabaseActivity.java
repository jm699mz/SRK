package com.example.srk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.srk.R;
import com.example.srk.adapters.SwitchboardAdapter;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Switchboard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class LocalDatabaseActivity extends AppCompatActivity {

    private ListView swichboardsList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.switchboards_database);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        swichboardsList = findViewById(R.id.switchboards_list);

        ArrayList<String> switchboardLabels = new ArrayList<>();
        ArrayList<Switchboard> switchboards = null;

        try {
            switchboards = (ArrayList<Switchboard>) databaseHelper.switchboardDao().queryForAll();
            for(Switchboard switchboard : switchboards){
                switchboardLabels.add(switchboard.getLabel());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        SwitchboardAdapter switchboardAdapter = new SwitchboardAdapter(this, switchboards);
        swichboardsList.setAdapter(switchboardAdapter);

        setSwitchboardLabelListener(switchboardLabels);

    }

    private void setSwitchboardLabelListener(ArrayList<String> switchboardLabels){
        swichboardsList.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), SwitchboardActivity.class);
            try {
                String currentSwitchboardLabel = databaseHelper.getSwitchboardByLabel(switchboardLabels.get(i)).getLabel();
                intent.putExtra("switchboardLabel", currentSwitchboardLabel);
                ArrayList<String> kksCodes = new ArrayList<>();
                Long switchBoardId = databaseHelper.getSwitchboardIdByLabel(currentSwitchboardLabel);
                for(KKSCode kksCode : databaseHelper.kksCodeDao().queryForAll()){
                    if(kksCode.getSwitchboardId() == switchBoardId){
                        kksCodes.add(kksCode.getLabel());
                    }
                }
                intent.putExtra("kksCodes", kksCodes);
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        if(menuItem.getItemId() == R.id.home){
            finish();
        }else if(menuItem.getItemId() == R.id.logout_database){
            logOut();
        }

        return true;
    };

    private void logOut(){

        AlertDialog.Builder alert = new AlertDialog.Builder(LocalDatabaseActivity.this);
        alert.setTitle(R.string.logout);
        alert.setCancelable(true);

        alert.setPositiveButton(R.string.logout_user, (dialogInterface, i) -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        alert.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

        });

        alert.show();
    }
}