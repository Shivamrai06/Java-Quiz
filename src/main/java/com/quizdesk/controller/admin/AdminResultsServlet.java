package com.quizdesk.controller.admin;

import com.quizdesk.controller.BaseServlet;
import com.quizdesk.model.Attempt;
import com.quizdesk.service.QuizService;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** GET /admin/results - every attempt, with optional quizId and email filters. */
@WebServlet(name = "AdminResultsServlet", urlPatterns = {"/admin/results"})
public class AdminResultsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ResultService resultService = new ResultService();
    private final QuizService quizService = new QuizService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Attempt> attempts = resultService.getAllAttempts();
            String quizIdFilter = param(request, "quizId");
            String emailFilter = param(request, "email");

            // Filtering in Java keeps the first version simple while still showing stream usage.
            List<Attempt> filtered = new ArrayList<>();
            for (Attempt attempt : attempts) {
                boolean matchesQuiz = quizIdFilter == null
                        || String.valueOf(attempt.getQuizId()).equals(quizIdFilter);
                boolean matchesEmail = emailFilter == null
                        || (attempt.getStudentEmail() != null
                            && attempt.getStudentEmail().toLowerCase().contains(emailFilter.toLowerCase()));
                if (matchesQuiz && matchesEmail) { filtered.add(attempt); }
            }

            request.setAttribute("attempts", filtered);
            request.setAttribute("totalCount", attempts.size());
            request.setAttribute("quizzes", quizService.listAllQuizzes());
            request.setAttribute("quizIdFilter", quizIdFilter);
            request.setAttribute("emailFilter", emailFilter);
            request.setAttribute("pageTitle", "Results");
            view(request, response, "admin/results");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
