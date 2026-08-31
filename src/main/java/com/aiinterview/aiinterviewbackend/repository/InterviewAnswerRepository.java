package com.aiinterview.aiinterviewbackend.repository;

import com.aiinterview.aiinterviewbackend.entity.InterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewAnswerRepository
        extends JpaRepository<InterviewAnswer, Long> {

    List<InterviewAnswer> findByInterviewId(Long interviewId);
}
