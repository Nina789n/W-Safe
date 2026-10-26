package com.womensafety.shajt3ch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserDetailActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfilePhone, tvProfileEmail, tvProfileLastActive;
    private TextView tvProfileStatus, tvEmergencyContact1, tvEmergencyContact2;
    private TextView tvCurrentLocation, tvLocation1, tvLocation2;
    private Button btnCallUser, btnSendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("User Profile");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initializeViews();
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileLastActive = findViewById(R.id.tvProfileLastActive);
        tvProfileStatus = findViewById(R.id.tvProfileStatus);
        tvEmergencyContact1 = findViewById(R.id.tvEmergencyContact1);
        tvEmergencyContact2 = findViewById(R.id.tvEmergencyContact2);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        tvLocation1 = findViewById(R.id.tvLocation1);
        tvLocation2 = findViewById(R.id.tvLocation2);
        btnCallUser = findViewById(R.id.btnCallUser);
        btnSendSMS = findViewById(R.id.btnSendSMS);
    }

    private void loadUserData() {
        // Get user data from intent
        String userName = getIntent().getStringExtra("userName");
        String userPhone = getIntent().getStringExtra("userPhone");
        String userEmail = getIntent().getStringExtra("userEmail");
        String userStatus = getIntent().getStringExtra("userStatus");

        if (userName != null) {
            tvProfileName.setText(userName);
            // Set initials
            String[] nameParts = userName.split(" ");
            String initials = "";
            for (String part : nameParts) {
                if (!part.isEmpty()) {
                    initials += part.charAt(0);
                }
            }
            TextView tvInitials = findViewById(R.id.tvProfileInitials);
            if (tvInitials != null) {
                tvInitials.setText(initials.toUpperCase());
            }
        }

        if (userPhone != null) {
            tvProfilePhone.setText(userPhone);
        }

        if (userEmail != null) {
            tvProfileEmail.setText(userEmail);
        }

        if (userStatus != null) {
            tvProfileStatus.setText(userStatus);
            // Update status badge color
            updateStatusBadge(userStatus);
        }

        // Set sample data for demo
        tvProfileLastActive.setText("2 minutes ago");
        tvEmergencyContact1.setText("+91-9876543211");
        tvEmergencyContact2.setText("+91-9876543212");
        tvCurrentLocation.setText("Mumbai Central Station");
        tvLocation1.setText("Andheri West");
        tvLocation2.setText("Bandra Kurla Complex");
    }

    private void updateStatusBadge(String status) {
        // Update status badge color based on status
        // This would be implemented with proper status management
    }

    private void setupClickListeners() {
        btnCallUser.setOnClickListener(v -> {
            String phoneNumber = tvProfilePhone.getText().toString();
            if (!phoneNumber.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnSendSMS.setOnClickListener(v -> {
            String phoneNumber = tvProfilePhone.getText().toString();
            if (!phoneNumber.isEmpty()) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
                smsIntent.putExtra("sms_body", "Hello, this is an emergency check from W-Safe admin.");
                startActivity(smsIntent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}