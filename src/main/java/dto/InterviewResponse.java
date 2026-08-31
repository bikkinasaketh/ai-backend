package com.aiinterview.aiinterviewbackend.dto;

import java.time.LocalDateTime;

public class InterviewResponse {

    private Long id;
    private Long userId;
    private int score;
    private int totalQuestions;
    private LocalDateTime completedAt;

    public InterviewResponse() {
    }

    public InterviewResponse(
            Long id,
            Long userId,
            int score,
            int totalQuestions,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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