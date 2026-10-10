package com.quizdesk.controller;

import com.quizdesk.util.SessionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * POST /logout invalidates the session.
 *
 * <p>Only POST is accepted for the button: a GET logout link could be triggered by an image tag on
 * another site, signing the user out silently.</p>
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SessionUtil.logout(request);
        go(request, response, "/login?expired=1");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SessionUtil.logout(request);
        go(request, response, "/login");
    }
}
