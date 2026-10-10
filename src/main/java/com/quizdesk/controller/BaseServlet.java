package com.quizdesk.controller;

import com.quizdesk.util.AppException;
import com.quizdesk.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Shared plumbing for every servlet.
 *
 * <p><strong>Thread safety:</strong> this class declares no mutable instance fields, and neither do
 * the subclasses. Tomcat creates ONE servlet instance and calls it from many threads at the same
 * time, so per-request state must live in local variables or in the request/session scope.</p>
 *
 * <p>Views live under {@code /WEB-INF/views/}, which a browser cannot request directly - a JSP can
 * never be opened without its servlet having prepared the data first.</p>
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Per-class logger. Safe because Logger is itself thread-safe. */
    protected Logger log() { return Logger.getLogger(getClass().getName()); }

    /** Forwards to a view under /WEB-INF/views/ ("login" -> /WEB-INF/views/login.jsp). */
    protected void view(HttpServletRequest request, HttpServletResponse response, String name)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + name + ".jsp").forward(request, response);
    }

    /** Sends a redirect (POST -&gt; Redirect -&gt; GET). */
    protected void go(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    /** Renders the shared error page with a message that is safe for the end user. */
    protected void showError(HttpServletRequest request, HttpServletResponse response,
                             String title, String message, int status)
            throws ServletException, IOException {
        response.setStatus(status);
        request.setAttribute("errorTitle", title);
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    /** Wraps an application failure: the detail is logged, the user sees the friendly sentence. */
    protected void handleAppFailure(HttpServletRequest request, HttpServletResponse response,
                                    AppException failure) throws ServletException, IOException {
        log().warning("Application error on " + request.getRequestURI() + ": " + failure.getMessage());
        showError(request, response, "Something went wrong", failure.getMessage(),
                HttpServletResponse.SC_BAD_REQUEST);
    }

    // ---- parameter helpers: every servlet validates its own input through these ----

    /** @return the trimmed parameter, or {@code null} when absent or blank. */
    protected String param(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null) { return null; }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** @return the raw parameter, or {@code null} when absent. Used when blank must be preserved. */
    protected String rawParam(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    protected long currentUserId(HttpServletRequest request) {
        Long id = SessionUtil.currentUserId(request);
        return id == null ? -1L : id;
    }
}
