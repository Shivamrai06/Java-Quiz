package com.quizdesk.dao;

import com.quizdesk.model.Answer;
import com.quizdesk.model.Attempt;
import com.quizdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of {@link AttemptDAO}. Writes are transactional. */
public class AttemptDAOImpl implements AttemptDAO {

    @Override
    public Attempt saveAttemptWithAnswers(Attempt attempt, List<Answer> answers) throws SQLException {
        String attemptSql = """
                INSERT INTO attempts
                    (user_id, quiz_id, score, total_questions, percentage, started_at, submitted_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        String answerSql = """
                INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            // ============ BEGIN TRANSACTION ============
            // The attempt header and every answer row must land together; a half-written attempt
            // would show a score with no review detail.
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(attemptSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, attempt.getUserId());
                    ps.setLong(2, attempt.getQuizId());
                    ps.setInt(3, attempt.getScore());
                    ps.setInt(4, attempt.getTotalQuestions());
                    ps.setDouble(5, attempt.getPercentage());
                    if (attempt.getStartedAt() == null) {
                        ps.setNull(6, Types.TIMESTAMP);
                    } else {
                        ps.setTimestamp(6, attempt.getStartedAt());
                    }
                    ps.setTimestamp(7, attempt.getSubmittedAt());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Saving the attempt failed, no row inserted.");
                    }
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            attempt.setId(keys.getLong(1));
                        } else {
                            throw new SQLException("Saving the attempt failed: no generated id.");
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(answerSql)) {
                    for (Answer answer : answers) {
                        ps.setLong(1, attempt.getId());
                        ps.setLong(2, answer.getQuestionId());
                        if (answer.getSelectedOption() == null) {
                            ps.setNull(3, Types.CHAR);      // question left unanswered
                        } else {
                            ps.setString(3, String.valueOf(answer.getSelectedOption()));
                        }
                        ps.setBoolean(4, answer.isCorrect());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();          // ============ COMMIT ============
            } catch (SQLException e) {
                con.rollback();        // ============ ROLLBACK ============
                throw e;
            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        }
        return attempt;
    }

    @Override
    public Attempt findById(long attemptId) throws SQLException {
        String sql = """
                SELECT a.id, a.user_id, a.quiz_id, a.score, a.total_questions, a.percentage,
                       a.started_at, a.submitted_at,
                       u.name AS student_name, u.email AS student_email, q.title AS quiz_title
                  FROM attempts a
                  JOIN users u ON u.id = a.user_id
                  JOIN quizzes q ON q.id = a.quiz_id
                 WHERE a.id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Attempt> findByUserId(long userId) throws SQLException {
        return query(String.format("%s WHERE a.user_id = ? ORDER BY a.submitted_at DESC, a.id DESC", SELECT_BODY), userId);
    }

    @Override
    public List<Attempt> findAllDetailed() throws SQLException {
        return query(SELECT_BODY + " ORDER BY a.submitted_at DESC, a.id DESC", null);
    }

    @Override
    public List<Attempt> findRecent(int limit) throws SQLException {
        String sql = SELECT_BODY + " ORDER BY a.submitted_at DESC, a.id DESC LIMIT ?";
        List<Attempt> attempts = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { attempts.add(map(rs)); }
            }
        }
        return attempts;
    }

    @Override
    public List<Answer> findAnswersByAttemptId(long attemptId) throws SQLException {
        String sql = """
                SELECT ans.id, ans.attempt_id, ans.question_id, ans.selected_option, ans.is_correct,
                       q.question_text, q.correct_option, q.explanation,
                       qo.option_text AS selected_text,
                       (SELECT option_text FROM question_options x
                         WHERE x.question_id = q.id AND x.option_key = q.correct_option) AS correct_text
                  FROM answers ans
                  JOIN questions q ON q.id = ans.question_id
                  LEFT JOIN question_options qo
                         ON qo.question_id = ans.question_id AND qo.option_key = ans.selected_option
                 WHERE ans.attempt_id = ?
                 ORDER BY ans.id ASC
                """;
        List<Answer> answers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Answer answer = new Answer();
                    answer.setId(rs.getLong("id"));
                    answer.setAttemptId(rs.getLong("attempt_id"));
                    answer.setQuestionId(rs.getLong("question_id"));
                    String selected = rs.getString("selected_option");
                    answer.setSelectedOption(selected == null || selected.isEmpty() ? null : selected.charAt(0));
                    answer.setCorrect(rs.getBoolean("is_correct"));
                    answer.setQuestionText(rs.getString("question_text"));
                    String correct = rs.getString("correct_option");
                    answer.setCorrectOption(correct == null || correct.isEmpty() ? 'A' : correct.charAt(0));
                    answer.setExplanation(rs.getString("explanation"));
                    answer.setSelectedText(rs.getString("selected_text"));
                    answer.setCorrectText(rs.getString("correct_text"));
                    answers.add(answer);
                }
            }
        }
        return answers;
    }

    @Override
    public int countAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM attempts");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public int countByUserId(long userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM attempts WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public double bestPercentageForUser(long userId) throws SQLException {
        return aggregate("SELECT COALESCE(MAX(percentage), 0) FROM attempts WHERE user_id = ?", userId);
    }

    @Override
    public double averagePercentageForUser(long userId) throws SQLException {
        return aggregate("SELECT COALESCE(AVG(percentage), 0) FROM attempts WHERE user_id = ?", userId);
    }

    private static final String SELECT_BODY = """
            SELECT a.id, a.user_id, a.quiz_id, a.score, a.total_questions, a.percentage,
                   a.started_at, a.submitted_at,
                   u.name AS student_name, u.email AS student_email, q.title AS quiz_title
              FROM attempts a
              JOIN users u ON u.id = a.user_id
              JOIN quizzes q ON q.id = a.quiz_id
            """;

    private List<Attempt> query(String sql, Long parameter) throws SQLException {
        List<Attempt> attempts = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parameter != null) { ps.setLong(1, parameter); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { attempts.add(map(rs)); }
            }
        }
        return attempts;
    }

    private double aggregate(String sql, long userId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    private Attempt map(ResultSet rs) throws SQLException {
        Attempt attempt = new Attempt();
        attempt.setId(rs.getLong("id"));
        attempt.setUserId(rs.getLong("user_id"));
        attempt.setQuizId(rs.getLong("quiz_id"));
        attempt.setScore(rs.getInt("score"));
        attempt.setTotalQuestions(rs.getInt("total_questions"));
        attempt.setPercentage(rs.getDouble("percentage"));
        attempt.setStartedAt(rs.getTimestamp("started_at"));
        attempt.setSubmittedAt(rs.getTimestamp("submitted_at"));
        attempt.setStudentName(rs.getString("student_name"));
        attempt.setStudentEmail(rs.getString("student_email"));
        attempt.setQuizTitle(rs.getString("quiz_title"));
        return attempt;
    }
}
