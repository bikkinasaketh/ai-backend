package com.aiinterview.aiinterviewbackend.service;

import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;

    public InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    public Interview saveInterview(
            Long userId,
            int score,
            int totalQuestions
    ) {

        Interview interview = new Interview();

        interview.setUserId(userId);
        interview.setScore(score);
        interview.setTotalQuestions(totalQuestions);
        interview.setCompletedAt(LocalDateTime.now());

        return interviewRepository.save(interview);
    }
}