package com.quizdesk.controller.admin;

import com.quizdesk.controller.BaseServlet;
import com.quizdesk.dao.UserDAO;
import com.quizdesk.dao.UserDAOImpl;
import com.quizdesk.service.QuizService;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.DBConnection;
import com.quizdesk.util.StatsCounter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;

/** GET /admin/dashboard - counters, recent activity and runtime diagnostics. */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final QuizService quizService = new QuizService();
    private final ResultService resultService = new ResultService();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("counters", quizService.adminCounters());
            request.setAttribute("recentAttempts", resultService.getRecentAttempts(5));
            request.setAttribute("poolSummary", DBConnection.poolSummary());
            request.setAttribute("requestsServed", StatsCounter.totalRequests());
            request.setAttribute("successfulLogins", StatsCounter.successfulLogins());
            request.setAttribute("quizSubmissions", StatsCounter.quizSubmissions());
            request.setAttribute("uptimeMinutes", StatsCounter.uptimeMinutes());
            try {
                request.setAttribute("userCount", userDAO.countAll());
            } catch (java.sql.SQLException e) {
                log().log(Level.WARNING, "Could not count users", e);
                request.setAttribute("userCount", 0);
            }
            request.setAttribute("pageTitle", "Admin dashboard");
            view(request, response, "admin/dashboard");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
