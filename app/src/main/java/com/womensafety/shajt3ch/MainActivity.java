package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.Manifest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
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
                            "User Instructions:\n1. Register your emergency contacts\n2. Press SOS button in emergency\n3. Share live location with contacts";

                    String selfDefense =
                            "\n\nSafety & Self-Defense Tips:\n" +
                            "1. Stay aware of your surroundings.\n" +
                            "2. Keep distance and create space to escape.\n" +
                            "3. Use a loud, firm voice to draw attention.\n" +
                            "4. Target vulnerable areas: eyes, nose, throat, groin, knees.\n" +
                            "5. Use everyday objects (keys, bag) as improvised tools.\n" +
                            "6. Run toward well-lit, populated places.\n" +
                            "7. After reaching safety, call SOS and notify contacts.";
                    new AlertDialog.Builder(this)
                            .setTitle("Instructions")
                            .setMessage(instructions + selfDefense)
                            .setPositiveButton("OK", null)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(this, "Instructions error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardTrustedContacts = findViewById(R.id.cardTrustedContacts);
        if (cardTrustedContacts != null) {
            cardTrustedContacts.setOnClickListener(v -> {
                try {
                    showTrustedContactsDialog();
                } catch (Exception e) {
                    Toast.makeText(this, "Trusted contacts error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardMyContacts = findViewById(R.id.cardMyContacts);
        if (cardMyContacts != null) {
            cardMyContacts.setOnClickListener(v -> {
                try {
                    showMyContactsDialog();
                } catch (Exception e) {
                    Toast.makeText(this, "My contacts error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        View cardLiveLocation = findViewById(R.id.cardLiveLocation);
        if (cardLiveLocation != null) {
            cardLiveLocation.setOnClickListener(v -> {
                try {
                    new AlertDialog.Builder(this)
                        .setTitle("Live Location")
                        .setMessage("Open the map")
                        .setPositiveButton("Open Map", (d, w) -> {
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                } catch (Exception e) {
                    Toast.makeText(this, "Live Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        View cardFakeActions = findViewById(R.id.cardFakeActions);
        if (cardFakeActions != null) {
            cardFakeActions.setOnClickListener(v -> {
                try {
                    showFakeActionsDialog();
                } catch (Exception e) {
                    Toast.makeText(this, "Fake actions error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        View cardAdminPanel = findViewById(R.id.cardAdminPanel);
        if (cardAdminPanel != null) {
            cardAdminPanel.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Admin panel error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void showFakeActionsDialog() {
        // Get fake contacts from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        Set<String> fakeContacts = prefs.getStringSet("fakeContacts", new HashSet<>());
        
        if (fakeContacts.isEmpty()) {
            // No contacts, show add contact dialog
            showAddFakeContactDialog();
            return;
        }
        
        // Show fake actions with existing contacts
        String[] contactNames = fakeContacts.toArray(new String[0]);
        String[] actions = new String[contactNames.length * 2];
        
        for (int i = 0; i < contactNames.length; i++) {
            actions[i * 2] = "Fake Call from " + contactNames[i];
            actions[i * 2 + 1] = "Fake SMS from " + contactNames[i];
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Fake Call / SMS")
            .setItems(actions, (d, which) -> {
                int contactIndex = which / 2;
                boolean isCall = which % 2 == 0;
                String contactName = contactNames[contactIndex];
                
                if (isCall) {
                    startFakeCall(contactName);
                } else {
                    showFakeSms(contactName);
                }
            })
            .setPositiveButton("Add Contact", (d, which) -> showAddFakeContactDialog())
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showAddFakeContactDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_fake_contact, null);
        EditText etName = dialogView.findViewById(R.id.etContactName);
        EditText etPhone = dialogView.findViewById(R.id.etContactPhone);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Add Fake Contact")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create();
            
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim().replaceAll("[^0-9]", "");
                
                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (phone.length() != 10) {
                    Toast.makeText(this, "Phone must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                addFakeContact(name, phone);
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
    
    private void addFakeContact(String name, String phone) {
        try {
            SharedPreferences prefs = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
            
            // Use same simple counter-based system
            int contactCount = prefs.getInt("contactCount", 0);
            contactCount++;
            
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("contact_" + contactCount + "_name", name);
            editor.putString("contact_" + contactCount + "_phone", phone);
            editor.putInt("contactCount", contactCount);
            
            // Also save to fake contacts for fake actions
            Set<String> existingFakeContacts = prefs.getStringSet("fakeContacts", new HashSet<>());
            Set<String> newFakeContacts = new HashSet<>(existingFakeContacts);
            newFakeContacts.add(name);
            editor.putStringSet("fakeContacts", newFakeContacts);
            editor.putString("fakeContact_" + name + "_phone", phone);
            
            boolean saved = editor.commit();
            
            if (saved) {
                Toast.makeText(this, "Contact added: " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save contact", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    // Keep last login details for convenience
                    String lastEmail = sharedPreferences.getString("lastLoginEmail", "");
                    String lastPassword = sharedPreferences.getString("lastLoginPassword", "");
                    editor.clear();
                    // Restore last login details
                    editor.putString("lastLoginEmail", lastEmail);
                    editor.putString("lastLoginPassword", lastPassword);
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

    private void showTrustedContactsDialog() {
        String[] actions = {"View Trusted Contacts", "Register New Trusted Contacts"};
        new AlertDialog.Builder(this)
                .setTitle("Trusted Contacts")
                .setItems(actions, (d, which) -> {
                    if (which == 0) {
                        // View trusted contacts
                        Intent intent = new Intent(MainActivity.this, TrustedContactsActivity.class);
                        intent.putExtra("mode", "viewTrusted");
                        startActivity(intent);
                    } else if (which == 1) {
                        // Register new trusted contact (open add dialog)
                        Intent intent = new Intent(MainActivity.this, TrustedContactsActivity.class);
                        intent.putExtra("mode", "addTrusted");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMyContactsDialog() {
        String[] actions = {"View All Contacts", "Register New Contact"};
        new AlertDialog.Builder(this)
                .setTitle("My Contacts")
                .setItems(actions, (d, which) -> {
                    if (which == 0) {
                        showAllContactsList();
                    } else if (which == 1) {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAllContactsList() {
        List<String> contacts = loadContactsFromDb();
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("All Contacts");

        if (contacts.isEmpty()) {
            builder.setMessage("No contacts yet. Add one now?")
                    .setPositiveButton("Add Contact", (d, w) -> {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("Close", null)
                    .show();
            return;
        }

        builder.setItems(contacts.toArray(new String[0]), null)
                .setPositiveButton("Add Contact", (d, w) -> {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private List<String> loadContactsFromDb() {
        List<String> contacts = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
            cursor = db.rawQuery("SELECT Pname, number FROM details", null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String phone = cursor.getString(1);
                if (name != null && phone != null && !name.trim().isEmpty() && !phone.trim().isEmpty()) {
                    contacts.add(name.trim() + " (" + phone.trim() + ")");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return contacts;
    }

    private void showFakeSmsNotification() {
        String channelId = "fake_sms_channel";
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(channelId, "Fake SMS", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setContentTitle("New message from Mom")
                .setContentText("Where are you? Call me back.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        nm.notify(1002, b.build());
    }

    private void startFakeCall(String callerName) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, FakeCallActivity.class);
            i.putExtra("callerName", callerName);
            startActivity(i);
        }, 2000);
    }

    private void showFakeSms(String sender) {
        // Let user type a fake SMS body
        final EditText input = new EditText(this);
        input.setHint("Type a fake SMS message");
        input.setPadding(50, 20, 50, 20);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Fake SMS from " + sender)
                .setView(input)
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", null)
                .create();
                
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String body = input.getText().toString().trim();
                if (body.isEmpty()) {
                    body = "Call me when you see this.";
                }
                
                // Show notification - simple and direct
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nm != null) {
                    String channelId = "fake_sms_channel";
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel ch = new NotificationChannel(channelId, "Fake SMS", NotificationManager.IMPORTANCE_HIGH);
                        ch.setDescription("Fake SMS notifications");
                        nm.createNotificationChannel(ch);
                    }
                    NotificationCompat.Builder b = new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(android.R.drawable.sym_action_chat)
                            .setContentTitle("New message from " + sender)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);
                    nm.notify(2000 + sender.hashCode(), b.build());
                }
                
                Toast.makeText(this, "Fake SMS notification sent!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
    

}
