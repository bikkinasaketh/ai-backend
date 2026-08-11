package com.aiinterview.aiinterviewbackend.repository;

import com.aiinterview.aiinterviewbackend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
}