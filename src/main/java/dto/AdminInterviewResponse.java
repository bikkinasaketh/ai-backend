package com.aiinterview.aiinterviewbackend.dto;

import java.time.LocalDateTime;

public class AdminInterviewResponse {

    private Long id;

    private String userName;

    private String userEmail;

    private String topic;

    private String difficulty;

    private int score;

    private int totalQuestions;

    private LocalDateTime completedAt;

    public AdminInterviewResponse() {
    }

    public AdminInterviewResponse(
            Long id,
            String userName,
            String userEmail,
            String topic,
            String difficulty,
            int score,
            int totalQuestions,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.topic = topic;
        this.difficulty = difficulty;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}