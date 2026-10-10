package com.quizdesk.model;

/** The two roles supported by the platform. */
public enum Role {

    STUDENT,
    ADMIN;

    public static Role fromDatabase(String value) {
        if (value == null) { return STUDENT; }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return STUDENT;
        }
    }
}
