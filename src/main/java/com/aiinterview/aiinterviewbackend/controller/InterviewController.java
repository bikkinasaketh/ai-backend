package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.dto.InterviewAnswerRequest;
import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.entity.InterviewAnswer;
import com.aiinterview.aiinterviewbackend.repository.InterviewAnswerRepository;
import com.aiinterview.aiinterviewbackend.service.AiEvaluationService;
import com.aiinterview.aiinterviewbackend.service.InterviewService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class InterviewController {

    private final InterviewService interviewService;

    private final InterviewAnswerRepository interviewAnswerRepository;

    private final AiEvaluationService aiEvaluationService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public InterviewController(
            InterviewService interviewService,
            InterviewAnswerRepository interviewAnswerRepository,
            AiEvaluationService aiEvaluationService
    ) {

        this.interviewService =
                interviewService;

        this.interviewAnswerRepository =
                interviewAnswerRepository;

        this.aiEvaluationService =
                aiEvaluationService;
    }


    // =========================================================
    // GENERATE INTERVIEW QUESTIONS
    // =========================================================

    @GetMapping("/questions")
    public ResponseEntity<List<String>> generateQuestions(

            @RequestParam String topic,

            @RequestParam String difficulty,

            @RequestParam int numberOfQuestions

    ) {

        System.out.println(
                "Generating questions:"
        );

        System.out.println(
                "Topic = " + topic
        );

        System.out.println(
                "Difficulty = " + difficulty
        );

        System.out.println(
                "Number = " + numberOfQuestions
        );


        List<String> questions =
                aiEvaluationService.generateQuestions(
                        topic,
                        difficulty,
                        numberOfQuestions
                );


        return ResponseEntity.ok(
                questions
        );
    }


    // =========================================================
    // SAVE INTERVIEW
    // =========================================================

    @PostMapping("/save")
    public ResponseEntity<Interview> saveInterview(

            @Valid
            @RequestBody InterviewRequest request

    ) {

        Interview savedInterview =
                interviewService.saveInterview(

                        request.getUserId(),

                        request.getTopic(),

                        request.getDifficulty(),

                        request.getScore(),

                        request.getTotalQuestions(),

                        request.getAnswers()
                );


        return ResponseEntity.ok(
                savedInterview
        );
    }


    // =========================================================
    // GET USER INTERVIEW HISTORY
    // =========================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Interview>>
    getUserInterviews(

            @PathVariable Long userId

    ) {

        List<Interview> interviews =
                interviewService
                        .getInterviewsByUser(
                                userId
                        );


        return ResponseEntity.ok(
                interviews
        );
    }


    // =========================================================
    // GET INTERVIEW ANSWERS
    // =========================================================

    @GetMapping("/{interviewId}/answers")
    public ResponseEntity<List<InterviewAnswer>>
    getInterviewAnswers(

            @PathVariable Long interviewId

    ) {

        List<InterviewAnswer> answers =
                interviewAnswerRepository
                        .findByInterviewId(
                                interviewId
                        );


        return ResponseEntity.ok(
                answers
        );
    }


    // =========================================================
    // INTERVIEW REQUEST
    // =========================================================

    public static class InterviewRequest {

        @NotNull(
                message = "User ID is required"
        )
        private Long userId;


        @NotBlank(
                message = "Topic is required"
        )
        private String topic;


        @NotBlank(
                message = "Difficulty is required"
        )
        private String difficulty;


        @Min(
                value = 0,
                message = "Score cannot be negative"
        )
        @Max(
                value = 100,
                message = "Score cannot be greater than 100"
        )
        private int score;


        @Min(
                value = 1,
                message = "Total questions must be at least 1"
        )
        private int totalQuestions;


        @Valid
        @NotNull(
                message = "Answers are required"
        )
        private List<InterviewAnswerRequest> answers;


        public Long getUserId() {
            return userId;
        }


        public void setUserId(Long userId) {
            this.userId = userId;
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


        public void setDifficulty(
                String difficulty
        ) {
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


        public void setTotalQuestions(
                int totalQuestions
        ) {
            this.totalQuestions =
                    totalQuestions;
        }


        public List<InterviewAnswerRequest>
        getAnswers() {

            return answers;
        }


        public void setAnswers(
                List<InterviewAnswerRequest> answers
        ) {

            this.answers =
                    answers;
        }
    }
}