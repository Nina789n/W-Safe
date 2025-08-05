package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmPasswordLayout, phoneLayout;
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
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
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
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Clear previous errors
        clearErrors();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Name is required");
            return;
        }

        if (name.length() < 2) {
            nameLayout.setError("Name must be at least 2 characters long");
            return;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            return;
        }

        if (!email.contains("@")) {
            emailLayout.setError("Email must contain @ symbol");
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return;
        }

        if (password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters long");
            return;
        }

        // Check for special characters in password
        if (!password.matches(".*[@#$%^&+=].*")) {
            passwordLayout.setError("Password must contain at least one special character (@#$%^&+=)");
            return;
        }

        // Validate confirm password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm your password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return;
        }

        // Validate phone number
        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            return;
        }

        if (phone.length() < 10) {
            phoneLayout.setError("Phone number must be at least 10 digits");
            return;
        }

        // If all validations pass, register the user
        registerUser(name, email, password, phone);
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        phoneLayout.setError(null);
    }

    private void registerUser(String name, String email, String password, String phone) {
        // In a real app, this would save to a database
        // For demo purposes, we'll save to SharedPreferences
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("userPassword", password); // In real app, this should be hashed
        editor.putString("userPhone", phone);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Toast.makeText(this, "Registration successful! Welcome " + name, Toast.LENGTH_LONG).show();
        
        // Start main activity
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
} 