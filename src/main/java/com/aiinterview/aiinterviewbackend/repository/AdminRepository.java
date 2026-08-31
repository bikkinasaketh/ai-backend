package com.aiinterview.aiinterviewbackend.repository;

import com.aiinterview.aiinterviewbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}