package com.aiinterview.aiinterviewbackend.service;

import com.aiinterview.aiinterviewbackend.dto.InterviewAnswerRequest;
import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.entity.InterviewAnswer;
import com.aiinterview.aiinterviewbackend.entity.User;
import com.aiinterview.aiinterviewbackend.repository.InterviewAnswerRepository;
import com.aiinterview.aiinterviewbackend.repository.InterviewRepository;
import com.aiinterview.aiinterviewbackend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;

    private final InterviewAnswerRepository interviewAnswerRepository;

    private final UserRepository userRepository;


    public InterviewService(
            InterviewRepository interviewRepository,
            InterviewAnswerRepository interviewAnswerRepository,
            UserRepository userRepository
    ) {

        this.interviewRepository =
                interviewRepository;

        this.interviewAnswerRepository =
                interviewAnswerRepository;

        this.userRepository =
                userRepository;
    }


    // =========================================================
    // SAVE COMPLETE INTERVIEW
    // =========================================================
    //
    // IMPORTANT:
    //
    // Gemini evaluation is NOT called here.
    //
    // The answer was already evaluated by:
    //
    // POST /api/interviews/evaluate-answer
    //
    // So we simply save the evaluation received
    // from the frontend.
    //
    // =========================================================

    @Transactional
    public Interview saveInterview(

            Long userId,

            String topic,

            String difficulty,

            int score,

            int totalQuestions,

            List<InterviewAnswerRequest> answers

    ) {

        // =====================================================
        // 1. FIND USER
        // =====================================================

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );


        // =====================================================
        // 2. VALIDATE ANSWERS
        // =====================================================

        if (
                answers == null ||
                answers.isEmpty()
        ) {

            throw new RuntimeException(
                    "Interview answers are required"
            );
        }


        if (
                answers.size()
                        != totalQuestions
        ) {

            throw new RuntimeException(
                    "Number of answers must match total questions"
            );
        }


        // =====================================================
        // 3. VALIDATE EACH ANSWER
        // =====================================================

        for (
                InterviewAnswerRequest answer
                : answers
        ) {

            if (
                    answer.getQuestion() == null ||
                    answer.getQuestion().isBlank()
            ) {

                throw new RuntimeException(
                        "Question cannot be empty"
                );
            }


            if (
                    answer.getAnswer() == null ||
                    answer.getAnswer().isBlank()
            ) {

                throw new RuntimeException(
                        "Answer cannot be empty"
                );
            }


            // AI score validation

            if (
                    answer.getAiScore() < 0 ||
                    answer.getAiScore() > 100
            ) {

                throw new RuntimeException(
                        "AI score must be between 0 and 100"
                );
            }
        }


        // =====================================================
        // 4. CALCULATE OVERALL SCORE
        // =====================================================
        //
        // Do NOT trust frontend overall score.
        //
        // Calculate it again from the individual
        // AI scores that were already generated.
        //
        // No Gemini call is required.
        //
        // =====================================================

        int totalAiScore = 0;

        for (
                InterviewAnswerRequest answer
                : answers
        ) {

            totalAiScore +=
                    answer.getAiScore();
        }


        int overallScore =
                Math.round(
                        (float) totalAiScore
                                / answers.size()
                );


        // =====================================================
        // 5. CREATE INTERVIEW
        // =====================================================

        Interview interview =
                new Interview();


        interview.setUser(
                user
        );


        interview.setTopic(
                topic
        );


        interview.setDifficulty(
                difficulty
        );


        interview.setScore(
                overallScore
        );


        interview.setTotalQuestions(
                totalQuestions
        );


        interview.setCompletedAt(
                LocalDateTime.now()
        );


        // =====================================================
        // 6. SAVE INTERVIEW
        // =====================================================

        Interview savedInterview =
                interviewRepository.save(
                        interview
                );


        // =====================================================
        // 7. SAVE EACH ANSWER
        // =====================================================
        //
        // The AI evaluation already exists.
        //
        // Save:
        //
        // - AI score
        // - feedback
        // - correct points
        // - improvements
        // - learn next
        // - better answer
        //
        // =====================================================

        for (
                InterviewAnswerRequest answerRequest
                : answers
        ) {

            InterviewAnswer interviewAnswer =
                    new InterviewAnswer();


            // -------------------------------------------------
            // Interview
            // -------------------------------------------------

            interviewAnswer.setInterview(
                    savedInterview
            );


            // -------------------------------------------------
            // Question
            // -------------------------------------------------

            interviewAnswer.setQuestion(
                    answerRequest.getQuestion()
            );


            // -------------------------------------------------
            // User Answer
            // -------------------------------------------------

            interviewAnswer.setAnswer(
                    answerRequest.getAnswer()
            );


            // -------------------------------------------------
            // AI Score
            // -------------------------------------------------

            interviewAnswer.setAiScore(
                    answerRequest.getAiScore()
            );


            // -------------------------------------------------
            // AI Feedback
            // -------------------------------------------------

            interviewAnswer.setFeedback(
                    answerRequest.getFeedback()
            );


            // -------------------------------------------------
            // What user did correctly
            // -------------------------------------------------

            interviewAnswer.setCorrectPoints(
                    convertListToText(
                            answerRequest.getCorrectPoints()
                    )
            );


            // -------------------------------------------------
            // Improvements
            // -------------------------------------------------

            interviewAnswer.setImprovements(
                    convertListToText(
                            answerRequest.getImprovements()
                    )
            );


            // -------------------------------------------------
            // Learn Next
            // -------------------------------------------------

            interviewAnswer.setLearnNext(
                    convertListToText(
                            answerRequest.getLearnNext()
                    )
            );


            // -------------------------------------------------
            // Better Answer
            // -------------------------------------------------

            interviewAnswer.setBetterAnswer(
                    answerRequest.getBetterAnswer()
            );


            // -------------------------------------------------
            // SAVE ANSWER
            // -------------------------------------------------

            interviewAnswerRepository.save(
                    interviewAnswer
            );
        }


        // =====================================================
        // 8. RETURN SAVED INTERVIEW
        // =====================================================

        return savedInterview;
    }


    // =========================================================
    // CONVERT LIST TO DATABASE TEXT
    // =========================================================

    private String convertListToText(
            List<String> values
    ) {

        if (
                values == null ||
                values.isEmpty()
        ) {

            return "";
        }


        return String.join(
                "\n",
                values
        );
    }


    // =========================================================
    // GET USER INTERVIEW HISTORY
    // =========================================================

    public List<Interview> getInterviewsByUser(
            Long userId
    ) {

        return interviewRepository
                .findByUserId(
                        userId
                );
    }
}