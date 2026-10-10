package com.quizdesk.dao;

import com.quizdesk.model.Role;
import com.quizdesk.model.User;
import com.quizdesk.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of {@link UserDAO}. Every value is bound, never concatenated. */
public class UserDAOImpl implements UserDAO {

    private static final String COLUMNS = "id, name, email, password_hash, role, created_at";

    @Override
    public User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Creating the user failed, no row inserted.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) { user.setId(keys.getLong(1)); }
            }
            return user;
        }
    }

    @Override
    public User findById(long id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<User> findAllWithAttemptCounts() throws SQLException {
        String sql = """
                SELECT u.id, u.name, u.email, u.password_hash, u.role, u.created_at,
                       COUNT(a.id) AS attempt_count
                  FROM users u
                  LEFT JOIN attempts a ON a.user_id = u.id
                 GROUP BY u.id, u.name, u.email, u.password_hash, u.role, u.created_at
                 ORDER BY u.created_at DESC
                """;
        List<User> users = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = map(rs);
                user.setAttemptCount(rs.getInt("attempt_count"));
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public int countAll() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromDatabase(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
