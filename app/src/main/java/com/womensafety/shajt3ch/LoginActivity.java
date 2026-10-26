package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private RadioGroup rgUserType;
    private RadioButton rbAdmin, rbUser;
    private SharedPreferences sharedPreferences;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("autoLoginAdmin", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putBoolean("isAdmin", true);
            editor.putString("userType", "Admin");
            editor.apply();
            startMainActivity();
            return;
        }

        if (isLoggedIn()) {
            startMainActivity();
            return;
        }

        initializeViews();
        prefillLastLoginDetails();
        setupClickListeners();
        
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void initializeViews() {
        try {
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            emailLayout = findViewById(R.id.emailLayout);
            passwordLayout = findViewById(R.id.passwordLayout);
            btnLogin = findViewById(R.id.btnLogin);
            btnRegister = findViewById(R.id.btnRegister);
            tvForgotPassword = findViewById(R.id.tvForgotPassword);
            rgUserType = findViewById(R.id.rgUserType);
            rbAdmin = findViewById(R.id.rbAdmin);
            rbUser = findViewById(R.id.rbUser);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> validateAndLogin());
        btnRegister.setOnClickListener(v -> startUserRegistrationActivity());
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
        
        Button btnEmergencySOS = findViewById(R.id.btnEmergencySOS);
        if (btnEmergencySOS != null) {
            btnEmergencySOS.setOnClickListener(v -> sendEmergencySOS());
        }
        
        if (rgUserType != null) {
            rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
                etEmail.setText("");
                etPassword.setText("");
            });
        }
    }

    private void validateAndLogin() {
        try {
            if (etEmail == null || etPassword == null || rbAdmin == null) {
                Toast.makeText(this, "Please wait, initializing...", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean isAdmin = rbAdmin.isChecked();

            // Clear previous errors
            if (emailLayout != null) emailLayout.setError(null);
            if (passwordLayout != null) passwordLayout.setError(null);

            // Validate email
            if (TextUtils.isEmpty(email)) {
                if (emailLayout != null) emailLayout.setError("Email is required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (emailLayout != null) emailLayout.setError("Please enter a valid email address");
                return;
            }

            // Validate password
            if (TextUtils.isEmpty(password)) {
                if (passwordLayout != null) passwordLayout.setError("Password is required");
                return;
            }

            // Check credentials
            if (isValidCredentials(email, password, isAdmin)) {
                saveLoginState(email, password, isAdmin);
                startMainActivity();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidCredentials(String email, String password, boolean isAdmin) {
        // Check if email is registered
        Set<String> registeredEmails = sharedPreferences.getStringSet("registeredEmails", new HashSet<>());
        
        // Debug: Show what emails are registered
        Toast.makeText(this, "Registered emails: " + registeredEmails.toString(), Toast.LENGTH_SHORT).show();
        
        if (!registeredEmails.contains(email)) {
            Toast.makeText(this, "Email not found: " + email, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get user data
        String userKey = "user_" + email.replace("@", "_").replace(".", "_");
        String storedPassword = sharedPreferences.getString(userKey + "_password", "");
        boolean storedIsAdmin = sharedPreferences.getBoolean(userKey + "_isAdmin", false);

        // Debug: Show stored data
        Toast.makeText(this, "Stored password: " + storedPassword + ", Admin: " + storedIsAdmin, Toast.LENGTH_SHORT).show();

        // Validate password and admin status
        return password.equals(storedPassword) && isAdmin == storedIsAdmin;
    }

    private void saveLoginState(String email, String password, boolean isAdmin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userEmail", email);
        editor.putBoolean("isAdmin", isAdmin);
        editor.putString("userType", isAdmin ? "Admin" : "User");

        // Persist last successful login details for convenience (plain text per user's request)
        editor.putString("lastLoginEmail", email);
        editor.putString("lastLoginPassword", password);

        // Mark this specific user as logged in for admin dashboard tracking
        String userKey = "user_" + email.replace("@", "_").replace(".", "_");
        editor.putBoolean(userKey + "_isLoggedIn", true);
        editor.putLong(userKey + "_lastActive", System.currentTimeMillis());

        // Enable auto-login for admin going forward
        if (isAdmin) {
            editor.putBoolean("autoLoginAdmin", true);
        }

        editor.apply();
    }

    private void prefillLastLoginDetails() {
        String lastEmail = sharedPreferences.getString("lastLoginEmail", "");
        String lastPassword = sharedPreferences.getString("lastLoginPassword", "");
        if (!lastEmail.isEmpty()) etEmail.setText(lastEmail);
        if (!lastPassword.isEmpty()) etPassword.setText(lastPassword);
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void startMainActivity() {
        // Check if user is admin and redirect to admin dashboard
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        
        Intent intent;
        if (isAdmin) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        
        startActivity(intent);
        finish();
    }

    private void startUserRegistrationActivity() {
        Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    private void showForgotPasswordDialog() {
        Toast.makeText(this, "Contact admin for password reset", Toast.LENGTH_SHORT).show();
    }

    private void sendEmergencySOS() {
        Toast.makeText(this, "Getting location and sending to Discord...", Toast.LENGTH_SHORT).show();
        
        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            getLocationAndSendToDiscord();
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                1001);
        }
    }
    
    private void getLocationAndSendToDiscord() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        String deviceId = android.provider.Settings.Secure.getString(
                            getContentResolver(), 
                            android.provider.Settings.Secure.ANDROID_ID);
                        String shortDeviceId = deviceId != null && deviceId.length() > 8 
                            ? deviceId.substring(0, 8) : deviceId;
                        
                        String locationStr;
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            locationStr = "Lat: " + lat + ", Long: " + lon + 
                                        "\nGoogle Maps: https://maps.google.com/?q=" + lat + "," + lon;
                        } else {
                            locationStr = "Location: Unable to get GPS coordinates";
                        }
                        
                        String emergencyMessage = "🚨 **EMERGENCY SOS** 🚨\n\n" +
                                "I am in danger, please come fast...\n\n" +
                                "**Device ID:** " + shortDeviceId + "\n" +
                                "**Location:** " + locationStr + "\n" +
                                "**Time:** " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", 
                                    java.util.Locale.getDefault()).format(new java.util.Date()) + "\n\n" +
                                "**Please respond immediately!**";
                        
                        // Send to Discord
                        DiscordHelper.sendMessageToDiscord(emergencyMessage, new DiscordHelper.DiscordCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(LoginActivity.this, 
                                    "SOS sent to Discord server 'w-safe' successfully!", 
                                    Toast.LENGTH_LONG).show();
                            }
                            
                            @Override
                            public void onError(String error) {
                                Toast.makeText(LoginActivity.this, 
                                    "Failed to send to Discord: " + error, 
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSendToDiscord();
            } else {
                Toast.makeText(this, "Location permission needed for SOS", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // Prevent going back to login if already logged in
        if (isLoggedIn()) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
} 