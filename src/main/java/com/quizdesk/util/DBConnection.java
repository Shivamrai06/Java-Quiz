package com.quizdesk.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;

/**
 * Small hand-written JDBC connection pool shared by the whole application.
 *
 * <p>try-with-resources keeps working: close() on the returned connection is intercepted and the
 * physical connection goes back to the pool instead of being closed.</p>
 */
public final class DBConnection {

    private static final String CONFIG_FILE = "db.properties";

    private static String url;
    private static String username;
    private static String password;
    private static int poolSize;
    private static long acquireTimeoutMs;
    private static boolean configured;

    private static final Deque<Connection> IDLE = new ArrayDeque<>();
    private static int created;
    private static int leased;
    private static long totalLeases;

    private DBConnection() { }

    private static synchronized void loadConfiguration() {
        if (configured) { return; }
        try (InputStream raw = DBConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (raw == null) {
                throw new IllegalStateException("Configuration file '" + CONFIG_FILE
                        + "' was not found. Copy db.properties.example to db.properties and set your MySQL credentials.");
            }
            Properties props = new Properties();
            props.load(new InputStreamReader(raw, StandardCharsets.UTF_8));
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password", "");
            poolSize = Integer.parseInt(props.getProperty("db.pool.size", "5"));
            acquireTimeoutMs = Long.parseLong(props.getProperty("db.pool.timeout.ms", "10000"));
            if (url == null || username == null) {
                throw new IllegalStateException("db.url and db.username are required in " + CONFIG_FILE);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            configured = true;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + CONFIG_FILE, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL Connector/J is not on the classpath.", e);
        }
    }

    public static void warmUp() { loadConfiguration(); }

    public static Connection getConnection() throws SQLException {
        loadConfiguration();
        synchronized (IDLE) {
            final long deadline = System.currentTimeMillis() + acquireTimeoutMs;
            while (true) {
                while (!IDLE.isEmpty()) {
                    Connection candidate = IDLE.pollFirst();
                    if (isUsable(candidate)) {
                        leased++;
                        totalLeases++;
                        return wrap(candidate);
                    }
                    closeQuietly(candidate);
                    created--;
                }
                if (created < poolSize) {
                    Connection fresh = DriverManager.getConnection(url, username, password);
                    fresh.setAutoCommit(true);
                    created++;
                    leased++;
                    totalLeases++;
                    return wrap(fresh);
                }
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    throw new SQLException("Timed out after " + acquireTimeoutMs
                            + " ms waiting for a database connection (pool size " + poolSize + ").");
                }
                try {
                    IDLE.wait(remaining);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Interrupted while waiting for a database connection.", e);
                }
            }
        }
    }

    private static boolean isUsable(Connection candidate) {
        try {
            return !candidate.isClosed() && candidate.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private static Connection wrap(final Connection physical) {
        return (Connection) Proxy.newProxyInstance(
                DBConnection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName()) && method.getParameterCount() == 0) {
                        release(physical);
                        return null;
                    }
                    try {
                        return method.invoke(physical, args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                });
    }

    private static void release(Connection physical) {
        synchronized (IDLE) {
            leased--;
            try {
                if (physical != null && !physical.isClosed()) {
                    if (!physical.getAutoCommit()) {
                        physical.rollback();
                        physical.setAutoCommit(true);
                    }
                    IDLE.addLast(physical);
                } else if (physical != null) {
                    created--;
                }
            } catch (SQLException e) {
                closeQuietly(physical);
                created--;
            }
            IDLE.notifyAll();
        }
    }

    private static void closeQuietly(Connection c) {
        try {
            if (c != null && !c.isClosed()) { c.close(); }
        } catch (SQLException ignored) {
            // nothing useful to do
        }
    }

    public static String poolSummary() {
        synchronized (IDLE) {
            return "idle=" + IDLE.size() + " leased=" + leased + " created=" + created + " of " + poolSize;
        }
    }

    public static long totalLeases() {
        synchronized (IDLE) { return totalLeases; }
    }

    public static void shutdown() {
        synchronized (IDLE) {
            while (!IDLE.isEmpty()) {
                Connection c = IDLE.pollFirst();
                closeQuietly(c);
                created--;
            }
            IDLE.notifyAll();
        }
    }
}
