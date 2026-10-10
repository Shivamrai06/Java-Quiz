package com.quizdesk.controller;

import com.quizdesk.service.ResultService;
import com.quizdesk.util.AppException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /results - the student's full attempt history. */
@WebServlet(name = "ResultsServlet", urlPatterns = {"/results"})
public class ResultsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ResultService resultService = new ResultService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long userId = currentUserId(request);
            request.setAttribute("attempts", resultService.getHistory(userId));
            request.setAttribute("summary", resultService.studentSummary(userId));
            request.setAttribute("pageTitle", "My results");
            view(request, response, "results");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }
}
