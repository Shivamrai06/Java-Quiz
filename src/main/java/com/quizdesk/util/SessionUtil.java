package com.quizdesk.util;

import com.quizdesk.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** All session access goes through here. Identity only - never a password. */
public final class SessionUtil {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    public static final String USER_ROLE = "userRole";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";

    private SessionUtil() { }

    public static void login(HttpServletRequest request, long userId, String name, Role role) {
        HttpSession existing = request.getSession(false);
        if (existing != null) { existing.invalidate(); }
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_ID, userId);
        session.setAttribute(USER_NAME, name);
        session.setAttribute(USER_ROLE, role.name());
        StatsCounter.sessionOpened();
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            StatsCounter.sessionClosed();
        }
    }

    public static Long currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) { return null; }
        Object value = session.getAttribute(USER_ID);
        return (value instanceof Long id) ? id : null;
    }

    public static String currentUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(USER_NAME);
    }

    public static Role currentRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) { return null; }
        Object value = session.getAttribute(USER_ROLE);
        return value == null ? null : Role.fromDatabase(value.toString());
    }

    public static boolean isLoggedIn(HttpServletRequest request) { return currentUserId(request) != null; }

    public static boolean isAdmin(HttpServletRequest request) { return currentRole(request) == Role.ADMIN; }

    public static void flashSuccess(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_SUCCESS, message);
    }

    public static void flashError(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_ERROR, message);
    }
}
