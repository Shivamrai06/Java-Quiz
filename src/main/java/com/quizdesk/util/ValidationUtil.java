package com.quizdesk.util;

import java.util.regex.Pattern;

/** Server-side validation. Browser validation is convenience, never a defence. */
public final class ValidationUtil {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static final int MIN_PASSWORD_LENGTH = 6;

    private ValidationUtil() { }

    public static boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }

    public static boolean isNotBlank(String value) { return !isBlank(value); }

    public static String trimToNull(String value) {
        if (value == null) { return null; }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static boolean isValidEmail(String email) {
        return isNotBlank(email) && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static String requireText(String value, String field, String label, int maxLength) {
        String trimmed = trimToNull(value);
        if (trimmed == null) { throw new AppException(field, label + " is required."); }
        if (trimmed.length() > maxLength) {
            throw new AppException(field, label + " must be at most " + maxLength + " characters.");
        }
        return trimmed;
    }

    public static long requireId(String raw, String label) {
        if (isBlank(raw)) { throw new AppException("A valid " + label + " is required."); }
        try {
            long parsed = Long.parseLong(raw.trim());
            if (parsed <= 0) { throw new NumberFormatException("not positive"); }
            return parsed;
        } catch (NumberFormatException e) {
            throw new AppException("A valid " + label + " is required.");
        }
    }

    public static Integer parseOptionalInt(String raw, String field, String label, int min, int max) {
        String trimmed = trimToNull(raw);
        if (trimmed == null) { return null; }
        try {
            int value = Integer.parseInt(trimmed);
            if (value < min || value > max) {
                throw new AppException(field, label + " must be between " + min + " and " + max + ".");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new AppException(field, label + " must be a whole number.");
        }
    }

    public static String escapeHtml(String input) {
        if (input == null) { return ""; }
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
