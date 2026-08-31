package com.aiinterview.aiinterviewbackend.repository;

import com.aiinterview.aiinterviewbackend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByUserId(Long userId);
}