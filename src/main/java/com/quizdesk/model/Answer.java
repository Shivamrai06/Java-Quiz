package com.quizdesk.model;

/** One stored answer: what the student picked, and whether it was right. */
public class Answer {

    private long id;
    private long attemptId;
    private long questionId;
    private Character selectedOption;
    private boolean correct;
    private String questionText;
    private char correctOption;
    private String explanation;
    private String selectedText;
    private String correctText;

    public Answer() { }

    public Answer(long questionId, Character selectedOption, boolean correct) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAttemptId() { return attemptId; }
    public void setAttemptId(long attemptId) { this.attemptId = attemptId; }

    public long getQuestionId() { return questionId; }
    public void setQuestionId(long questionId) { this.questionId = questionId; }

    public Character getSelectedOption() { return selectedOption; }
    public void setSelectedOption(Character selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public char getCorrectOption() { return correctOption; }
    public void setCorrectOption(char correctOption) { this.correctOption = correctOption; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getSelectedText() { return selectedText; }
    public void setSelectedText(String selectedText) { this.selectedText = selectedText; }

    public String getCorrectText() { return correctText; }
    public void setCorrectText(String correctText) { this.correctText = correctText; }

    public boolean isUnanswered() { return selectedOption == null; }

    public String getStatusLabel() {
        if (isUnanswered()) { return "Not answered"; }
        return correct ? "Correct" : "Incorrect";
    }

    public String getStatusClass() {
        if (isUnanswered()) { return "fair"; }
        return correct ? "good" : "poor";
    }

    @Override
    public String toString() {
        return "Answer{questionId=" + questionId + ", selected=" + selectedOption + ", correct=" + correct + '}';
    }
}
