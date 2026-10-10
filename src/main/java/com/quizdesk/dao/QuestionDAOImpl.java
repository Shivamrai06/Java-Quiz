package com.quizdesk.dao;

import com.quizdesk.model.Question;
import com.quizdesk.model.QuizOption;
import com.quizdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of {@link QuestionDAO}.
 *
 * <p>Reads use two small queries (questions, then options) stitched together in Java, so the
 * question text is not repeated once per option as it would be in a wide join.</p>
 */
public class QuestionDAOImpl implements QuestionDAO {

    @Override
    public Question insert(Question question) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            return insert(question, con);
        }
    }

    @Override
    public Question insert(Question question, Connection con) throws SQLException {
        String questionSql = """
                INSERT INTO questions (quiz_id, question_text, correct_option, explanation)
                VALUES (?, ?, ?, ?)
                """;
        String optionSql = """
                INSERT INTO question_options (question_id, option_key, option_text)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, String.valueOf(question.getCorrectOption()));
            if (question.getExplanation() == null) {
                ps.setNull(4, Types.LONGVARCHAR);
            } else {
                ps.setString(4, question.getExplanation());
            }
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Creating the question failed, no row inserted.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) { question.setId(keys.getLong(1)); }
            }
        }
        try (PreparedStatement ps = con.prepareStatement(optionSql)) {
            for (QuizOption option : question.getOptions()) {
                ps.setLong(1, question.getId());
                ps.setString(2, String.valueOf(option.getOptionKey()));
                ps.setString(3, option.getOptionText());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return question;
    }

    @Override
    public void update(Question question) throws SQLException {
        String updateSql = """
                UPDATE questions
                   SET question_text = ?, correct_option = ?, explanation = ?
                 WHERE id = ?
                """;
        String deleteOptionsSql = "DELETE FROM question_options WHERE question_id = ?";
        String insertOptionSql = """
                INSERT INTO question_options (question_id, option_key, option_text)
                VALUES (?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection()) {
            boolean originalAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);              // ---- BEGIN TRANSACTION ----
            try {
                try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                    ps.setString(1, question.getQuestionText());
                    ps.setString(2, String.valueOf(question.getCorrectOption()));
                    if (question.getExplanation() == null) {
                        ps.setNull(3, Types.LONGVARCHAR);
                    } else {
                        ps.setString(3, question.getExplanation());
                    }
                    ps.setLong(4, question.getId());
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Question " + question.getId() + " no longer exists.");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(deleteOptionsSql)) {
                    ps.setLong(1, question.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(insertOptionSql)) {
                    for (QuizOption option : question.getOptions()) {
                        ps.setLong(1, question.getId());
                        ps.setString(2, String.valueOf(option.getOptionKey()));
                        ps.setString(3, option.getOptionText());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();                      // ---- COMMIT ----
            } catch (SQLException e) {
                con.rollback();                    // ---- ROLLBACK ----
                throw e;
            } finally {
                con.setAutoCommit(originalAutoCommit);
            }
        }
    }

    @Override
    public void delete(long questionId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }
    }

    @Override
    public Question findById(long questionId) throws SQLException {
        String sql = """
                SELECT id, quiz_id, question_text, correct_option, explanation, created_at
                  FROM questions WHERE id = ?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { return null; }
                Question question = mapQuestion(rs);
                loadOptions(con, List.of(question));
                return question;
            }
        }
    }

    @Override
    public List<Question> findByQuizId(long quizId) throws SQLException {
        String sql = """
                SELECT id, quiz_id, question_text, correct_option, explanation, created_at
                  FROM questions
                 WHERE quiz_id = ?
                 ORDER BY id ASC
                """;
        List<Question> questions = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { questions.add(mapQuestion(rs)); }
            }
            loadOptions(con, questions);
        }
        return questions;
    }

    @Override
    public Map<Long, Question> findByQuizIdAsMap(long quizId) throws SQLException {
        Map<Long, Question> byId = new LinkedHashMap<>();
        for (Question question : findByQuizId(quizId)) {
            byId.put(question.getId(), question);
        }
        return byId;
    }

    @Override
    public int countByQuizId(long quizId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM questions WHERE quiz_id = ?")) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int countAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM questions");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** One extra query loads every option for the given questions - avoids the N+1 problem. */
    private void loadOptions(Connection con, List<Question> questions) throws SQLException {
        if (questions.isEmpty()) { return; }
        Map<Long, Question> byId = new LinkedHashMap<>();
        StringBuilder placeholders = new StringBuilder();
        for (Question question : questions) {
            byId.put(question.getId(), question);
            if (placeholders.length() > 0) { placeholders.append(", "); }
            placeholders.append('?');
        }
        String sql = "SELECT id, question_id, option_key, option_text FROM question_options "
                   + "WHERE question_id IN (" + placeholders + ") ORDER BY question_id ASC, option_key ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            for (Question question : questions) { ps.setLong(index++, question.getId()); }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question owner = byId.get(rs.getLong("question_id"));
                    if (owner == null) { continue; }
                    QuizOption option = new QuizOption();
                    option.setId(rs.getLong("id"));
                    option.setQuestionId(rs.getLong("question_id"));
                    String key = rs.getString("option_key");
                    option.setOptionKey(key == null || key.isEmpty() ? 'A' : key.charAt(0));
                    option.setOptionText(rs.getString("option_text"));
                    owner.addOption(option);
                }
            }
        }
    }

    private Question mapQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setQuizId(rs.getLong("quiz_id"));
        question.setQuestionText(rs.getString("question_text"));
        String correct = rs.getString("correct_option");
        question.setCorrectOption(correct == null || correct.isEmpty() ? 'A' : correct.charAt(0));
        question.setExplanation(rs.getString("explanation"));
        question.setCreatedAt(rs.getTimestamp("created_at"));
        return question;
    }
}
