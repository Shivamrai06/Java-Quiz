package com.quizdesk.filter;

import com.quizdesk.model.Role;
import com.quizdesk.util.SessionUtil;
import com.quizdesk.util.StatsCounter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Authentication and role-based authorization for every protected route.
 *
 * <p>One filter instead of a session check copied into every servlet: a new protected page cannot
 * ship without a guard, because the URL pattern list in web.xml is the single source of truth.</p>
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        StatsCounter.requestServed();

        // ---- Rule 1: an anonymous request must sign in first ----
        if (!SessionUtil.isLoggedIn(request)) {
            String target = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null) { target = target + '?' + query; }
            request.getSession(true).setAttribute("redirectAfterLogin", target);
            response.sendRedirect(request.getContextPath() + "/login?expired=1");
            return;
        }

        // ---- Rule 2: /admin/* requires the ADMIN role ----
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (path.startsWith("/admin") && SessionUtil.currentRole(request) != Role.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("errorTitle", "Not authorized");
            request.setAttribute("errorMessage",
                    "You are not authorized to access this page. It is restricted to administrators.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
