package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.service.AiEvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class AiController {

    private final AiEvaluationService aiEvaluationService;

    public AiController(
            AiEvaluationService aiEvaluationService
    ) {
        this.aiEvaluationService =
                aiEvaluationService;
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<QuestionResponse> generateQuestions(
            @Valid @RequestBody QuestionRequest request
    ) {

        List<String> questions =
                aiEvaluationService.generateQuestions(
                        request.getTopic(),
                        request.getDifficulty(),
                        request.getNumberOfQuestions()
                );

        return ResponseEntity.ok(
                new QuestionResponse(questions)
        );
    }

    public static class QuestionRequest {

        @NotBlank(message = "Topic is required")
        private String topic;

        @NotBlank(message = "Difficulty is required")
        private String difficulty;

        @NotNull(message = "Number of questions is required")
        @Min(
                value = 1,
                message = "At least 1 question is required"
        )
        @Max(
                value = 20,
                message = "Maximum 20 questions allowed"
        )
        private Integer numberOfQuestions;

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

        public Integer getNumberOfQuestions() {
            return numberOfQuestions;
        }

        public void setNumberOfQuestions(
                Integer numberOfQuestions
        ) {
            this.numberOfQuestions =
                    numberOfQuestions;
        }
    }

    public static class QuestionResponse {

        private final List<String> questions;

        public QuestionResponse(
                List<String> questions
        ) {
            this.questions = questions;
        }

        public List<String> getQuestions() {
            return questions;
        }
    }
}