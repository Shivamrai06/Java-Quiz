package com.quizdesk.controller;

import com.quizdesk.dao.UserDAO;
import com.quizdesk.dao.UserDAOImpl;
import com.quizdesk.model.User;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

/** GET /profile - account details and performance summary, read-only. */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UserDAO userDAO = new UserDAOImpl();
    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = userDAO.findById(currentUserId(request));
            if (user == null) {
                SessionUtil.logout(request);      // account removed while the session was alive
                go(request, response, "/login?expired=1");
                return;
            }
            request.setAttribute("user", user);
            request.setAttribute("summary", resultService.studentSummary(user.getId()));
            request.setAttribute("attempts", resultService.getHistory(user.getId()));
            request.setAttribute("pageTitle", "Profile");
            view(request, response, "profile");
        } catch (SQLException e) {
            log().log(Level.SEVERE, "Could not load the profile", e);
            showError(request, response, "Something went wrong",
                    "Your profile could not be loaded right now. Please try again.",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
