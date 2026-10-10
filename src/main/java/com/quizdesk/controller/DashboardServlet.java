package com.quizdesk.controller;

import com.quizdesk.model.Attempt;
import com.quizdesk.model.Quiz;
import com.quizdesk.service.QuizService;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.DBConnection;
import com.quizdesk.util.SessionUtil;
import com.quizdesk.util.StatsCounter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/** GET /dashboard - summary cards, available quizzes and recent results. */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final int RECENT_LIMIT = 5;

    private final QuizService quizService = new QuizService();
    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessionUtil.isAdmin(request)) {
            go(request, response, "/admin/dashboard");
            return;
        }
        try {
            long userId = currentUserId(request);
            List<Quiz> quizzes = quizService.listPublishedQuizzes();
            Map<String, Object> summary = resultService.studentSummary(userId);
            List<Attempt> history = resultService.getHistory(userId);

            int attemptable = 0;
            for (Quiz quiz : quizzes) {
                if (quiz.isAttemptable()) { attemptable++; }
            }

            request.setAttribute("quizzes", quizzes);
            request.setAttribute("attemptableCount", attemptable);
            request.setAttribute("summary", summary);
            request.setAttribute("recentAttempts",
                    history.size() > RECENT_LIMIT ? history.subList(0, RECENT_LIMIT) : history);
            request.setAttribute("greeting", greeting());
            request.setAttribute("connectionPool", DBConnection.poolSummary());
            request.setAttribute("requestsServed", StatsCounter.totalRequests());
            request.setAttribute("pageTitle", "Dashboard");
            view(request, response, "dashboard");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }

    /** Server-side greeting, so the page never says "Good morning" at 9pm. */
    private String greeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) { return "Good morning"; }
        if (hour < 17) { return "Good afternoon"; }
        return "Good evening";
    }
}
