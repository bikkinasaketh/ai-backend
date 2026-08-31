package com.aiinterview.aiinterviewbackend.dto;

public class AdminDashboardStatsResponse {

    private long totalUsers;
    private long totalInterviews;
    private double averageScore;
    private int highestScore;

    public AdminDashboardStatsResponse() {
    }

    public AdminDashboardStatsResponse(
            long totalUsers,
            long totalInterviews,
            double averageScore,
            int highestScore
    ) {
        this.totalUsers = totalUsers;
        this.totalInterviews = totalInterviews;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(long totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
}