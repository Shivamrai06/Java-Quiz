package com.quizdesk.dao;

import com.quizdesk.model.Quiz;
import com.quizdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of {@link QuizDAO}. Sub-queries supply the derived counters. */
public class QuizDAOImpl implements QuizDAO {

    private static final String BASE_SELECT = """
            SELECT q.id, q.title, q.description, q.category, q.duration_min, q.status,
                   q.created_at, q.updated_at,
                   (SELECT COUNT(*) FROM questions qs WHERE qs.quiz_id = q.id) AS question_count,
                   (SELECT COUNT(*) FROM attempts  at WHERE at.quiz_id = q.id) AS attempt_count
              FROM quizzes q
            """;

    @Override
    public Quiz insert(Quiz quiz) throws SQLException {
        String sql = """
                INSERT INTO quizzes (title, description, category, duration_min, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setString(3, quiz.getCategory());
            setNullableInt(ps, 4, quiz.getDurationMin());
            ps.setString(5, quiz.getStatus().name());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Creating the quiz failed, no row inserted.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) { quiz.setId(keys.getLong(1)); }
            }
            return quiz;
        }
    }

    @Override
    public void update(Quiz quiz) throws SQLException {
        String sql = """
                UPDATE quizzes
                   SET title = ?, description = ?, category = ?, duration_min = ?, status = ?
                 WHERE id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            ps.setString(3, quiz.getCategory());
            setNullableInt(ps, 4, quiz.getDurationMin());
            ps.setString(5, quiz.getStatus().name());
            ps.setLong(6, quiz.getId());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Updating the quiz failed: no quiz with id " + quiz.getId() + ".");
            }
        }
    }

    @Override
    public void delete(long quizId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM quizzes WHERE id = ?")) {
            ps.setLong(1, quizId);
            ps.executeUpdate();
        }
    }

    @Override
    public Quiz findById(long quizId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(BASE_SELECT + " WHERE q.id = ?")) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Quiz> findPublished() throws SQLException {
        return queryList(BASE_SELECT + " WHERE q.status = 'PUBLISHED' ORDER BY q.updated_at DESC");
    }

    @Override
    public List<Quiz> findAll() throws SQLException {
        return queryList(BASE_SELECT + " ORDER BY q.status = 'PUBLISHED' DESC, q.updated_at DESC");
    }

    @Override
    public int countAll() throws SQLException {
        return scalar("SELECT COUNT(*) FROM quizzes");
    }

    @Override
    public int countPublished() throws SQLException {
        return scalar("SELECT COUNT(*) FROM quizzes WHERE status = 'PUBLISHED'");
    }

    private List<Quiz> queryList(String sql) throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { quizzes.add(map(rs)); }
        }
        return quizzes;
    }

    private int scalar(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) { ps.setNull(index, Types.INTEGER); } else { ps.setInt(index, value); }
    }

    private Quiz map(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(rs.getLong("id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setCategory(rs.getString("category"));
        int duration = rs.getInt("duration_min");
        quiz.setDurationMin(rs.wasNull() ? null : duration);
        quiz.setStatus(Quiz.Status.fromDatabase(rs.getString("status")));
        quiz.setCreatedAt(rs.getTimestamp("created_at"));
        quiz.setUpdatedAt(rs.getTimestamp("updated_at"));
        quiz.setQuestionCount(rs.getInt("question_count"));
        quiz.setAttemptCount(rs.getInt("attempt_count"));
        return quiz;
    }
}
