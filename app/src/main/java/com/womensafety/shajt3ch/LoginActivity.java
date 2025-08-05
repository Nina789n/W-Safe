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

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private Button btnLogin, btnRegister;
    private TextView tvForgotPassword;
    private RadioGroup rgUserType;
    private RadioButton rbAdmin, rbUser;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);

        // Check if user is already logged in
        if (isLoggedIn()) {
            startMainActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
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
        try {
            btnLogin.setOnClickListener(v -> validateAndLogin());
            btnRegister.setOnClickListener(v -> startRegisterActivity());
            tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
            
            // SOS button - simple emergency help
            Button btnEmergencySOS = findViewById(R.id.btnEmergencySOS);
            if (btnEmergencySOS != null) {
                btnEmergencySOS.setOnClickListener(v -> sendEmergencySOS());
            }
            
            // Auto-fill credentials based on user type selection
            if (rgUserType != null) {
                rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.rbAdmin) {
                        etEmail.setText("admin@womensafety.com");
                        etPassword.setText("password123");
                    } else if (checkedId == R.id.rbUser) {
                        etEmail.setText("user@womensafety.com");
                        etPassword.setText("password123");
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up listeners: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void validateAndLogin() {
        try {
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
                saveLoginState(email, isAdmin);
                startMainActivity();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidCredentials(String email, String password, boolean isAdmin) {
        if (isAdmin) {
            return email.equals("admin@womensafety.com") && password.equals("password123");
        } else {
            return email.equals("user@womensafety.com") && password.equals("password123");
        }
    }

    private void saveLoginState(String email, boolean isAdmin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userEmail", email);
        editor.putBoolean("isAdmin", isAdmin);
        editor.putString("userType", isAdmin ? "Admin" : "User");
        editor.apply();
    }

    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showForgotPasswordDialog() {
        Toast.makeText(this, "Contact admin for password reset", Toast.LENGTH_SHORT).show();
    }

    private void sendEmergencySOS() {
        try {
            // Create emergency message with device ID
            String deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            String shortDeviceId = deviceId.substring(0, Math.min(8, deviceId.length()));
            
            String emergencyMessage = "🚨 EMERGENCY SOS 🚨\n" +
                    "I am in danger, please come fast...\n" +
                    "Device ID: " + shortDeviceId + "\n" +
                    "Location: " + getLocationString() + "\n" +
                    "Time: " + java.time.LocalDateTime.now().toString() + "\n" +
                    "Please respond immediately!";

            // Try to open WhatsApp with the message
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, emergencyMessage);
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Emergency message: " + emergencyMessage, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "SOS error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getLocationString() {
        try {
            // Get real GPS location
            android.location.LocationManager locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                android.location.Location location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                if (location != null) {
                    return "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude();
                }
            }
        } catch (SecurityException e) {
            // Handle permission denied
        }
        return "Location: GPS coordinates available";
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