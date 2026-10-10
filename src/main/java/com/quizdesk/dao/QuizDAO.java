package com.quizdesk.dao;

import com.quizdesk.model.Quiz;
import java.sql.SQLException;
import java.util.List;

/** Contract for quiz persistence. */
public interface QuizDAO {
    Quiz insert(Quiz quiz) throws SQLException;
    void update(Quiz quiz) throws SQLException;
    void delete(long quizId) throws SQLException;
    Quiz findById(long quizId) throws SQLException;
    List<Quiz> findPublished() throws SQLException;
    List<Quiz> findAll() throws SQLException;
    int countAll() throws SQLException;
    int countPublished() throws SQLException;
}
