package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashSet;
import java.util.Set;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmPasswordLayout, phoneLayout;
    private Button btnRegister, btnBackToLogin;
    private RadioGroup rgUserType;
    private RadioButton rbUser, rbAdmin;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

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
        
        rgUserType = findViewById(R.id.rgUserType);
        rbUser = findViewById(R.id.rbUser);
        rbAdmin = findViewById(R.id.rbAdmin);
        
        // Set default selection
        rbUser.setChecked(true);
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
        boolean isAdmin = rbAdmin.isChecked();

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

        // Check if email already exists
        if (isEmailAlreadyRegistered(email)) {
            emailLayout.setError("Email already registered");
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordLayout.setError("Password must be at least 6 characters long");
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

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number is required");
            return;
        }

        // Remove any non-digit characters
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() != 10) {
            phoneLayout.setError("Phone number must be exactly 10 digits");
            return;
        }
        
        // Update phone with clean format
        phone = cleanPhone;

        // Register user
        registerUser(name, email, password, phone, isAdmin);
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        phoneLayout.setError(null);
    }

    private boolean isEmailAlreadyRegistered(String email) {
        Set<String> registeredEmails = sharedPreferences.getStringSet("registeredEmails", new HashSet<>());
        return registeredEmails.contains(email);
    }

    private void registerUser(String name, String email, String password, String phone, boolean isAdmin) {
        try {
            // Get existing registered emails and create a new set
            Set<String> existingEmails = sharedPreferences.getStringSet("registeredEmails", new HashSet<>());
            Set<String> newRegisteredEmails = new HashSet<>(existingEmails);
            newRegisteredEmails.add(email);
            
            // Save user data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("registeredEmails", newRegisteredEmails);
            
            // Save user details with email as key
            String userKey = "user_" + email.replace("@", "_").replace(".", "_");
            editor.putString(userKey + "_name", name);
            editor.putString(userKey + "_email", email);
            editor.putString(userKey + "_password", password); // In real app, hash this
            editor.putString(userKey + "_phone", phone);
            editor.putBoolean(userKey + "_isAdmin", isAdmin);
            editor.putLong(userKey + "_registeredAt", System.currentTimeMillis());
            
            editor.apply();

            String userType = isAdmin ? "Admin" : "User";
            Toast.makeText(this, "Registration successful! Welcome " + name + " (" + userType + ")", Toast.LENGTH_LONG).show();
            
            // Auto-login after registration
            autoLogin(email, isAdmin);
            
        } catch (Exception e) {
            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void autoLogin(String email, boolean isAdmin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userEmail", email);
        editor.putBoolean("isAdmin", isAdmin);
        editor.putString("userType", isAdmin ? "Admin" : "User");
        editor.apply();

        Intent intent;
        if (isAdmin) {
            intent = new Intent(UserRegistrationActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(UserRegistrationActivity.this, MainActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}