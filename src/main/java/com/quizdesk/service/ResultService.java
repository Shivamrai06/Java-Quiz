package com.quizdesk.service;

import com.quizdesk.dao.AttemptDAO;
import com.quizdesk.dao.AttemptDAOImpl;
import com.quizdesk.dao.QuestionDAO;
import com.quizdesk.dao.QuestionDAOImpl;
import com.quizdesk.dao.QuizDAO;
import com.quizdesk.dao.QuizDAOImpl;
import com.quizdesk.model.Answer;
import com.quizdesk.model.Attempt;
import com.quizdesk.model.Question;
import com.quizdesk.model.Quiz;
import com.quizdesk.util.AppException;
import com.quizdesk.util.StatsCounter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scoring, result storage and result retrieval.
 *
 * <p>The score is always computed here, from the correct answers held in MySQL. The browser submits
 * only which option was clicked, so editing hidden fields cannot inflate a score.</p>
 */
public class ResultService {

    private static final Logger LOG = Logger.getLogger(ResultService.class.getName());

    private final AttemptDAO attemptDAO;
    private final QuizDAO quizDAO;
    private final QuestionDAO questionDAO;

    public ResultService() { this(new AttemptDAOImpl(), new QuizDAOImpl(), new QuestionDAOImpl()); }

    public ResultService(AttemptDAO attemptDAO, QuizDAO quizDAO, QuestionDAO questionDAO) {
        this.attemptDAO = attemptDAO;
        this.quizDAO = quizDAO;
        this.questionDAO = questionDAO;
    }

    /** A rendered result: the attempt header plus the per-question review. */
    public record QuizResult(Attempt attempt, Quiz quiz, List<Answer> answers) {
        public int getCorrectCount() { return (int) answers.stream().filter(Answer::isCorrect).count(); }
        public int getIncorrectCount() {
            return (int) answers.stream().filter(a -> !a.isCorrect() && !a.isUnanswered()).count();
        }
        public int getUnansweredCount() { return (int) answers.stream().filter(Answer::isUnanswered).count(); }
    }

    // ==================== submission + scoring ====================

    public Attempt submitQuiz(long userId, long quizId, Map<String, String> submittedAnswers, Long startedAtMillis) {
        if (userId <= 0) { throw new AppException("You must be signed in to submit a quiz."); }
        try {
            Quiz quiz = quizDAO.findById(quizId);
            if (quiz == null) { throw new AppException("Quiz could not be found."); }
            if (!quiz.isPublished()) { throw new AppException("This quiz is not available."); }

            // The authoritative questions and correct answers come from the database.
            Map<Long, Question> questionsById = questionDAO.findByQuizIdAsMap(quizId);
            if (questionsById.isEmpty()) { throw new AppException("This quiz has no questions yet."); }

            List<Answer> answers = new ArrayList<>();
            int correctCount = 0;
            for (Question question : questionsById.values()) {
                String raw = submittedAnswers == null ? null
                        : submittedAnswers.get(String.valueOf(question.getId()));
                Character selected = normalizeOption(raw);   // anything not A-D becomes "unanswered"
                boolean isCorrect = selected != null && selected == question.getCorrectOption();
                if (isCorrect) { correctCount++; }
                answers.add(new Answer(question.getId(), selected, isCorrect));
            }

            int totalQuestions = questionsById.size();
            Attempt attempt = new Attempt(userId, quizId);
            attempt.setScore(correctCount);
            attempt.setTotalQuestions(totalQuestions);
            attempt.setPercentage(calculatePercentage(correctCount, totalQuestions));
            attempt.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
            if (startedAtMillis != null && startedAtMillis > 0) {
                attempt.setStartedAt(new Timestamp(startedAtMillis));
            }
            Attempt saved = attemptDAO.saveAttemptWithAnswers(attempt, answers);
            StatsCounter.quizSubmitted();
            final int scoreForLog = correctCount;
            final int totalForLog = totalQuestions;
            LOG.info(() -> "Attempt " + saved.getId() + " stored for user " + userId + " quiz " + quizId
                    + " score " + scoreForLog + '/' + totalForLog);
            return saved;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not store the submission for user " + userId, e);
            throw new AppException("Your submission could not be saved. Please try again.");
        }
    }

    /** Percentage rounded to two decimals, with a guard against dividing by zero. */
    public double calculatePercentage(int correct, int total) {
        if (total <= 0) { return 0.0; }
        return Math.round(((correct * 100.0) / total) * 100.0) / 100.0;
    }

    private Character normalizeOption(String raw) {
        if (raw == null || raw.isBlank()) { return null; }
        char value = Character.toUpperCase(raw.trim().charAt(0));
        for (char key : QuizService.OPTION_KEYS) {
            if (key == value) { return value; }
        }
        return null;
    }

    // ==================== retrieval ====================

    /** Loads one result. Authorization is part of the lookup, not an afterthought. */
    public QuizResult getResult(long attemptId, long viewerId, boolean viewerIsAdmin) {
        try {
            Attempt attempt = attemptDAO.findById(attemptId);
            if (attempt == null) { throw new AppException("Result could not be found."); }
            if (!viewerIsAdmin && attempt.getUserId() != viewerId) {
                LOG.warning(() -> "User " + viewerId + " tried to open attempt " + attemptId
                        + " owned by user " + attempt.getUserId());
                throw new AppException("You are not authorized to view this result.");
            }
            return new QuizResult(attempt, quizDAO.findById(attempt.getQuizId()),
                    attemptDAO.findAnswersByAttemptId(attemptId));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load attempt " + attemptId, e);
            throw new AppException("This result could not be loaded right now. Please try again.");
        }
    }

    public List<Attempt> getHistory(long userId) {
        try {
            return attemptDAO.findByUserId(userId);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load history for user " + userId, e);
            throw new AppException("Your results could not be loaded right now. Please try again.");
        }
    }

    public List<Attempt> getAllAttempts() {
        try {
            return attemptDAO.findAllDetailed();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load all attempts", e);
            throw new AppException("Results could not be loaded right now. Please try again.");
        }
    }

    public List<Attempt> getRecentAttempts(int limit) {
        try {
            return attemptDAO.findRecent(limit);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not load recent attempts", e);
            return Collections.emptyList();
        }
    }

    public Map<String, Object> studentSummary(long userId) {
        Map<String, Object> summary = new LinkedHashMap<>();
        try {
            summary.put("attempted", attemptDAO.countByUserId(userId));
            summary.put("average", Math.round(attemptDAO.averagePercentageForUser(userId) * 10.0) / 10.0);
            summary.put("best", Math.round(attemptDAO.bestPercentageForUser(userId) * 10.0) / 10.0);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Could not build summary for user " + userId, e);
            summary.put("attempted", 0);
            summary.put("average", 0.0);
            summary.put("best", 0.0);
        }
        return summary;
    }

    public int countAllAttempts() {
        try {
            return attemptDAO.countAll();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Could not count attempts", e);
            return 0;
        }
    }
}
