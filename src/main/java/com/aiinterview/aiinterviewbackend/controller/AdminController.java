package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.dto.AdminDashboardStatsResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminInterviewAnswerResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminInterviewResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginRequest;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminRegisterRequest;
import com.aiinterview.aiinterviewbackend.dto.AdminUserResponse;
import com.aiinterview.aiinterviewbackend.entity.Admin;
import com.aiinterview.aiinterviewbackend.service.AdminService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class AdminController {

    private final AdminService adminService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }


    // =========================================================
    // ADMIN REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid
            @RequestBody
            AdminRegisterRequest request
    ) {

        try {

            Admin admin =
                    adminService.register(request);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Admin registered successfully"
            );

            response.put(
                    "id",
                    admin.getId()
            );

            response.put(
                    "name",
                    admin.getName()
            );

            response.put(
                    "email",
                    admin.getEmail()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }


    // =========================================================
    // ADMIN LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid
            @RequestBody
            AdminLoginRequest request
    ) {

        try {

            AdminLoginResponse loginResponse =
                    adminService.login(request);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Admin login successful"
            );

            response.put(
                    "id",
                    loginResponse.getId()
            );

            response.put(
                    "name",
                    loginResponse.getName()
            );

            response.put(
                    "email",
                    loginResponse.getEmail()
            );

            // =================================================
            // JWT TOKEN
            // =================================================

            response.put(
                    "token",
                    loginResponse.getToken()
            );

            // Password is intentionally NOT returned.

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }
    }


    // =========================================================
    // DASHBOARD STATS
    // =========================================================

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {

        try {

            AdminDashboardStatsResponse stats =
                    adminService.getDashboardStats();

            return ResponseEntity.ok(stats);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================================
    // ALL REGISTERED USERS
    // =========================================================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {

        try {

            List<AdminUserResponse> users =
                    adminService.getAllUsers();

            return ResponseEntity.ok(users);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================================
    // ALL INTERVIEWS
    // =========================================================

    @GetMapping("/interviews")
    public ResponseEntity<?> getAllInterviews() {

        try {

            List<AdminInterviewResponse> interviews =
                    adminService.getAllInterviews();

            return ResponseEntity.ok(interviews);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================================
    // AI FEEDBACK FOR ONE INTERVIEW
    // =========================================================

    @GetMapping("/interviews/{interviewId}/feedback")
    public ResponseEntity<?> getInterviewFeedback(
            @PathVariable Long interviewId
    ) {

        try {

            List<AdminInterviewAnswerResponse> feedback =
                    adminService.getInterviewFeedback(
                            interviewId
                    );

            return ResponseEntity.ok(feedback);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================================
    // AVERAGE SCORE BY TOPIC
    // =========================================================

    @GetMapping("/analytics/topic")
    public ResponseEntity<?> getAverageScoreByTopic() {

        try {

            Map<String, Double> result =
                    adminService.getAverageScoreByTopic();

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }


    // =========================================================
    // AVERAGE SCORE BY DIFFICULTY
    // =========================================================

    @GetMapping("/analytics/difficulty")
    public ResponseEntity<?> getAverageScoreByDifficulty() {

        try {

            Map<String, Double> result =
                    adminService.getAverageScoreByDifficulty();

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {

            Map<String, String> error =
                    new HashMap<>();

            error.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(error);
        }
    }
}