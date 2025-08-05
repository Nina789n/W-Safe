package com.womensafety.shajt3ch;

import android.Manifest;
import android.content.Intent;
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
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnSOS = findViewById(R.id.btnSOS);

        btnSOS.setOnClickListener(v -> sendSOSAlert());
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
                                // Save SOS record to database
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
        // Get emergency contacts from database
        List<DatabaseHelper.EmergencyContact> emergencyContacts = databaseHelper.getAllEmergencyContacts();
        
        if (emergencyContacts.isEmpty()) {
            Toast.makeText(this, "No emergency contacts found. Please add contacts first.", Toast.LENGTH_LONG).show();
            return;
        }

        String locationMessage = "SOS ALERT! I need immediate help! My location: " +
                "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();

        int successCount = 0;
        // Send SMS to all emergency contacts
        SmsManager smsManager = SmsManager.getDefault();
        for (DatabaseHelper.EmergencyContact contact : emergencyContacts) {
            try {
                smsManager.sendTextMessage(contact.getPhone(), null, locationMessage, null, null);
                successCount++;
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS to " + contact.getName(), Toast.LENGTH_SHORT).show();
            }
        }

        // Make emergency call to first contact
        if (!emergencyContacts.isEmpty()) {
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + emergencyContacts.get(0).getPhone()));
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to make emergency call", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "SOS Alert sent to " + successCount + " contacts", Toast.LENGTH_LONG).show();
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