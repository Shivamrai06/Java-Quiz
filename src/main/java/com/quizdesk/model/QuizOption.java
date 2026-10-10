package com.quizdesk.model;

/** One answer choice of a question. */
public class QuizOption {

    private long id;
    private long questionId;
    private char optionKey;
    private String optionText;

    public QuizOption() { }

    public QuizOption(char optionKey, String optionText) {
        this.optionKey = optionKey;
        this.optionText = optionText;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public char getOptionKey() { return optionKey; }
    public void setOptionKey(char optionKey) { this.optionKey = optionKey; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    @Override
    public String toString() { return optionKey + ") " + optionText; }
}
