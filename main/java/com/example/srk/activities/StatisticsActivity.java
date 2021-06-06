package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.History;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Switchboard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView lastScannedCodeLabel;
    private TextView totalNumberOfScans;
    private TextView deviceScans;
    private TextView switchboardScans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.statistics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        lastScannedCodeLabel = findViewById(R.id.statistics_last_scan_code);
        totalNumberOfScans = findViewById(R.id.statistics_textview_scans_text);
        deviceScans = findViewById(R.id.statistics_textview_device_scans_text);
        switchboardScans = findViewById(R.id.statistics_textview_scans_switchboard_text);

        try {
            setStats();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setStats() throws SQLException {
        lastScannedCodeLabel.setText(getLastScanLabel());
        totalNumberOfScans.setText(String.valueOf(getTotalScans()));
        deviceScans.setText(getCodeWithMaxScans());
        switchboardScans.setText(getSwitchboardWithMaxScans());
    }

    private String getLastScanLabel() throws SQLException{
        List<History> history = databaseHelper.getHistory();

        if(history.isEmpty()){
            return getString(R.string.no_scan);
        }

        return history.get(history.size() - 1).getCodeLabel();

    }

    private int getTotalScans() throws SQLException{
        int scans = 0;

        List<Switchboard> switchboards = databaseHelper.switchboardDao().queryForAll();
        List<KKSCode> kksCodes = databaseHelper.getKKSCodes();

        for(Switchboard switchboard : switchboards){
            scans += switchboard.getNumberOfScans();
        }

        for(KKSCode kksCode : kksCodes){
            scans += kksCode.getNumberOfScans();
        }

        return scans;
    }

    private String getCodeWithMaxScans() throws SQLException {

        int maxCount = 0;
        String kksCodeLabel = "";

        for(KKSCode kksCode : databaseHelper.getKKSCodes()){
            if(kksCode.getNumberOfScans() > maxCount){
                maxCount = kksCode.getNumberOfScans();
                kksCodeLabel = kksCode.getLabel();
            }
        }

        if(maxCount == 0){
            return getString(R.string.no_scan);
        }

        return kksCodeLabel;
    }

    private String getSwitchboardWithMaxScans() throws SQLException{
        int maxCount = 0;
        String switchboardLabel = "";

        for(Switchboard switchboard : databaseHelper.switchboardDao().queryForAll()){
            if(switchboard.getNumberOfScans() > maxCount){
                maxCount = switchboard.getNumberOfScans();
                switchboardLabel = switchboard.getLabel();
            }
        }

        if(maxCount == 0){
            getString(R.string.no_scan);
        }

        return switchboardLabel;
    }

    private void deleteStats() throws SQLException {
        List<Switchboard> switchboards = databaseHelper.switchboardDao().queryForAll();
        List<KKSCode> kksCodes = databaseHelper.getKKSCodes();

        for(Switchboard switchboard : switchboards){
            databaseHelper.resetSwitchboardScans(switchboard.getLabel());
        }

        for(KKSCode kksCode : kksCodes){
            databaseHelper.resetKKSCodeScans(kksCode.getLabel());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        if(menuItem.getItemId() == R.id.home){
            finish();
        }else if(menuItem.getItemId() == R.id.delete_stats){
            try {
                deleteStats();
                setStats();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(menuItem.getItemId() == R.id.logout_statistics){
            logOut();
        }

        return true;
    };

    private void logOut(){

        AlertDialog.Builder alert = new AlertDialog.Builder(StatisticsActivity.this);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}