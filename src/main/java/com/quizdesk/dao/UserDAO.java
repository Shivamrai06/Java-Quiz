package com.quizdesk.dao;

import com.quizdesk.model.User;
import java.sql.SQLException;
import java.util.List;

/** Contract for user persistence. */
public interface UserDAO {
    User insert(User user) throws SQLException;
    User findById(long id) throws SQLException;
    User findByEmail(String email) throws SQLException;
    boolean emailExists(String email) throws SQLException;
    List<User> findAllWithAttemptCounts() throws SQLException;
    int countAll() throws SQLException;
}
