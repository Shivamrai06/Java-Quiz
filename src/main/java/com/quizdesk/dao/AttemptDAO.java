package com.quizdesk.dao;

import com.quizdesk.model.Answer;
import com.quizdesk.model.Attempt;
import java.sql.SQLException;
import java.util.List;

/** Contract for attempt/answer persistence. */
public interface AttemptDAO {
    Attempt saveAttemptWithAnswers(Attempt attempt, List<Answer> answers) throws SQLException;
    Attempt findById(long attemptId) throws SQLException;
    List<Attempt> findByUserId(long userId) throws SQLException;
    List<Attempt> findAllDetailed() throws SQLException;
    List<Answer> findAnswersByAttemptId(long attemptId) throws SQLException;
    int countAll() throws SQLException;
    int countByUserId(long userId) throws SQLException;
    double bestPercentageForUser(long userId) throws SQLException;
    double averagePercentageForUser(long userId) throws SQLException;
    List<Attempt> findRecent(int limit) throws SQLException;
}
