package com.quizdesk.controller;

import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.SessionUtil;
import com.quizdesk.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /result?id=N - one result with the full question-by-question review.
 *
 * <p>Ownership is verified inside {@link ResultService}, so changing the id in the address bar to
 * another student's attempt returns "not authorized" rather than their answers.</p>
 */
@WebServlet(name = "ResultServlet", urlPatterns = {"/result"})
public class ResultServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long attemptId = ValidationUtil.requireId(rawParam(request, "id"), "result id");
            ResultService.QuizResult result = resultService.getResult(
                    attemptId, currentUserId(request), SessionUtil.isAdmin(request));
            request.setAttribute("result", result);
            request.setAttribute("attempt", result.attempt());
            request.setAttribute("quiz", result.quiz());
            request.setAttribute("answers", result.answers());
            request.setAttribute("pageTitle", "Result");
            view(request, response, "result");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
