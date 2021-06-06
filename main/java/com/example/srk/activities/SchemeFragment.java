package com.example.srk.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.srk.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class SchemeFragment extends Fragment {

    private TextView schemesCountTextView;
    private final String[] EXTERNAL_PERMS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requestPermissions(EXTERNAL_PERMS, 138);
        View view = inflater.inflate(R.layout.fragment_scheme, container, false);

        TextView schemesDirTextView = view.findViewById(R.id.schemes_directory_textview);
        schemesCountTextView = view.findViewById(R.id.device_schemes_count_textview);
        TextView deviceContainsTextView = view.findViewById(R.id.device_contains_textview);

        schemesDirTextView.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schemes/");

        File schemes = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schemes/");

        if(!schemes.exists()){
            schemes.mkdir();

            File[] files = schemes.listFiles();

            if(getContext().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && files != null) {
                deviceContainsTextView.setVisibility(View.VISIBLE);
                schemesCountTextView.setText(String.valueOf(files.length));
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomNavigationView bottomNavigationView = getView().findViewById(R.id.bottom_nav_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 138){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)  {
                File schemes = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Schemes/");

                File[] files = schemes.listFiles();

                schemesCountTextView.setText(String.valueOf(files.length));
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = menuItem -> {
        if(menuItem.getItemId() == R.id.scan_qr_code_main){
            startActivity(new Intent(getContext(), QrCodeScanner.class));
        }else if(menuItem.getItemId() == R.id.serach_code_main){
            startActivity(new Intent(getContext(), SearchCodeActivity.class));
        }else if(menuItem.getItemId() == R.id.logout_main){
            logOut();
        }

        return true;
    };

    private void logOut(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.logout);
        alert.setCancelable(true);

        alert.setPositiveButton(R.string.logout_user, (dialogInterface, i) -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        alert.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

        });

        alert.show();
    }

}
