package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class TrustedContactsActivity extends AppCompatActivity {

    private EditText etContact1, etContact2, etContact3, etContact4, etMessage;
    private TextInputLayout contact1Layout, contact2Layout, contact3Layout, contact4Layout, messageLayout;
    private Button btnSave, btnTryIt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        initializeViews();
        loadSavedContacts();
        setupClickListeners();
    }

    private void initializeViews() {
        etContact1 = findViewById(R.id.etContact1);
        etContact2 = findViewById(R.id.etContact2);
        etContact3 = findViewById(R.id.etContact3);
        etContact4 = findViewById(R.id.etContact4);
        etMessage = findViewById(R.id.etMessage);
        
        contact1Layout = findViewById(R.id.contact1Layout);
        contact2Layout = findViewById(R.id.contact2Layout);
        contact3Layout = findViewById(R.id.contact3Layout);
        contact4Layout = findViewById(R.id.contact4Layout);
        messageLayout = findViewById(R.id.messageLayout);
        
        btnSave = findViewById(R.id.btnSave);
        btnTryIt = findViewById(R.id.btnTryIt);
    }

    private void loadSavedContacts() {
        // Load saved contacts
        etContact1.setText(sharedPreferences.getString("contact1", ""));
        etContact2.setText(sharedPreferences.getString("contact2", ""));
        etContact3.setText(sharedPreferences.getString("contact3", ""));
        etContact4.setText(sharedPreferences.getString("contact4", ""));
        
        // Load saved message or use default
        String savedMessage = sharedPreferences.getString("emergency_message", "");
        if (TextUtils.isEmpty(savedMessage)) {
            savedMessage = "I am in danger, please come fast...";
        }
        etMessage.setText(savedMessage);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveContacts());
        btnTryIt.setOnClickListener(v -> testEmergencyMessage());
    }

    private void saveContacts() {
        String contact1 = etContact1.getText().toString().trim();
        String contact2 = etContact2.getText().toString().trim();
        String contact3 = etContact3.getText().toString().trim();
        String contact4 = etContact4.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        // Validate at least one contact
        if (TextUtils.isEmpty(contact1) && TextUtils.isEmpty(contact2) && 
            TextUtils.isEmpty(contact3) && TextUtils.isEmpty(contact4)) {
            Toast.makeText(this, "Please add at least one trusted contact", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate message
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Please enter an emergency message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contact1", contact1);
        editor.putString("contact2", contact2);
        editor.putString("contact3", contact3);
        editor.putString("contact4", contact4);
        editor.putString("emergency_message", message);
        editor.apply();

        Toast.makeText(this, "Trusted contacts saved successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void testEmergencyMessage() {
        String contact1 = etContact1.getText().toString().trim();
        String contact2 = etContact2.getText().toString().trim();
        String contact3 = etContact3.getText().toString().trim();
        String contact4 = etContact4.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(contact1) && TextUtils.isEmpty(contact2) && 
            TextUtils.isEmpty(contact3) && TextUtils.isEmpty(contact4)) {
            Toast.makeText(this, "Please add at least one contact to test", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create emergency message with location
        String emergencyMessage = "🚨 EMERGENCY SOS 🚨\n" +
                message + "\n" +
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
    }

    private String getLocationString() {
        // Get real GPS location
        android.location.LocationManager locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            android.location.Location location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            if (location != null) {
                return "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude();
            }
        } catch (SecurityException e) {
            // Handle permission denied
        }
        return "Location: GPS coordinates available";
    }
} 