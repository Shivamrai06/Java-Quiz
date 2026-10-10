package com.quizdesk.controller.admin;

import com.quizdesk.controller.BaseServlet;
import com.quizdesk.model.Quiz;
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
 * Quiz management for administrators.
 *
 * <ul>
 *   <li>{@code GET  /admin/quizzes}     - table of every quiz</li>
 *   <li>{@code GET  /admin/quiz/create} - empty form</li>
 *   <li>{@code GET  /admin/quiz/edit}   - form loaded with an existing quiz</li>
 *   <li>{@code POST /admin/quiz/create}, {@code /edit}, {@code /delete}</li>
 * </ul>
 *
 * <p>Every write ends in a redirect, so a refresh never resubmits the form.</p>
 */
@WebServlet(name = "QuizManagementServlet", urlPatterns = {
        "/admin/quizzes", "/admin/quiz/create", "/admin/quiz/edit", "/admin/quiz/delete"})
public class QuizManagementServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final QuizService quizService = new QuizService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            switch (request.getServletPath()) {
                case "/admin/quiz/create" -> {
                    request.setAttribute("mode", "create");
                    request.setAttribute("pageTitle", "Create quiz");
                    view(request, response, "admin/quiz-form");
                }
                case "/admin/quiz/edit" -> {
                    long quizId = ValidationUtil.requireId(rawParam(request, "id"), "quiz id");
                    request.setAttribute("mode", "edit");
                    request.setAttribute("quiz", quizService.requireQuiz(quizId));
                    request.setAttribute("pageTitle", "Edit quiz");
                    view(request, response, "admin/quiz-form");
                }
                default -> {
                    request.setAttribute("quizzes", quizService.listAllQuizzes());
                    request.setAttribute("pageTitle", "Manage quizzes");
                    view(request, response, "admin/quizzes");
                }
            }
        } catch (AppException e) {
            handleAppFailure(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("/admin/quiz/delete".equals(request.getServletPath())) {
            try {
                quizService.deleteQuiz(ValidationUtil.requireId(rawParam(request, "id"), "quiz id"));
                SessionUtil.flashSuccess(request, "The quiz was deleted.");
            } catch (AppException e) {
                SessionUtil.flashError(request, e.getMessage());
            }
            go(request, response, "/admin/quizzes");
            return;
        }

        String rawId = rawParam(request, "id");
        try {
            Quiz saved = quizService.saveQuiz(rawId, rawParam(request, "title"),
                    rawParam(request, "description"), rawParam(request, "category"),
                    rawParam(request, "durationMin"), rawParam(request, "status"));

            if (ValidationUtil.isBlank(rawId)) {
                SessionUtil.flashSuccess(request, "The quiz was created. Add its questions next.");
                go(request, response, "/admin/questions?quizId=" + saved.getId());
            } else {
                SessionUtil.flashSuccess(request, "The quiz was updated.");
                go(request, response, "/admin/quizzes");
            }
        } catch (AppException e) {
            // Re-render the form. The JSP re-populates the fields from ${param.x}, so nothing is lost.
            request.setAttribute("mode", ValidationUtil.isBlank(rawId) ? "create" : "edit");
            request.setAttribute("editId", rawId);
            handleFormError(request, response, e);
        }
    }

    /** Shows the validation message on the form instead of a generic error page. */
    private void handleFormError(HttpServletRequest request, HttpServletResponse response, AppException e)
            throws ServletException, IOException {
        request.setAttribute("formError", e.getMessage());
        request.setAttribute("formErrorField", e.getField());
        request.setAttribute("pageTitle", "Quiz form");
        view(request, response, "admin/quiz-form");
    }
}
