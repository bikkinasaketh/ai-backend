package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.service.InterviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:5173")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/save")
    public ResponseEntity<Interview> saveInterview(
            @RequestBody InterviewRequest request
    ) {

        Interview savedInterview =
                interviewService.saveInterview(
                        request.getUserId(),
                        request.getScore(),
                        request.getTotalQuestions()
                );

        return ResponseEntity.ok(savedInterview);
    }

    public static class InterviewRequest {

        private Long userId;
        private int score;
        private int totalQuestions;

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
    }
}