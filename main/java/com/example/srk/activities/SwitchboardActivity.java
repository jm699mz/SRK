package com.example.srk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.srk.adapters.KKSCodeAdapter;
import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.KKSCode;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class SwitchboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switchboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.srk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView kksListView = findViewById(R.id.kks_listview);
        TextView switchboardLabel = findViewById(R.id.device_name_textview_text);

        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        ArrayList<String> codes = new ArrayList<>();
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                codes = null;
            }else{
                codes = getIntent().getStringArrayListExtra("kksCodes");
                switchboardLabel.setText(getIntent().getStringExtra("switchboardLabel"));
            }
        }

        ArrayList<KKSCode> kksCodes = new ArrayList<>();
        for(String kksCode: codes){
            try {
                kksCodes.add(databaseHelper.getKKSCodeByLabel(kksCode));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        KKSCodeAdapter kksCodeAdapter = new KKSCodeAdapter(this, kksCodes);
        kksListView.setAdapter(kksCodeAdapter);

        ArrayList<String> finalArrayList = codes;

        kksListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), KKSCodeActivity.class);
            intent.putExtra("kksCode", finalArrayList.get(i));
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}