package com.quizdesk.dao;

import com.quizdesk.model.Question;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Contract for question and option persistence. */
public interface QuestionDAO {
    Question insert(Question question, Connection con) throws SQLException;
    Question insert(Question question) throws SQLException;
    void update(Question question) throws SQLException;
    void delete(long questionId) throws SQLException;
    Question findById(long questionId) throws SQLException;
    List<Question> findByQuizId(long quizId) throws SQLException;
    Map<Long, Question> findByQuizIdAsMap(long quizId) throws SQLException;
    int countByQuizId(long quizId) throws SQLException;
    int countAll() throws SQLException;
}
