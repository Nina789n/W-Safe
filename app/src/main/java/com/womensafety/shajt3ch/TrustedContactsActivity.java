package com.womensafety.shajt3ch;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TrustedContactsActivity extends AppCompatActivity {

    private Button btnViewContacts, btnViewTrusted, btnAddContact;
    private SharedPreferences sharedPreferences;
    private boolean isAddTrustedMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trusted_contacts);

        sharedPreferences = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        initializeViews();
        setupClickListeners();

        String mode = getIntent().getStringExtra("mode");
        isAddTrustedMode = "addTrusted".equals(mode);
        if (isAddTrustedMode) {
            btnAddContact.post(this::showAddContactDialog);
        } else if ("viewTrusted".equals(mode)) {
            btnViewTrusted.post(this::showTrustedContacts);
        }
    }

    private void initializeViews() {
        btnViewContacts = findViewById(R.id.btnViewContacts);
        btnViewTrusted = findViewById(R.id.btnViewTrustedContacts);
        btnAddContact = findViewById(R.id.btnAddContact);
    }

    private void setupClickListeners() {
        btnViewContacts.setOnClickListener(v -> showAllContacts());
        btnViewTrusted.setOnClickListener(v -> showTrustedContacts());
        btnAddContact.setOnClickListener(v -> showAddContactDialog());
    }

    private void showAllContacts() {
        java.util.List<String> allContacts = loadContactsFromDb();
        if (allContacts.isEmpty()) {
            allContacts.add("No contacts yet — tap to add");
        }

        new android.app.AlertDialog.Builder(this)
            .setTitle("All Contacts (" + (allContacts.size() - (allContacts.get(0).startsWith("No") ? 1 : 0)) + ")")
            .setItems(allContacts.toArray(new String[0]), (d, which) -> {
                String selectedContact = allContacts.get(which);
                if (selectedContact.startsWith("No contacts yet")) {
                    showAddContactDialog();
                } else {
                    markAsTrusted(selectedContact);
                }
            })
            .setPositiveButton("Close", null)
            .show();
    }

    private java.util.List<String> loadContactsFromDb() {
        java.util.List<String> contacts = new java.util.ArrayList<>();
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

    private void showTrustedContacts() {
        String[] trustedNames = {"name1", "name2", "name3", "name4"};
        String[] trustedContacts = {"contact1", "contact2", "contact3", "contact4"};

        java.util.List<String> trustedList = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = sharedPreferences.getString(trustedNames[i], "");
            String contact = sharedPreferences.getString(trustedContacts[i], "");
            if (!name.isEmpty() && !contact.isEmpty()) {
                trustedList.add("• " + name + " (" + contact + ")");
            }
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle("Trusted Contacts");

        if (trustedList.isEmpty()) {
            builder.setMessage("• (none yet)")
                    .setPositiveButton("OK", null);
        } else {
            builder.setItems(trustedList.toArray(new String[0]), null)
                    .setPositiveButton("OK", null);
        }

        builder.show();
    }
    
    private void markAsTrusted(String contactInfo) {
        // Extract name and phone from "Name (Phone)" format
        String name = contactInfo.substring(0, contactInfo.indexOf(" ("));
        String phone = contactInfo.substring(contactInfo.indexOf("(") + 1, contactInfo.indexOf(")"));
        saveTrustedContact(name, phone);
    }
        
    private void saveTrustedContact(String name, String phone) {
        String[] trustedNames = {"name1", "name2", "name3", "name4"};
        String[] trustedContacts = {"contact1", "contact2", "contact3", "contact4"};
        
        for (int i = 0; i < 4; i++) {
            String existingName = sharedPreferences.getString(trustedNames[i], "");
            if (existingName.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(trustedNames[i], name);
                editor.putString(trustedContacts[i], phone);
                editor.apply();
                
                Toast.makeText(this, name + " marked as trusted contact!", Toast.LENGTH_SHORT).show();
            return;
            }
        }
        
        Toast.makeText(this, "All trusted contact slots are full. Remove one first.", Toast.LENGTH_SHORT).show();
    }
    
    private void showAddContactDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_fake_contact, null);
        EditText etName = dialogView.findViewById(R.id.etContactName);
        EditText etPhone = dialogView.findViewById(R.id.etContactPhone);
        
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
            .setTitle("Add New Contact")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create();
            
        dialog.setOnShowListener(d -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
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
                
                addNewContact(name, phone, isAddTrustedMode);
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
    
    private void addNewContact(String name, String phone, boolean markTrusted) {
        SQLiteDatabase db = null;
        try {
            db = openOrCreateDatabase("NumberDB", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS details(Pname VARCHAR,number VARCHAR);");
            db.execSQL("INSERT INTO details VALUES('" + name + "','" + phone + "');");

                Toast.makeText(this, "Contact added: " + name, Toast.LENGTH_SHORT).show();
            if (markTrusted) {
                saveTrustedContact(name, phone);
            }
            showAllContacts();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
} 