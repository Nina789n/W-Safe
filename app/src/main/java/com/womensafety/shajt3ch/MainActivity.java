package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isAdmin = false;
    private String userType = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);
        userType = sharedPreferences.getString("userType", "User");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        setupHomepageCardListeners();
    }

    private void setupHomepageCardListeners() {
        // Same as before: set up click listeners for each card
        // ... (existing card click logic here) ...
        View cardEmergencySOS = findViewById(R.id.cardEmergencySOS);
        if (cardEmergencySOS != null) {
            cardEmergencySOS.setOnClickListener(v -> {
                try {
                    Intent sosIntent = new Intent(MainActivity.this, SOSActivity.class);
                    startActivity(sosIntent);
                } catch (Exception e) {
                    Toast.makeText(this, "SOS error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardInstructions = findViewById(R.id.cardInstructions);
        if (cardInstructions != null) {
            cardInstructions.setOnClickListener(v -> {
                try {
                    String instructions = isAdmin ?
                            "Admin Instructions:\n1. Monitor emergency alerts\n2. Manage user accounts\n3. View system statistics\n4. Configure safety settings" :
                            "User Instructions:\n1. Register your emergency contacts\n2. Press SOS button in emergency\n3. Stay safe!";
                    new AlertDialog.Builder(this)
                            .setTitle("Instructions")
                            .setMessage(instructions)
                            .setPositiveButton("OK", null)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(this, "Instructions error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardRegisterContacts = findViewById(R.id.cardRegisterContacts);
        if (cardRegisterContacts != null) {
            cardRegisterContacts.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Register error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardTrustedContacts = findViewById(R.id.cardTrustedContacts);
        if (cardTrustedContacts != null) {
            cardTrustedContacts.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, TrustedContactsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Trusted contacts error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardMyContacts = findViewById(R.id.cardMyContacts);
        if (cardMyContacts != null) {
            cardMyContacts.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "My contacts error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardLiveLocation = findViewById(R.id.cardLiveLocation);
        if (cardLiveLocation != null) {
            cardLiveLocation.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Live Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Handle logout
            new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
