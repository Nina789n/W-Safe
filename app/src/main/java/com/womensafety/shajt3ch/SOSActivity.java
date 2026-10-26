package com.womensafety.shajt3ch;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class SOSActivity extends AppCompatActivity {

    private Button btnSOS;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        btnSOS = findViewById(R.id.btnSOS);

        if (btnSOS != null) {
            btnSOS.setOnClickListener(v -> sendSOSAlert());
        }
    }

    private void sendSOSAlert() {
        if (checkLocationPermission()) {
            getCurrentLocationAndSendAlert();
        } else {
            requestLocationPermission();
        }
    }

    private void getCurrentLocationAndSendAlert() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                sendEmergencyAlerts(location);
                                String locationStr = location.getLatitude() + "," + location.getLongitude();
                                databaseHelper.addSOSRecord(locationStr, "SENT");
                            } else {
                                Toast.makeText(SOSActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendEmergencyAlerts(Location location) {
        // Build trusted contacts list from SharedPreferences
        String[] trustedNames = {"name1", "name2", "name3", "name4"};
        String[] trustedContacts = {"contact1", "contact2", "contact3", "contact4"};
        java.util.List<String> phones = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = sharedPreferences.getString(trustedNames[i], "");
            String phone = sharedPreferences.getString(trustedContacts[i], "");
            if (!name.isEmpty() && !phone.isEmpty()) {
                phones.add(phone);
            }
        }

        if (phones.isEmpty()) {
            Toast.makeText(this, "No Trusted Contacts found. Please add and mark as trusted.", Toast.LENGTH_LONG).show();
            return;
        }

        String locationMessage = "SOS ALERT! I need immediate help!\n" +
                "My location: https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude() + "\n" +
                "Coordinates: " + location.getLatitude() + "," + location.getLongitude();

        int successCount = 0;
        // Send SMS to all trusted contacts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phone : phones) {
                try {
                    smsManager.sendTextMessage(phone, null, locationMessage, null, null);
                    successCount++;
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to send SMS to " + phone, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1002);
        }

        // Make emergency call to first trusted contact
        if (!phones.isEmpty()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phones.get(0)));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to make emergency call", Toast.LENGTH_SHORT).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1003);
            }
        }

        Toast.makeText(this, "SOS Alert sent to " + successCount + " trusted contacts", Toast.LENGTH_LONG).show();
        
        // Update user status to SOS in SharedPreferences
        updateUserStatus("SOS");
    }
    
    private void updateUserStatus(String status) {
        try {
            String userEmail = sharedPreferences.getString("userEmail", "");
            if (!userEmail.isEmpty()) {
                String userKey = "user_" + userEmail.replace("@", "_").replace(".", "_");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(userKey + "_status", status);
                editor.putLong(userKey + "_lastActive", System.currentTimeMillis());
                editor.apply();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, 
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
            LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndSendAlert();
            } else {
                Toast.makeText(this, "Location permission required for SOS alerts", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
} 