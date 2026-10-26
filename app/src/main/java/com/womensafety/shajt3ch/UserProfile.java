package com.womensafety.shajt3ch;

public class UserProfile {
    private String name;
    private String email;
    private String phone;
    private String status;
    private String lastActive;

    public UserProfile(String name, String email, String phone, String status, String lastActive) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.lastActive = lastActive;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getLastActive() { return lastActive; }

    public String getInitials() {
        String[] nameParts = name.split(" ");
        String initials = "";
        for (String part : nameParts) {
            if (!part.isEmpty()) {
                initials += part.charAt(0);
            }
        }
        return initials.toUpperCase();
    }
}