package com.quizdesk.controller;

import com.quizdesk.model.Quiz;
import com.quizdesk.service.QuizService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * GET /quiz?id=N - loads a published quiz.
 *
 * <p>The default view is the instructions page; {@code mode=start} opens the questions and records
 * the attempt start time in the session.</p>
 */
@WebServlet(name = "QuizServlet", urlPatterns = {"/quiz"})
public class QuizServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final QuizService quizService = new QuizService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long quizId = ValidationUtil.requireId(rawParam(request, "id"), "quiz id");
            QuizService.QuizDetail detail = quizService.getQuizDetail(quizId, true);
            Quiz quiz = detail.quiz();
            request.setAttribute("quiz", quiz);

            if ("start".equalsIgnoreCase(param(request, "mode"))) {
                if (detail.questions().isEmpty()) {
                    throw new AppException("This quiz does not have any questions yet.");
                }
                HttpSession session = request.getSession(true);
                session.setAttribute("attemptStartedAt_" + quizId, System.currentTimeMillis());
                request.setAttribute("questions", detail.questions());
                request.setAttribute("pageTitle", quiz.getTitle());
                view(request, response, "quiz");
            } else {
                request.setAttribute("pageTitle", "Quiz overview");
                view(request, response, "quiz-details");
            }
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
