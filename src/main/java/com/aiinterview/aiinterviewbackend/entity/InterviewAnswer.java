package com.aiinterview.aiinterviewbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "interview_answers")
public class InterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "interview_id",
            nullable = false
    )
    private Interview interview;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String question;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String answer;

    @Column(nullable = false)
    private int aiScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(columnDefinition = "TEXT")
    private String correctPoints;

    @Column(columnDefinition = "TEXT")
    private String improvements;

    @Column(columnDefinition = "TEXT")
    private String learnNext;

    @Column(columnDefinition = "TEXT")
    private String betterAnswer;

    public InterviewAnswer() {
    }

    public InterviewAnswer(
            Interview interview,
            String question,
            String answer,
            int aiScore,
            String feedback
    ) {
        this.interview = interview;
        this.question = question;
        this.answer = answer;
        this.aiScore = aiScore;
        this.feedback = feedback;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Interview getInterview() {
        return interview;
    }

    public void setInterview(Interview interview) {
        this.interview = interview;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getAiScore() {
        return aiScore;
    }

    public void setAiScore(int aiScore) {
        this.aiScore = aiScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCorrectPoints() {
        return correctPoints;
    }

    public void setCorrectPoints(String correctPoints) {
        this.correctPoints = correctPoints;
    }

    public String getImprovements() {
        return improvements;
    }

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }

    public String getLearnNext() {
        return learnNext;
    }

    public void setLearnNext(String learnNext) {
        this.learnNext = learnNext;
    }

    public String getBetterAnswer() {
        return betterAnswer;
    }

    public void setBetterAnswer(String betterAnswer) {
        this.betterAnswer = betterAnswer;
    }
}