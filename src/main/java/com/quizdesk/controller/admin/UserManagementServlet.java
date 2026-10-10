package com.quizdesk.controller.admin;

import com.quizdesk.controller.BaseServlet;
import com.quizdesk.dao.UserDAO;
import com.quizdesk.dao.UserDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * GET /admin/users - read-only list of registered accounts.
 *
 * <p>Deliberately read-only: an administrator has no reason to read or change another user's
 * password, and the hash is never placed into a request attribute.</p>
 */
@WebServlet(name = "UserManagementServlet", urlPatterns = {"/admin/users"})
public class UserManagementServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("users", userDAO.findAllWithAttemptCounts());
            request.setAttribute("pageTitle", "Users");
            view(request, response, "admin/users");
        } catch (SQLException e) {
            log().log(Level.SEVERE, "Could not list users", e);
            showError(request, response, "Something went wrong",
                    "The user list could not be loaded right now. Please try again.",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
