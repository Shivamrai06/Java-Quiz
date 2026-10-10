package com.quizdesk.service;

import com.quizdesk.dao.UserDAO;
import com.quizdesk.dao.UserDAOImpl;
import com.quizdesk.model.Role;
import com.quizdesk.model.User;
import com.quizdesk.util.AppException;
import com.quizdesk.util.PasswordUtil;
import com.quizdesk.util.StatsCounter;
import com.quizdesk.util.ValidationUtil;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Business rules for registration and login. */
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;

    public AuthService() { this(new UserDAOImpl()); }

    public AuthService(UserDAO userDAO) { this.userDAO = userDAO; }

    public User register(String name, String email, String password, String confirmPassword) {
        String cleanName = ValidationUtil.requireText(name, "name", "Full name", 100);

        if (!ValidationUtil.isValidEmail(email)) {
            throw new AppException("email", "Please enter a valid email address.");
        }
        String cleanEmail = email.trim().toLowerCase();

        if (!ValidationUtil.isValidPassword(password)) {
            throw new AppException("password",
                    "Password must be at least " + ValidationUtil.MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new AppException("confirmPassword", "Passwords do not match.");
        }

        try {
            if (userDAO.emailExists(cleanEmail)) {
                throw new AppException("email", "This email is already registered.");
            }
            User user = new User(cleanName, cleanEmail, PasswordUtil.hash(password), Role.STUDENT);
            User saved = userDAO.insert(user);
            LOG.info(() -> "Registered new student id=" + saved.getId());
            return saved;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Registration failed for " + cleanEmail, e);
            throw new AppException("We could not create your account right now. Please try again.");
        }
    }

    public User login(String email, String password) {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            throw new AppException("Please enter both your email and your password.");
        }
        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase());
            // One vague message covers both cases so the form cannot be used to enumerate accounts.
            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                throw new AppException("Invalid email or password.");
            }
            StatsCounter.loginSucceeded();
            LOG.info(() -> "Login OK for user id=" + user.getId() + " role=" + user.getRole());
            return user;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Login query failed", e);
            throw new AppException("We could not sign you in right now. Please try again.");
        }
    }
}
