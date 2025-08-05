package com.womensafety.shajt3ch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WomenSafetyDB";

    // Table Names
    private static final String TABLE_EMERGENCY_CONTACTS = "emergency_contacts";
    private static final String TABLE_USER_PROFILES = "user_profiles";
    private static final String TABLE_SOS_HISTORY = "sos_history";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CREATED_AT = "created_at";

    // Emergency Contacts Table - column names
    private static final String KEY_RELATIONSHIP = "relationship";
    private static final String KEY_IS_PRIMARY = "is_primary";

    // User Profiles Table - column names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ADDRESS = "address";

    // SOS History Table - column names
    private static final String KEY_SOS_DATE = "sos_date";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_STATUS = "status";

    // Table Create Statements
    private static final String CREATE_TABLE_EMERGENCY_CONTACTS = "CREATE TABLE " + TABLE_EMERGENCY_CONTACTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_RELATIONSHIP + " TEXT,"
            + KEY_IS_PRIMARY + " INTEGER DEFAULT 0,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_USER_PROFILES = "CREATE TABLE " + TABLE_USER_PROFILES + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " TEXT UNIQUE,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_ADDRESS + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TABLE_SOS_HISTORY = "CREATE TABLE " + TABLE_SOS_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_SOS_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + KEY_LOCATION + " TEXT,"
            + KEY_STATUS + " TEXT,"
            + KEY_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_EMERGENCY_CONTACTS);
        db.execSQL(CREATE_TABLE_USER_PROFILES);
        db.execSQL(CREATE_TABLE_SOS_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMERGENCY_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOS_HISTORY);

        // Create tables again
        onCreate(db);
    }

    // Emergency Contacts CRUD Operations
    public long addEmergencyContact(String name, String phone, String email, String relationship) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_EMAIL, email);
        values.put(KEY_RELATIONSHIP, relationship);

        long id = db.insert(TABLE_EMERGENCY_CONTACTS, null, values);
        db.close();
        return id;
    }

    public List<EmergencyContact> getAllEmergencyContacts() {
        List<EmergencyContact> contacts = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EMERGENCY_CONTACTS + " ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                EmergencyContact contact = new EmergencyContact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                contact.setRelationship(cursor.getString(cursor.getColumnIndex(KEY_RELATIONSHIP)));
                contact.setPrimary(cursor.getInt(cursor.getColumnIndex(KEY_IS_PRIMARY)) == 1);
                contacts.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contacts;
    }

    public int updateEmergencyContact(EmergencyContact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_RELATIONSHIP, contact.getRelationship());
        values.put(KEY_IS_PRIMARY, contact.isPrimary() ? 1 : 0);

        return db.update(TABLE_EMERGENCY_CONTACTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteEmergencyContact(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EMERGENCY_CONTACTS, KEY_ID + " = ?",
                new String[]{String.valueOf(contactId)});
        db.close();
    }

    // SOS History Operations
    public long addSOSRecord(String location, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION, location);
        values.put(KEY_STATUS, status);

        long id = db.insert(TABLE_SOS_HISTORY, null, values);
        db.close();
        return id;
    }

    public List<SOSRecord> getSOSHistory() {
        List<SOSRecord> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SOS_HISTORY + " ORDER BY " + KEY_SOS_DATE + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SOSRecord record = new SOSRecord();
                record.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                record.setSosDate(cursor.getString(cursor.getColumnIndex(KEY_SOS_DATE)));
                record.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                record.setStatus(cursor.getString(cursor.getColumnIndex(KEY_STATUS)));
                records.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return records;
    }

    // Data Classes for better organization
    public static class EmergencyContact {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String relationship;
        private boolean isPrimary;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
        public boolean isPrimary() { return isPrimary; }
        public void setPrimary(boolean primary) { isPrimary = primary; }
    }

    public static class SOSRecord {
        private int id;
        private String sosDate;
        private String location;
        private String status;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getSosDate() { return sosDate; }
        public void setSosDate(String sosDate) { this.sosDate = sosDate; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
} 