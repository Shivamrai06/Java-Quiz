package com.quizdesk.controller.admin;

import com.quizdesk.controller.BaseServlet;
import com.quizdesk.service.QuizService;
import com.quizdesk.util.AppException;
import com.quizdesk.util.SessionUtil;
import com.quizdesk.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Question management for administrators.
 *
 * <ul>
 *   <li>{@code GET  /admin/questions?quizId=N} - list the questions of one quiz</li>
 *   <li>{@code POST /admin/question/create}, {@code /edit}, {@code /delete}</li>
 * </ul>
 *
 * <p>{@code ?edit=N} pre-fills the form for an existing question.</p>
 */
@WebServlet(name = "QuestionManagementServlet", urlPatterns = {
        "/admin/questions", "/admin/question/create", "/admin/question/edit", "/admin/question/delete"})
public class QuestionManagementServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final QuizService quizService = new QuizService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long quizId = ValidationUtil.requireId(rawParam(request, "quizId"), "quiz id");
            QuizService.QuizDetail detail = quizService.getQuizDetail(quizId, false);
            request.setAttribute("quiz", detail.quiz());
            request.setAttribute("questions", detail.questions());

            String editId = param(request, "edit");
            if (editId != null) {
                long questionId = ValidationUtil.requireId(editId, "question id");
                request.setAttribute("editing", detail.questions().stream()
                        .filter(q -> q.getId() == questionId)
                        .findFirst()
                        .orElseThrow(() -> new AppException("Question could not be found.")));
            }
            request.setAttribute("pageTitle", "Manage questions");
            view(request, response, "admin/questions");
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String rawQuestionId = rawParam(request, "id");
        String rawQuizId = rawParam(request, "quizId");
        String redirectTarget = "/admin/questions?quizId=" + (rawQuizId == null ? "" : rawQuizId);

        try {
            long quizId = ValidationUtil.requireId(rawQuizId, "quiz id");

            if ("/admin/question/delete".equals(request.getServletPath())) {
                quizService.deleteQuestion(ValidationUtil.requireId(rawQuestionId, "question id"));
                SessionUtil.flashSuccess(request, "The question was deleted.");
                go(request, response, redirectTarget);
                return;
            }

            String[] optionTexts = {rawParam(request, "optionA"), rawParam(request, "optionB"),
                    rawParam(request, "optionC"), rawParam(request, "optionD")};

            quizService.saveQuestion(rawQuestionId, quizId, rawParam(request, "questionText"),
                    rawParam(request, "explanation"), rawParam(request, "correctOption"), optionTexts);

            SessionUtil.flashSuccess(request, ValidationUtil.isBlank(rawQuestionId)
                    ? "The question was added." : "The question was updated.");
            go(request, response, redirectTarget);
        } catch (AppException e) {
            SessionUtil.flashError(request, e.getMessage());
            go(request, response, redirectTarget);
        }
    }
}
