package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPhone;
    private TextInputLayout nameLayout, phoneLayout;
    private Button btnRegister, btnBackToLogin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        nameLayout = findViewById(R.id.nameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> validateAndRegister());
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void validateAndRegister() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Clear previous errors
        clearErrors();

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Name is required");
            return;
        }

        if (name.length() < 2) {
            nameLayout.setError("Name must be at least 2 characters long");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            return;
        }

        if (phone.length() < 10) {
            phoneLayout.setError("Phone number must be at least 10 digits");
            return;
        }

        // Save contact
        registerContact(name, phone);
    }

    private void clearErrors() {
        nameLayout.setError(null);
        phoneLayout.setError(null);
    }

    private void registerContact(String name, String phone) {
        // Save to database
        SQLiteDatabase db = null;
        try {
            db = openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
            
            // Use ContentValues to prevent SQL injection
            android.content.ContentValues values = new android.content.ContentValues();
            values.put("Pname", name);
            values.put("number", phone);
            db.insert("details", null, values);
            
            Toast.makeText(this, "Contact saved: " + name, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
} 