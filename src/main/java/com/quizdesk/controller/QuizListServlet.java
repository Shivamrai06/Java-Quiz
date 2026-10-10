package com.quizdesk.controller;

import com.quizdesk.service.QuizService;
import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /quizzes - the full list of published quizzes. */
@WebServlet(name = "QuizListServlet", urlPatterns = {"/quizzes"})
public class QuizListServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final QuizService quizService = new QuizService();
    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("quizzes", quizService.listPublishedQuizzes());
            request.setAttribute("attempted", resultService.getHistory(currentUserId(request)).size());
            request.setAttribute("pageTitle", "Quizzes");
            view(request, response, "quizzes");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
