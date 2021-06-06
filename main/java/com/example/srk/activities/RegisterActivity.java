package com.example.srk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srk.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.registration);

        name = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_email);
        password = findViewById(R.id.edit_text_password);
        EditText phone = findViewById(R.id.edit_text_phone);
        Button register = findViewById(R.id.button_register);
        TextView loginButton = findViewById(R.id.textview_login_here);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        loginButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        register.setOnClickListener(view -> {
            String userMail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if(TextUtils.isEmpty(userMail)){
                email.setError(getString(R.string.email_required));
                return;
            }

            if(TextUtils.isEmpty(userPassword)){
                name.setError(getString(R.string.password_required));
                return;
            }


            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }else if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }

            firebaseAuth.createUserWithEmailAndPassword(userMail, userPassword).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, getString(R.string.user) + userMail + getString(R.string.user_created), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, R.string.account_not_created, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}