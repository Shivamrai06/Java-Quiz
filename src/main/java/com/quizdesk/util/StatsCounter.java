package com.quizdesk.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Runtime counters. AtomicLong is used instead of a plain long because a servlet is called from many
 * threads at once and "count++" is not atomic.
 */
public final class StatsCounter {

    private static final AtomicLong TOTAL_REQUESTS = new AtomicLong();
    private static final AtomicLong LOGINS = new AtomicLong();
    private static final AtomicLong SUBMISSIONS = new AtomicLong();
    private static final AtomicLong ACTIVE_SESSIONS = new AtomicLong();
    private static final long STARTED_AT = System.currentTimeMillis();

    private StatsCounter() { }

    public static void requestServed() { TOTAL_REQUESTS.incrementAndGet(); }
    public static void loginSucceeded() { LOGINS.incrementAndGet(); }
    public static void quizSubmitted() { SUBMISSIONS.incrementAndGet(); }
    public static void sessionOpened() { ACTIVE_SESSIONS.incrementAndGet(); }
    public static void sessionClosed() {
        ACTIVE_SESSIONS.updateAndGet(current -> current > 0 ? current - 1 : 0);
    }

    public static long totalRequests() { return TOTAL_REQUESTS.get(); }
    public static long successfulLogins() { return LOGINS.get(); }
    public static long quizSubmissions() { return SUBMISSIONS.get(); }
    public static long activeSessions() { return ACTIVE_SESSIONS.get(); }
    public static long uptimeMinutes() { return (System.currentTimeMillis() - STARTED_AT) / 60_000L; }
}
