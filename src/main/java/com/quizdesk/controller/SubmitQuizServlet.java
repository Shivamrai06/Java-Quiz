package com.quizdesk.controller;

import com.quizdesk.model.Attempt;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.ValidationUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * POST /quiz/submit - scores the submission and stores it.
 *
 * <p>The browser posts one field per question named {@code q_<questionId>}. No score is accepted
 * from the client; {@link ResultService} recalculates everything from the answers in MySQL. The
 * redirect afterwards means a refresh cannot create a second attempt.</p>
 */
@WebServlet(name = "SubmitQuizServlet", urlPatterns = {"/quiz/submit"})
public class SubmitQuizServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final String QUESTION_PREFIX = "q_";

    private final ResultService resultService = new ResultService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            long quizId = ValidationUtil.requireId(rawParam(request, "quizId"), "quiz id");
            long userId = currentUserId(request);

            Map<String, String> submitted = new LinkedHashMap<>();
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                if (name.startsWith(QUESTION_PREFIX)) {
                    submitted.put(name.substring(QUESTION_PREFIX.length()), request.getParameter(name));
                }
            }

            HttpSession session = request.getSession(false);
            Long startedAt = null;
            if (session != null) {
                Object value = session.getAttribute("attemptStartedAt_" + quizId);
                if (value instanceof Long millis) {
                    startedAt = millis;
                    session.removeAttribute("attemptStartedAt_" + quizId);
                }
            }

            Attempt attempt = resultService.submitQuiz(userId, quizId, submitted, startedAt);
            go(request, response, "/result?id=" + attempt.getId());
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
