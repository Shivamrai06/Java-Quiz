package com.quizdesk.controller;

import com.quizdesk.service.AuthService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /register renders the form; POST /register creates the account. */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(request)) {
            go(request, response, "/dashboard");
            return;
        }
        request.setAttribute("pageTitle", "Create account");
        view(request, response, "register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = rawParam(request, "name");
        String email = rawParam(request, "email");
        String password = rawParam(request, "password");
        String confirmPassword = rawParam(request, "confirmPassword");
        try {
            authService.register(name, email, password, confirmPassword);
            SessionUtil.flashSuccess(request,
                    "Your account has been created. Please sign in to start a quiz.");
            go(request, response, "/login");
        } catch (AppException e) {
            // Keep what the user typed, but never echo the password back into the HTML.
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("errorField", e.getField());
            request.setAttribute("name", name == null ? "" : name);
            request.setAttribute("email", email == null ? "" : email);
            request.setAttribute("pageTitle", "Create account");
            view(request, response, "register");
        }
    }
}
