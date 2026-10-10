package com.quizdesk.util;

import org.mindrot.jbcrypt.BCrypt;

/** BCrypt password hashing. Plain-text passwords are never stored or logged. */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 10;

    private PasswordUtil() { }

    public static String hash(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null || storedHash.isEmpty()) { return false; }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException malformed) {
            return false; // fail closed
        }
    }
}
