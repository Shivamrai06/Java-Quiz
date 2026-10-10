package com.quizdesk.util;

/** A failure whose message is safe to show to the end user. */
public class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String field;

    public AppException(String message) { this(null, message); }

    public AppException(String field, String message) {
        super(message);
        this.field = field;
    }

    public AppException(String message, Throwable cause) { super(message, cause); this.field = null; }

    public String getField() { return field; }
}
