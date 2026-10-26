package com.womensafety.shajt3ch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvActiveSOS, tvTotalUsers, tvOnlineUsers, tvFeedbackPending;
    private TextInputEditText etSearch;
    private Button btnFilterAll, btnFilterSafe, btnFilterAlert, btnFilterSOS;
    private UserProfileAdapter userAdapter;
    private List<UserProfile> allUsers;
    private List<UserProfile> filteredUsers;
    private String currentFilter = "All";
    private Handler handler;
    private Runnable updateRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Admin Dashboard");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }

        initializeViews();
        setupRecyclerView();
        loadUserData();
        setupClickListeners();
        updateStats();
        startRealTimeUpdates();
    }

    private void initializeViews() {
        try {
            recyclerView = findViewById(R.id.recyclerUsers);
            tvActiveSOS = findViewById(R.id.tvActiveSOS);
            tvTotalUsers = findViewById(R.id.tvTotalUsers);
            tvOnlineUsers = findViewById(R.id.tvOnlineUsers);
            tvFeedbackPending = findViewById(R.id.tvFeedbackPending);
            etSearch = findViewById(R.id.etSearch);
            btnFilterAll = findViewById(R.id.btnFilterAll);
            btnFilterSafe = findViewById(R.id.btnFilterSafe);
            btnFilterAlert = findViewById(R.id.btnFilterAlert);
            btnFilterSOS = findViewById(R.id.btnFilterSOS);

            allUsers = new ArrayList<>();
            filteredUsers = new ArrayList<>();
            
            handler = new Handler(Looper.getMainLooper());
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView not found", Toast.LENGTH_SHORT).show();
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserProfileAdapter(filteredUsers, this::onUserClick);
        recyclerView.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        if (etSearch == null || btnFilterAll == null || btnFilterSafe == null || 
            btnFilterAlert == null || btnFilterSOS == null) {
            Toast.makeText(this, "Some views not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter buttons
        btnFilterAll.setOnClickListener(v -> setFilter("All"));
        btnFilterSafe.setOnClickListener(v -> setFilter("Safe"));
        btnFilterAlert.setOnClickListener(v -> setFilter("Alert"));
        btnFilterSOS.setOnClickListener(v -> setFilter("SOS"));
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateFilterButtons();
        filterUsers();
    }

    private void updateFilterButtons() {
        // Reset all buttons
        btnFilterAll.setSelected(false);
        btnFilterSafe.setSelected(false);
        btnFilterAlert.setSelected(false);
        btnFilterSOS.setSelected(false);

        // Select current filter
        switch (currentFilter) {
            case "All":
                btnFilterAll.setSelected(true);
                break;
            case "Safe":
                btnFilterSafe.setSelected(true);
                break;
            case "Alert":
                btnFilterAlert.setSelected(true);
                break;
            case "SOS":
                btnFilterSOS.setSelected(true);
                break;
        }
    }

    private void filterUsers() {
        filteredUsers.clear();
        String searchText = etSearch.getText().toString().toLowerCase();

        for (UserProfile user : allUsers) {
            boolean matchesSearch = user.getName().toLowerCase().contains(searchText) ||
                    user.getPhone().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);

            boolean matchesFilter = currentFilter.equals("All") || user.getStatus().equals(currentFilter);

            if (matchesSearch && matchesFilter) {
                filteredUsers.add(user);
            }
        }

        userAdapter.notifyDataSetChanged();
    }

    private void onUserClick(UserProfile user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("userName", user.getName());
        intent.putExtra("userPhone", user.getPhone());
        intent.putExtra("userEmail", user.getEmail());
        intent.putExtra("userStatus", user.getStatus());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE).edit();
        editor.putBoolean("isLoggedIn", false);
        editor.remove("userEmail");
        editor.remove("isAdmin");
        editor.remove("userType");
        editor.apply();

        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        allUsers.clear();
        
        // Create sample users in SharedPreferences if they don't exist
        createSampleUsers();
        
        // Load users from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        Set<String> registeredEmails = prefs.getStringSet("registeredEmails", new HashSet<>());
        
        for (String email : registeredEmails) {
            String userKey = "user_" + email.replace("@", "_").replace(".", "_");
            String name = prefs.getString(userKey + "_name", "Unknown");
            String phone = prefs.getString(userKey + "_phone", "N/A");
            String status = prefs.getString(userKey + "_status", "Safe");
            
            // Calculate last active time
            long lastActiveTime = 0;
            try {
                String lastActiveStr = prefs.getString(userKey + "_lastActive", "0");
                lastActiveTime = Long.parseLong(lastActiveStr);
            } catch (Exception e) {
                lastActiveTime = 0;
            }
            String lastActive = "Never";
            if (lastActiveTime > 0) {
                long diff = System.currentTimeMillis() - lastActiveTime;
                long minutes = diff / 60000;
                if (minutes < 1) {
                    lastActive = "Just now";
                } else if (minutes < 60) {
                    lastActive = minutes + " min ago";
                } else {
                    long hours = minutes / 60;
                    lastActive = hours + " hour" + (hours > 1 ? "s" : "") + " ago";
                }
            }
            
            allUsers.add(new UserProfile(name, email, phone, status, lastActive));
        }
        
        filterUsers();
        updateStats();
    }

    private void createSampleUsers() {
        SharedPreferences prefs = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        Set<String> registeredEmails = prefs.getStringSet("registeredEmails", new HashSet<>());
        
        // Sample users data
        String[][] sampleUsers = {
            {"Sarah Johnson", "sarah@example.com", "+91-9876543210", "Safe", "2 min ago"},
            {"Priya Sharma", "priya@example.com", "+91-8765432109", "SOS", "1 min ago"},
            {"Meera Patel", "meera@example.com", "+91-7654321098", "Alert", "5 min ago"},
            {"Anita Singh", "anita@example.com", "+91-6543210987", "Safe", "10 min ago"},
            {"Kavya Reddy", "kavya@example.com", "+91-5432109876", "Safe", "15 min ago"}
        };
        
        SharedPreferences.Editor editor = prefs.edit();
        
        for (String[] user : sampleUsers) {
            String email = user[1];
            
            // Add email to registered emails if not already present
            if (!registeredEmails.contains(email)) {
                registeredEmails.add(email);
                
                // Save user data
                String userKey = "user_" + email.replace("@", "_").replace(".", "_");
                editor.putString(userKey + "_name", user[0]);
                editor.putString(userKey + "_email", user[1]);
                editor.putString(userKey + "_phone", user[2]);
                editor.putString(userKey + "_status", user[3]);
                editor.putString(userKey + "_lastActive", user[4]);
                editor.putString(userKey + "_password", "password123"); // Default password
                editor.putBoolean(userKey + "_isAdmin", false);
                editor.putLong(userKey + "_registeredAt", System.currentTimeMillis());
            }
        }
        
        // Save updated registered emails
        editor.putStringSet("registeredEmails", registeredEmails);
        editor.apply();
    }

    private void updateStats() {
        int totalUsers = allUsers.size();
        int activeSOS = 0;
        int onlineUsers = 0;
        int feedbackPending = 5; // Sample data
        
        SharedPreferences prefs = getSharedPreferences("WomenSafetyApp", MODE_PRIVATE);
        
        for (UserProfile user : allUsers) {
            // Check if user is currently logged in (within last 5 minutes)
            String userKey = "user_" + user.getEmail().replace("@", "_").replace(".", "_");
            long lastActive = 0;
            try {
                String lastActiveStr = prefs.getString(userKey + "_lastActive", "0");
                lastActive = Long.parseLong(lastActiveStr);
            } catch (Exception e) {
                lastActive = 0;
            }
            long currentTime = System.currentTimeMillis();
            // Consider user online if active within last 5 minutes
            if (lastActive > 0 && (currentTime - lastActive) < 300000) {
                onlineUsers++;
            }
            
            // Check for SOS status
            String currentStatus = prefs.getString(userKey + "_status", "Safe");
            if ("SOS".equals(currentStatus)) {
                activeSOS++;
            }
        }
        
        if (tvTotalUsers != null) tvTotalUsers.setText(String.valueOf(totalUsers));
        if (tvActiveSOS != null) tvActiveSOS.setText(String.valueOf(activeSOS));
        if (tvOnlineUsers != null) tvOnlineUsers.setText(String.valueOf(onlineUsers));
        if (tvFeedbackPending != null) tvFeedbackPending.setText(String.valueOf(feedbackPending));
    }
    

    private void startRealTimeUpdates() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updateStats();
                handler.postDelayed(this, 5000); // Update every 5 seconds
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

}

