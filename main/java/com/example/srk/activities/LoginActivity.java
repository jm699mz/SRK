package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.j256.ormlite.stmt.query.In;

public class LoginActivity extends AppCompatActivity {

    private EditText loginName, loginPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
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

        loginName = findViewById(R.id.edit_text_login_name);
        loginPassword = findViewById(R.id.edit_text_login_password);
        Button loginButton = findViewById(R.id.button_login);
        TextView createAcc = findViewById(R.id.textview_create_acc);
        firebaseAuth = FirebaseAuth.getInstance();

        createAcc.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(view -> {
            String userMail = loginName.getText().toString().trim();
            String userPassword = loginPassword.getText().toString().trim();

            if(TextUtils.isEmpty(userMail)){
                loginName.setError(getString(R.string.email_required));
                return;
            }

            if(TextUtils.isEmpty(userPassword)){
                loginPassword.setError(getString(R.string.password_required));
                return;
            }

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }

            firebaseAuth.signInWithEmailAndPassword(userMail, userPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), R.string.login_successful, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), R.string.login_unsuccessful, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}