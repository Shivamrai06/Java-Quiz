package com.quizdesk.controller;

import com.quizdesk.model.User;
import com.quizdesk.service.AuthService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /login renders the form; POST /login authenticates.
 *
 * <p>On success the identity goes into the session and the response is a redirect - rendering the
 * dashboard directly would make a refresh re-POST the credentials.</p>
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            go(request, response, SessionUtil.isAdmin(request) ? "/admin/dashboard" : "/dashboard");
            return;
        }
        if (param(request, "expired") != null) {
            request.setAttribute("infoMessage", "Please sign in to continue.");
        }
        request.setAttribute("pageTitle", "Sign in");
        view(request, response, "login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = rawParam(request, "email");
        String password = rawParam(request, "password");
        try {
            User user = authService.login(email, password);

            // Session stores identity only - never the password, never the hash.
            SessionUtil.login(request, user.getId(), user.getName(), user.getRole());

            Object target = request.getSession().getAttribute("redirectAfterLogin");
            request.getSession().removeAttribute("redirectAfterLogin");
            if (target != null && target.toString().startsWith("/")) {
                response.sendRedirect(request.getContextPath() + target);
            } else if (user.isAdmin()) {
                go(request, response, "/admin/dashboard");
            } else {
                go(request, response, "/dashboard");
            }
        } catch (AppException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("email", email == null ? "" : email);
            request.setAttribute("pageTitle", "Sign in");
            view(request, response, "login");
        }
    }
}
