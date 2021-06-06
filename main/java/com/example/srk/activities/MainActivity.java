package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Scheme;
import com.example.srk.model.Switchboard;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseHelper databaseHelper;
    private final String[] EXTERNAL_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private DrawerLayout drawerLayout;
    private DatabaseReference kksCodesRef;
    private DatabaseReference schemesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.srk);
        FirebaseMessaging.getInstance().subscribeToTopic("global");

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        requestForPermission();

        kksCodesRef = FirebaseDatabase.getInstance().getReference("KKSCodes");
        schemesRef = FirebaseDatabase.getInstance().getReference("Schemes");

        setKKsCodesRefListener();
        setSchemesRefListener();


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new SchemeFragment());
        fragmentTransaction.commit();


        ImageView user = header.findViewById(R.id.imageview_user);
        TextView userSignedMail = header.findViewById(R.id.textview_user_signed_mail);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            user.setVisibility(View.VISIBLE);
            userSignedMail.setVisibility(View.VISIBLE);
            userSignedMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }


    }

    private void setSchemesRefListener(){

        schemesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> schemeLabelsFirebase = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                    try {
                        Scheme scheme = dataSnapshot.getValue(Scheme.class);
                        if(scheme != null) {
                            schemeLabelsFirebase.add(scheme.getLabel());
                            if (!databaseHelper.schemeExists(scheme)) {
                                databaseHelper.createScheme(scheme);
                                Toast.makeText(getApplicationContext(), getString(R.string.kkscode_scheme) + scheme.getLabel() + getString(R.string.was_created), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    for(Scheme scheme : databaseHelper.getSchemes()){
                        if(!schemeLabelsFirebase.contains(scheme.getLabel())){
                            databaseHelper.deleteSchemeByLabel(scheme.getLabel());
                            Toast.makeText(getApplicationContext(), getString(R.string.kkscode_scheme) + scheme.getLabel() + getString(R.string.was_deleted), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setKKsCodesRefListener(){

        kksCodesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> firebaseKKSCodes = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KKSCode kksCode = dataSnapshot.getValue(KKSCode.class);

                    firebaseKKSCodes.add(kksCode.getLabel());

                    try {
                        if(!databaseHelper.kksCodeExists(kksCode)){
                            Toast.makeText(getApplicationContext(), kksCode.getLabel(), Toast.LENGTH_LONG).show();
                            KKSCode newKKSCode = new KKSCode();
                            newKKSCode.setDescription(kksCode.getDescription());
                            newKKSCode.setLabel(kksCode.getLabel());
                            newKKSCode.setDevicePower(kksCode.getDevicePower());
                            newKKSCode.setNumberOfScans(0);

                            if(!databaseHelper.switchboardExists(kksCode.getSwitchboardLabel())){
                                Switchboard switchboard = new Switchboard();
                                switchboard.setLabel(kksCode.getSwitchboardLabel());
                                switchboard.setNumberOfScans(0);
                                databaseHelper.createSwitchboard(switchboard);
                            }else {
                                newKKSCode.setSwitchboardId(databaseHelper.getSwitchboardByLabel(kksCode.getSwitchboardLabel()).getId());
                            }

                            databaseHelper.createKKSCode(newKKSCode);
                            Toast.makeText(getApplicationContext(), getString(R.string.kks_code_created) + kksCode.getLabel(), Toast.LENGTH_LONG).show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    for(KKSCode kksCode : databaseHelper.getKKSCodes()){
                        if(!firebaseKKSCodes.contains(kksCode.getLabel())){
                            databaseHelper.deleteCodeByLabel(kksCode.getLabel());
                            Toast.makeText(getApplicationContext(), "KKS kód: " + kksCode.getLabel() + " bol vymazaný z databázy", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Switchboard createSwitchboard(String label) throws SQLException {

        if(databaseHelper != null) {
            Switchboard switchboard = new Switchboard();
            switchboard.setLabel(label);
            switchboard.setNumberOfScans(0);

            databaseHelper.createSwitchboard(switchboard);
            return switchboard;
        }

        return null;
    }

    private void createKKSCode(String label, String description, String devicePower, Switchboard switchboard) throws SQLException {

            if(databaseHelper != null && databaseHelper.getSwitchBoardId(switchboard) != null) {
                KKSCode kksCode = new KKSCode();
                kksCode.setLabel(label);
                kksCode.setDescription(description);
                kksCode.setDevicePower(devicePower);
                kksCode.setNumberOfScans(0);

                kksCode.setSwitchboardId(databaseHelper.getSwitchBoardId(switchboard));

                databaseHelper.createKKSCode(kksCode);
            }
    }

    private void createScheme(String label, String url) throws SQLException {
        if(databaseHelper != null) {
            Scheme scheme = new Scheme(label, url);
            databaseHelper.createScheme(scheme);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if(menuItem.getItemId() == R.id.scan){
            startActivity(new Intent(getApplicationContext(), QrCodeScanner.class));
        }else if(menuItem.getItemId() == R.id.database){
            startActivity(new Intent(getApplicationContext(), LocalDatabaseActivity.class));
        }else if(menuItem.getItemId() == R.id.kksCodeSearch){
            startActivity(new Intent(getApplicationContext(), SearchCodeActivity.class));
        }else if(menuItem.getItemId() == R.id.logout){
            logOut();
        }else if(menuItem.getItemId() == R.id.history){
            startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
        }else if(menuItem.getItemId() == R.id.statistics){
            startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logOut(){

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
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

    public void requestForPermission() {

        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                int EXTERNAL_REQUEST = 138;
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, 102);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
            }
        }

    }

    public boolean canAccessExternalSd() {
        return (hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }

}