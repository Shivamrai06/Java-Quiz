package com.quizdesk.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Start-up validation of the database configuration, and clean shutdown of the pool. */
@WebListener
public final class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("[QuizDesk] Starting application...");
        try {
            DBConnection.warmUp();
            System.out.println("[QuizDesk] Database configuration loaded. Pool: " + DBConnection.poolSummary());
        } catch (RuntimeException e) {
            System.err.println("[QuizDesk] Database is NOT ready: " + e.getMessage());
            event.getServletContext().log("QuizDesk database configuration problem", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        DBConnection.shutdown();
        System.out.println("[QuizDesk] Stopped. Total connections lent: " + DBConnection.totalLeases());
    }
}
