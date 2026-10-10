package com.quizdesk.service;

import com.quizdesk.dao.QuestionDAO;
import com.quizdesk.dao.QuestionDAOImpl;
import com.quizdesk.dao.QuizDAO;
import com.quizdesk.dao.QuizDAOImpl;
import com.quizdesk.model.Question;
import com.quizdesk.model.Quiz;
import com.quizdesk.model.QuizOption;
import com.quizdesk.util.AppException;
import com.quizdesk.util.DBConnection;
import com.quizdesk.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Business rules for quizzes and questions. */
public class QuizService {

    private static final Logger LOG = Logger.getLogger(QuizService.class.getName());

    /** The only option labels the schema accepts. */
    public static final char[] OPTION_KEYS = {'A', 'B', 'C', 'D'};

    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;

    public QuizService() { this(new QuizDAOImpl(), new QuestionDAOImpl()); }

    public QuizService(QuizDAO quizDAO, QuestionDAO questionDAO) {
        this.quizDAO = quizDAO;
        this.questionDAO = questionDAO;
    }

    /** A quiz together with its questions - one round trip for the servlet. */
    public record QuizDetail(Quiz quiz, List<Question> questions) { }

    // ==================== read ====================

    public List<Quiz> listPublishedQuizzes() {
        try {
            return quizDAO.findPublished();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load published quizzes", e);
            throw new AppException("Quizzes could not be loaded right now. Please try again.");
        }
    }

    public List<Quiz> listAllQuizzes() {
        try {
            return quizDAO.findAll();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load quizzes", e);
            throw new AppException("Quizzes could not be loaded right now. Please try again.");
        }
    }

    public QuizDetail getQuizDetail(long quizId, boolean mustBePublished) {
        try {
            Quiz quiz = quizDAO.findById(quizId);
            if (quiz == null) { throw new AppException("Quiz could not be found."); }
            if (mustBePublished && !quiz.isPublished()) { throw new AppException("Quiz could not be found."); }
            List<Question> questions = questionDAO.findByQuizId(quizId);
            quiz.setQuestionCount(questions.size());
            return new QuizDetail(quiz, questions);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load quiz " + quizId, e);
            throw new AppException("This quiz could not be loaded right now. Please try again.");
        }
    }

    public Quiz requireQuiz(long quizId) {
        try {
            Quiz quiz = quizDAO.findById(quizId);
            if (quiz == null) { throw new AppException("Quiz could not be found."); }
            return quiz;
        } catch (SQLException e) {
            throw new AppException("Quiz could not be found.");
        }
    }

    // ==================== quiz writes ====================

    public Quiz saveQuiz(String rawId, String title, String description, String category,
                         String durationMin, String status) {

        String cleanTitle = ValidationUtil.requireText(title, "title", "Quiz title", 150);
        String cleanCategory = ValidationUtil.requireText(category, "category", "Category", 100);
        String cleanDescription = ValidationUtil.trimToNull(description);
        Integer cleanDuration = ValidationUtil.parseOptionalInt(durationMin, "durationMin",
                "Duration", 1, 600);

        Quiz.Status requestedStatus = "PUBLISHED".equalsIgnoreCase(status)
                ? Quiz.Status.PUBLISHED : Quiz.Status.DRAFT;

        try {
            if (ValidationUtil.isBlank(rawId)) {
                Quiz quiz = new Quiz(cleanTitle, cleanDescription, cleanCategory, cleanDuration,
                        Quiz.Status.DRAFT);
                return quizDAO.insert(quiz);
            }

            long quizId = ValidationUtil.requireId(rawId, "quiz");
            Quiz existing = quizDAO.findById(quizId);
            if (existing == null) { throw new AppException("Quiz could not be found."); }

            if (requestedStatus == Quiz.Status.PUBLISHED && questionDAO.countByQuizId(quizId) == 0) {
                throw new AppException("A quiz needs at least one question before it can be published.");
            }

            existing.setTitle(cleanTitle);
            existing.setDescription(cleanDescription);
            existing.setCategory(cleanCategory);
            existing.setDurationMin(cleanDuration);
            existing.setStatus(requestedStatus);
            quizDAO.update(existing);
            return existing;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not save the quiz", e);
            throw new AppException("Unable to save the quiz. Please try again.");
        }
    }

    public void deleteQuiz(long quizId) {
        try {
            if (quizDAO.findById(quizId) == null) { throw new AppException("Quiz could not be found."); }
            quizDAO.delete(quizId);   // ON DELETE CASCADE clears questions, options and attempts
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not delete quiz " + quizId, e);
            throw new AppException("Unable to delete the quiz. Please try again.");
        }
    }

    // ==================== question writes ====================

    public Question saveQuestion(String rawQuestionId, long quizId, String questionText,
                                 String explanation, String correctOption, String[] optionTexts) {

        String cleanText = ValidationUtil.requireText(questionText, "questionText", "Question text", 1000);
        char correct = parseCorrectOption(correctOption);

        List<QuizOption> options = new ArrayList<>();
        for (int i = 0; i < OPTION_KEYS.length; i++) {
            char key = OPTION_KEYS[i];
            String raw = (optionTexts != null && i < optionTexts.length) ? optionTexts[i] : null;
            options.add(new QuizOption(key, ValidationUtil.requireText(raw, "option" + key,
                    "Option " + key, 500)));
        }
        String cleanExplanation = ValidationUtil.trimToNull(explanation);

        try {
            if (ValidationUtil.isBlank(rawQuestionId)) {
                if (quizDAO.findById(quizId) == null) { throw new AppException("Quiz could not be found."); }
                Question question = new Question(quizId, cleanText, correct, cleanExplanation);
                options.forEach(question::addOption);
                return questionDAO.insert(question);
            }

            long questionId = ValidationUtil.requireId(rawQuestionId, "question");
            Question existing = questionDAO.findById(questionId);
            if (existing == null) { throw new AppException("Question could not be found."); }
            existing.setQuestionText(cleanText);
            existing.setCorrectOption(correct);
            existing.setExplanation(cleanExplanation);
            existing.getOptions().clear();
            options.forEach(existing::addOption);
            questionDAO.update(existing);
            return existing;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not save the question", e);
            throw new AppException("Unable to save the question. Please try again.");
        }
    }

    /**
     * Deletes a question in one transaction, pulling the quiz back to DRAFT when that question was
     * the last one - a published quiz must never be left with nothing to attempt.
     */
    public void deleteQuestion(long questionId) {
        String unpublishSql = """
                UPDATE quizzes SET status = 'DRAFT'
                 WHERE id = (SELECT quiz_id FROM questions WHERE id = ?)
                   AND (SELECT COUNT(*) FROM questions WHERE quiz_id =
                        (SELECT quiz_id FROM questions WHERE id = ?)) <= 1
                """;
        String deleteSql = "DELETE FROM questions WHERE id = ?";
        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try (var unpublish = con.prepareStatement(unpublishSql);
                 var remove = con.prepareStatement(deleteSql)) {
                unpublish.setLong(1, questionId);
                unpublish.setLong(2, questionId);
                unpublish.executeUpdate();
                remove.setLong(1, questionId);
                remove.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not delete question " + questionId, e);
            throw new AppException("Unable to delete the question. Please try again.");
        }
    }

    // ==================== helpers ====================

    public Map<String, Integer> adminCounters() {
        try {
            return Map.of(
                    "quizzes", quizDAO.countAll(),
                    "published", quizDAO.countPublished(),
                    "questions", questionDAO.countAll());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load counters", e);
            throw new AppException("Dashboard statistics could not be loaded.");
        }
    }

    private char parseCorrectOption(String correctOption) {
        if (ValidationUtil.isBlank(correctOption)) {
            throw new AppException("correctOption", "Please choose the correct option (A, B, C or D).");
        }
        char value = Character.toUpperCase(correctOption.trim().charAt(0));
        for (char key : OPTION_KEYS) {
            if (key == value) { return value; }
        }
        throw new AppException("correctOption", "The correct option must be A, B, C or D.");
    }
}
