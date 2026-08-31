package com.aiinterview.aiinterviewbackend.dto;

public class AdminInterviewAnswerResponse {

    private Long id;

    private String question;

    private String answer;

    private int aiScore;

    private String feedback;

    private String correctPoints;

    private String improvements;

    private String learnNext;

    private String betterAnswer;


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public AdminInterviewAnswerResponse() {
    }


    // =========================================================
    // FULL CONSTRUCTOR
    // =========================================================

    public AdminInterviewAnswerResponse(
            Long id,
            String question,
            String answer,
            int aiScore,
            String feedback,
            String correctPoints,
            String improvements,
            String learnNext,
            String betterAnswer
    ) {

        this.id = id;

        this.question = question;

        this.answer = answer;

        this.aiScore = aiScore;

        this.feedback = feedback;

        this.correctPoints = correctPoints;

        this.improvements = improvements;

        this.learnNext = learnNext;

        this.betterAnswer = betterAnswer;
    }


    // =========================================================
    // GET ID
    // =========================================================

    public Long getId() {
        return id;
    }


    // =========================================================
    // SET ID
    // =========================================================

    public void setId(Long id) {
        this.id = id;
    }


    // =========================================================
    // GET QUESTION
    // =========================================================

    public String getQuestion() {
        return question;
    }


    // =========================================================
    // SET QUESTION
    // =========================================================

    public void setQuestion(String question) {
        this.question = question;
    }


    // =========================================================
    // GET ANSWER
    // =========================================================

    public String getAnswer() {
        return answer;
    }


    // =========================================================
    // SET ANSWER
    // =========================================================

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    // =========================================================
    // GET AI SCORE
    // =========================================================

    public int getAiScore() {
        return aiScore;
    }


    // =========================================================
    // SET AI SCORE
    // =========================================================

    public void setAiScore(int aiScore) {
        this.aiScore = aiScore;
    }


    // =========================================================
    // GET FEEDBACK
    // =========================================================

    public String getFeedback() {
        return feedback;
    }


    // =========================================================
    // SET FEEDBACK
    // =========================================================

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }


    // =========================================================
    // GET CORRECT POINTS
    // =========================================================

    public String getCorrectPoints() {
        return correctPoints;
    }


    // =========================================================
    // SET CORRECT POINTS
    // =========================================================

    public void setCorrectPoints(String correctPoints) {
        this.correctPoints = correctPoints;
    }


    // =========================================================
    // GET IMPROVEMENTS
    // =========================================================

    public String getImprovements() {
        return improvements;
    }


    // =========================================================
    // SET IMPROVEMENTS
    // =========================================================

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }


    // =========================================================
    // GET LEARN NEXT
    // =========================================================

    public String getLearnNext() {
        return learnNext;
    }


    // =========================================================
    // SET LEARN NEXT
    // =========================================================

    public void setLearnNext(String learnNext) {
        this.learnNext = learnNext;
    }


    // =========================================================
    // GET BETTER ANSWER
    // =========================================================

    public String getBetterAnswer() {
        return betterAnswer;
    }


    // =========================================================
    // SET BETTER ANSWER
    // =========================================================

    public void setBetterAnswer(String betterAnswer) {
        this.betterAnswer = betterAnswer;
    }
}