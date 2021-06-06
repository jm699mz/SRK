package com.example.srk.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.srk.adapters.KKSCodeAdapter;
import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.KKSCode;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchCodeActivity extends AppCompatActivity {

    ListView searchCodeListView;

    private DatabaseHelper databaseHelper;
    ArrayList<KKSCode> kksCodes;
    KKSCodeAdapter kksCodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_code);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.kks_codes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchCodeListView = findViewById(R.id.searchCodeListView);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        try {
            kksCodes = (ArrayList<KKSCode>) databaseHelper.getKKSCodes();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        kksCodeAdapter = new KKSCodeAdapter(this, kksCodes);
        searchCodeListView.setAdapter(kksCodeAdapter);


        searchCodeListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), KKSCodeActivity.class);
            intent.putExtra("kksCode", kksCodeAdapter.getItem(i).getLabel());
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchView);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                kksCodeAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}