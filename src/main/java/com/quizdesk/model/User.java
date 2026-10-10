package com.quizdesk.model;

import java.sql.Timestamp;

/** A registered account. */
public class User {

    private long id;
    private String name;
    private String email;
    private String passwordHash;
    private Role role = Role.STUDENT;
    private Timestamp createdAt;
    private int attemptCount;

    public User() { }

    public User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    public String getFirstName() {
        if (name == null || name.isBlank()) { return "there"; }
        String trimmed = name.trim();
        int space = trimmed.indexOf(' ');
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }

    public String getInitials() {
        if (name == null || name.isBlank()) { return "?"; }
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder().append(Character.toUpperCase(parts[0].charAt(0)));
        if (parts.length > 1) {
            initials.append(Character.toUpperCase(parts[parts.length - 1].charAt(0)));
        }
        return initials.toString();
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + '}';
    }
}
