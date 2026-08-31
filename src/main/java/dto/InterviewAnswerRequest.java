package com.aiinterview.aiinterviewbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class InterviewAnswerRequest {

    @NotBlank(
            message = "Question is required"
    )
    private String question;


    @NotBlank(
            message = "Answer is required"
    )
    private String answer;


    @Min(
            value = 0,
            message = "AI score cannot be negative"
    )
    @Max(
            value = 100,
            message = "AI score cannot exceed 100"
    )
    private int aiScore;


    private String feedback;


    private List<String> correctPoints =
            new ArrayList<>();


    private List<String> improvements =
            new ArrayList<>();


    private List<String> learnNext =
            new ArrayList<>();


    private String betterAnswer;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public InterviewAnswerRequest() {
    }


    public InterviewAnswerRequest(
            String question,
            String answer
    ) {

        this.question = question;
        this.answer = answer;
    }


    // =========================================================
    // QUESTION
    // =========================================================

    public String getQuestion() {
        return question;
    }


    public void setQuestion(
            String question
    ) {

        this.question = question;
    }


    // =========================================================
    // ANSWER
    // =========================================================

    public String getAnswer() {
        return answer;
    }


    public void setAnswer(
            String answer
    ) {

        this.answer = answer;
    }


    // =========================================================
    // AI SCORE
    // =========================================================

    public int getAiScore() {
        return aiScore;
    }


    public void setAiScore(
            int aiScore
    ) {

        this.aiScore = aiScore;
    }


    // =========================================================
    // FEEDBACK
    // =========================================================

    public String getFeedback() {
        return feedback;
    }


    public void setFeedback(
            String feedback
    ) {

        this.feedback = feedback;
    }


    // =========================================================
    // CORRECT POINTS
    // =========================================================

    public List<String> getCorrectPoints() {
        return correctPoints;
    }


    public void setCorrectPoints(
            List<String> correctPoints
    ) {

        this.correctPoints =
                correctPoints != null
                        ? correctPoints
                        : new ArrayList<>();
    }


    // =========================================================
    // IMPROVEMENTS
    // =========================================================

    public List<String> getImprovements() {
        return improvements;
    }


    public void setImprovements(
            List<String> improvements
    ) {

        this.improvements =
                improvements != null
                        ? improvements
                        : new ArrayList<>();
    }


    // =========================================================
    // LEARN NEXT
    // =========================================================

    public List<String> getLearnNext() {
        return learnNext;
    }


    public void setLearnNext(
            List<String> learnNext
    ) {

        this.learnNext =
                learnNext != null
                        ? learnNext
                        : new ArrayList<>();
    }


    // =========================================================
    // BETTER ANSWER
    // =========================================================

    public String getBetterAnswer() {
        return betterAnswer;
    }


    public void setBetterAnswer(
            String betterAnswer
    ) {

        this.betterAnswer =
                betterAnswer;
    }
}