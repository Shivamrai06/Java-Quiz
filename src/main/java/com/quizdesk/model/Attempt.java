package com.quizdesk.model;

import java.sql.Timestamp;

/** One submitted run of a quiz. Score and percentage are always computed on the server. */
public class Attempt {

    private long id;
    private long userId;
    private long quizId;
    private int score;
    private int totalQuestions;
    private double percentage;
    private Timestamp startedAt;
    private Timestamp submittedAt;
    private String studentName;
    private String studentEmail;
    private String quizTitle;

    public Attempt() { }

    public Attempt(long userId, long quizId) {
        this.userId = userId;
        this.quizId = quizId;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getQuizId() { return quizId; }
    public void setQuizId(long quizId) { this.quizId = quizId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public Timestamp getStartedAt() { return startedAt; }
    public void setStartedAt(Timestamp startedAt) { this.startedAt = startedAt; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public int getIncorrectCount() { return Math.max(0, totalQuestions - score); }

    public String getGradeBand() {
        if (percentage >= 80) { return "good"; }
        if (percentage >= 50) { return "fair"; }
        return "poor";
    }

    @Override
    public String toString() {
        return "Attempt{id=" + id + ", quizId=" + quizId + ", score=" + score + '/' + totalQuestions + '}';
    }
}
