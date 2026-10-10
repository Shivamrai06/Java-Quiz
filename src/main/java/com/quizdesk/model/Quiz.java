package com.quizdesk.model;

import java.sql.Timestamp;

/** A quiz: the container for questions and its publication state. */
public class Quiz {

    public enum Status {
        DRAFT,
        PUBLISHED;

        public static Status fromDatabase(String value) {
            if (value == null) { return DRAFT; }
            try {
                return Status.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return DRAFT;
            }
        }

        public String getLabel() { return this == PUBLISHED ? "Published" : "Draft"; }
    }

    private long id;
    private String title;
    private String description;
    private String category;
    private Integer durationMin;
    private Status status = Status.DRAFT;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int questionCount;
    private int attemptCount;

    public Quiz() { }

    public Quiz(String title, String description, String category, Integer durationMin, Status status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.durationMin = durationMin;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    public boolean isPublished() { return status == Status.PUBLISHED; }

    public boolean isAttemptable() { return isPublished() && questionCount > 0; }

    @Override
    public String toString() {
        return "Quiz{id=" + id + ", title='" + title + "', status=" + status + '}';
    }
}
