package com.quizdesk.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** An MCQ with four labelled choices (A-D). */
public class Question {

    private long id;
    private long quizId;
    private String questionText;
    private char correctOption = 'A';
    private String explanation;
    private Timestamp createdAt;
    private final List<QuizOption> options = new ArrayList<>();

    public Question() { }

    public Question(long quizId, String questionText, char correctOption, String explanation) {
        this.quizId = quizId;
        this.questionText = questionText;
        this.correctOption = correctOption;
        this.explanation = explanation;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getQuizId() { return quizId; }
    public void setQuizId(long quizId) { this.quizId = quizId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public char getCorrectOption() { return correctOption; }
    public void setCorrectOption(char correctOption) { this.correctOption = correctOption; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<QuizOption> getOptions() { return options; }

    public void addOption(QuizOption option) { this.options.add(option); }

    public String getCorrectOptionText() {
        for (QuizOption option : options) {
            if (option.getOptionKey() == correctOption) { return option.getOptionText(); }
        }
        return "";
    }

    public String getShortText(int max) {
        if (questionText == null) { return ""; }
        return questionText.length() <= max ? questionText : questionText.substring(0, max) + "...";
    }

    @Override
    public String toString() {
        return "Question{id=" + id + ", quizId=" + quizId + ", correct=" + correctOption + '}';
    }
}
