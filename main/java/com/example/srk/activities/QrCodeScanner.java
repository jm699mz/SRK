package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.srk.R;
import com.example.srk.model.Config.DatabaseHelper;
import com.example.srk.model.History;
import com.example.srk.model.KKSCode;
import com.example.srk.model.Switchboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.Result;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QrCodeScanner extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);

        CodeScannerView scanView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scanView);

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            try {
                List<Switchboard> switchboards = databaseHelper.switchboardDao().queryForAll();
                List<KKSCode> kksCodes = databaseHelper.kksCodeDao().queryForAll();

                checkKKSCodes(kksCodes, result);
                checkSwitchboards(switchboards, kksCodes, result);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
        RequestForCamera();
    }

    private void RequestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mCodeScanner.startPreview();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QrCodeScanner.this,
                                "permission required",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void createHistory(String label) throws SQLException {
        History history =  new History();
        history.setCodeLabel(label);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String creationTime = dateFormat.format(Calendar.getInstance().getTime());
        history.setCreationTime(creationTime);
        history.setScannedBy(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        databaseHelper.createHistory(history);
    }

    private void checkKKSCodes(List<KKSCode> kksCodes, Result result) throws SQLException {
        for(KKSCode kksCode : kksCodes) {
            if(result.getText().equals(kksCode.getLabel())){
                Intent intent = new Intent(getApplicationContext(), KKSCodeActivity.class);
                databaseHelper.updateKKSCodeScans(kksCode.getLabel());
                intent.putExtra("kksCode", kksCode.getLabel());
                createHistory(kksCode.getLabel());
                startActivity(intent);
                finish();
            }
        }
    }

    private void checkSwitchboards(List<Switchboard> switchboards, List<KKSCode> kksCodes, Result result) throws SQLException {
        for(Switchboard switchboard: switchboards){
            if(result.getText().equals(switchboard.getLabel())){
                Intent intent = new Intent(getApplicationContext(), SwitchboardActivity.class);
                ArrayList<String> switchboardKKS = new ArrayList<>();
                for(KKSCode kksCode: kksCodes){
                    if(kksCode.getSwitchboardId() == switchboard.getId()){
                        switchboardKKS.add(kksCode.getLabel());
                    }
                }

                intent.putExtra("switchboardLabel", switchboard.getLabel());

                databaseHelper.updateSwitchboardScans(switchboard.getLabel());
                intent.putExtra("kksCodes", switchboardKKS);
                createHistory(switchboard.getLabel());
                startActivity(intent);
                finish();
            }
        }
    }
}