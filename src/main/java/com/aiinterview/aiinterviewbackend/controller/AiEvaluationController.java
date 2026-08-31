package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.service.AiEvaluationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class AiEvaluationController {

    private final AiEvaluationService aiEvaluationService;

    public AiEvaluationController(
            AiEvaluationService aiEvaluationService
    ) {
        this.aiEvaluationService = aiEvaluationService;
    }

    // =========================================================
    // EVALUATE COMPLETE INTERVIEW
    // =========================================================

    @PostMapping("/evaluate")
    public ResponseEntity<List<EvaluationResponse>> evaluateInterview(
            @Valid @RequestBody EvaluationRequest request
    ) {

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();

        for (AnswerItem item : request.getAnswers()) {

            questions.add(item.getQuestion());
            answers.add(item.getAnswer());
        }

        // =====================================================
        // ONE CALL TO AI EVALUATION SERVICE
        // =====================================================

        List<AiEvaluationService.EvaluationResult> evaluations =
                aiEvaluationService.evaluateAnswers(
                        request.getTopic(),
                        request.getDifficulty(),
                        questions,
                        answers
                );

        // =====================================================
        // VALIDATE RESULT COUNT
        // =====================================================

        if (evaluations.size() != request.getAnswers().size()) {

            throw new RuntimeException(
                    "AI evaluation count does not match answer count"
            );
        }

        // =====================================================
        // BUILD RESPONSE
        // =====================================================

        List<EvaluationResponse> response =
                new ArrayList<>();

        for (
                AiEvaluationService.EvaluationResult evaluation
                : evaluations
        ) {

            response.add(
                    new EvaluationResponse(
                            evaluation.getScore(),
                            evaluation.getFeedback(),
                            evaluation.getCorrectPoints(),
                            evaluation.getImprovements(),
                            evaluation.getLearnNext(),
                            evaluation.getBetterAnswer()
                    )
            );
        }

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // EVALUATION REQUEST
    // =========================================================

    public static class EvaluationRequest {

        @NotBlank(
                message = "Topic is required"
        )
        private String topic;

        @NotBlank(
                message = "Difficulty is required"
        )
        private String difficulty;

        @NotEmpty(
                message = "Answers are required"
        )
        private List<AnswerItem> answers;

        public EvaluationRequest() {
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

        public List<AnswerItem> getAnswers() {
            return answers;
        }

        public void setAnswers(
                List<AnswerItem> answers
        ) {
            this.answers = answers;
        }
    }

    // =========================================================
    // ANSWER ITEM
    // =========================================================

    public static class AnswerItem {

        @NotBlank(
                message = "Question is required"
        )
        private String question;

        @NotBlank(
                message = "Answer is required"
        )
        private String answer;

        public AnswerItem() {
        }

        public AnswerItem(
                String question,
                String answer
        ) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(
                String question
        ) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(
                String answer
        ) {
            this.answer = answer;
        }
    }

    // =========================================================
    // EVALUATION RESPONSE
    // =========================================================

    public static class EvaluationResponse {

        private final int score;

        private final String feedback;

        private final List<String> correctPoints;

        private final List<String> improvements;

        private final List<String> learnNext;

        private final String betterAnswer;

        public EvaluationResponse(
                int score,
                String feedback,
                List<String> correctPoints,
                List<String> improvements,
                List<String> learnNext,
                String betterAnswer
        ) {

            this.score = score;

            this.feedback =
                    feedback != null
                            ? feedback
                            : "";

            this.correctPoints =
                    correctPoints != null
                            ? correctPoints
                            : new ArrayList<>();

            this.improvements =
                    improvements != null
                            ? improvements
                            : new ArrayList<>();

            this.learnNext =
                    learnNext != null
                            ? learnNext
                            : new ArrayList<>();

            this.betterAnswer =
                    betterAnswer != null
                            ? betterAnswer
                            : "";
        }

        public int getScore() {
            return score;
        }

        public String getFeedback() {
            return feedback;
        }

        public List<String> getCorrectPoints() {
            return correctPoints;
        }

        public List<String> getImprovements() {
            return improvements;
        }

        public List<String> getLearnNext() {
            return learnNext;
        }

        public String getBetterAnswer() {
            return betterAnswer;
        }
    }
}