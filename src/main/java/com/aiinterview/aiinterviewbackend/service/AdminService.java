package com.aiinterview.aiinterviewbackend.service;

import com.aiinterview.aiinterviewbackend.dto.AdminDashboardStatsResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminInterviewAnswerResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminInterviewResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginRequest;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminRegisterRequest;
import com.aiinterview.aiinterviewbackend.dto.AdminUserResponse;

import com.aiinterview.aiinterviewbackend.entity.Admin;
import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.entity.InterviewAnswer;
import com.aiinterview.aiinterviewbackend.entity.User;

import com.aiinterview.aiinterviewbackend.repository.AdminRepository;
import com.aiinterview.aiinterviewbackend.repository.InterviewAnswerRepository;
import com.aiinterview.aiinterviewbackend.repository.InterviewRepository;
import com.aiinterview.aiinterviewbackend.repository.UserRepository;

import com.aiinterview.aiinterviewbackend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final UserRepository userRepository;

    private final InterviewRepository interviewRepository;

    private final InterviewAnswerRepository interviewAnswerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AdminService(
            AdminRepository adminRepository,
            UserRepository userRepository,
            InterviewRepository interviewRepository,
            InterviewAnswerRepository interviewAnswerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {

        this.adminRepository =
                adminRepository;

        this.userRepository =
                userRepository;

        this.interviewRepository =
                interviewRepository;

        this.interviewAnswerRepository =
                interviewAnswerRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;
    }


    // =========================================================
    // ADMIN REGISTER
    // =========================================================

    public Admin register(
            AdminRegisterRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        // -----------------------------------------------------
        // CHECK DUPLICATE EMAIL
        // -----------------------------------------------------

        if (
                adminRepository
                        .existsByEmail(email)
        ) {

            throw new RuntimeException(
                    "Admin with this email already exists"
            );
        }


        // -----------------------------------------------------
        // CREATE ADMIN
        // -----------------------------------------------------

        Admin admin =
                new Admin();


        admin.setName(
                request.getName()
                        .trim()
        );


        admin.setEmail(email);


        // -----------------------------------------------------
        // ENCODE PASSWORD
        // -----------------------------------------------------

        admin.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );


        // -----------------------------------------------------
        // SAVE ADMIN
        // -----------------------------------------------------

        return adminRepository.save(
                admin
        );
    }


    // =========================================================
    // ADMIN LOGIN
    // =========================================================

    public AdminLoginResponse login(
            AdminLoginRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        // -----------------------------------------------------
        // FIND ADMIN
        // -----------------------------------------------------

        Admin admin =
                adminRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Invalid email or password"
                                        )
                        );


        // -----------------------------------------------------
        // CHECK PASSWORD
        // -----------------------------------------------------

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        admin.getPassword()
                );


        if (!passwordMatches) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // =====================================================
        // GENERATE ADMIN JWT
        // =====================================================

        String token =
                jwtService.generateToken(
                        admin.getEmail(),
                        "ADMIN"
                );


        // =====================================================
        // RETURN ADMIN + JWT
        // =====================================================

        return new AdminLoginResponse(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                token
        );
    }


    // =========================================================
    // DASHBOARD STATS
    // =========================================================

    public AdminDashboardStatsResponse
    getDashboardStats() {

        long totalUsers =
                userRepository.count();


        long totalInterviews =
                interviewRepository.count();


        List<Interview> interviews =
                interviewRepository.findAll();


        double averageScore =
                0.0;


        int highestScore =
                0;


        if (!interviews.isEmpty()) {

            int totalScore =
                    0;


            for (
                    Interview interview
                    : interviews
            ) {

                int score =
                        interview.getScore();


                totalScore += score;


                if (
                        score > highestScore
                ) {

                    highestScore =
                            score;
                }
            }


            averageScore =
                    Math.round(
                            (
                                    (double) totalScore
                                            / interviews.size()
                            ) * 100.0
                    ) / 100.0;
        }


        return new AdminDashboardStatsResponse(
                totalUsers,
                totalInterviews,
                averageScore,
                highestScore
        );
    }


    // =========================================================
    // ALL REGISTERED USERS
    // =========================================================

    public List<AdminUserResponse>
    getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(
                        user ->
                                new AdminUserResponse(
                                        user.getId(),
                                        user.getName(),
                                        user.getEmail(),
                                        user.getPhone()
                                )
                )
                .toList();
    }


    // =========================================================
    // ALL INTERVIEWS
    // =========================================================

    public List<AdminInterviewResponse>
    getAllInterviews() {

        return interviewRepository
                .findAll()
                .stream()
                .map(
                        this::convertInterview
                )
                .toList();
    }


    // =========================================================
    // INTERVIEW → DTO
    // =========================================================

    private AdminInterviewResponse
    convertInterview(
            Interview interview
    ) {

        User user =
                interview.getUser();


        String userName =
                "";


        String userEmail =
                "";


        if (user != null) {

            userName =
                    user.getName();

            userEmail =
                    user.getEmail();
        }


        return new AdminInterviewResponse(
                interview.getId(),
                userName,
                userEmail,
                interview.getTopic(),
                interview.getDifficulty(),
                interview.getScore(),
                interview.getTotalQuestions(),
                interview.getCompletedAt()
        );
    }


    // =========================================================
    // AVERAGE SCORE BY TOPIC
    // =========================================================

    public Map<String, Double>
    getAverageScoreByTopic() {

        List<Interview> interviews =
                interviewRepository.findAll();


        Map<String, List<Integer>>
                scoresByTopic =
                new HashMap<>();


        for (
                Interview interview
                : interviews
        ) {

            String topic =
                    interview.getTopic();


            if (
                    topic == null ||
                    topic.isBlank()
            ) {

                topic =
                        "Unknown";
            }


            scoresByTopic
                    .computeIfAbsent(
                            topic,
                            key ->
                                    new ArrayList<>()
                    )
                    .add(
                            interview.getScore()
                    );
        }


        Map<String, Double> result =
                new HashMap<>();


        for (
                Map.Entry<
                        String,
                        List<Integer>
                        > entry
                : scoresByTopic.entrySet()
        ) {

            double average =
                    entry.getValue()
                            .stream()
                            .mapToInt(
                                    Integer::intValue
                            )
                            .average()
                            .orElse(0.0);


            result.put(
                    entry.getKey(),
                    Math.round(
                            average * 100.0
                    ) / 100.0
            );
        }


        return result;
    }


    // =========================================================
    // AVERAGE SCORE BY DIFFICULTY
    // =========================================================

    public Map<String, Double>
    getAverageScoreByDifficulty() {

        List<Interview> interviews =
                interviewRepository.findAll();


        Map<String, List<Integer>>
                scoresByDifficulty =
                new HashMap<>();


        for (
                Interview interview
                : interviews
        ) {

            String difficulty =
                    interview.getDifficulty();


            if (
                    difficulty == null ||
                    difficulty.isBlank()
            ) {

                difficulty =
                        "Unknown";
            }


            scoresByDifficulty
                    .computeIfAbsent(
                            difficulty,
                            key ->
                                    new ArrayList<>()
                    )
                    .add(
                            interview.getScore()
                    );
        }


        Map<String, Double> result =
                new HashMap<>();


        for (
                Map.Entry<
                        String,
                        List<Integer>
                        > entry
                : scoresByDifficulty.entrySet()
        ) {

            double average =
                    entry.getValue()
                            .stream()
                            .mapToInt(
                                    Integer::intValue
                            )
                            .average()
                            .orElse(0.0);


            result.put(
                    entry.getKey(),
                    Math.round(
                            average * 100.0
                    ) / 100.0
            );
        }


        return result;
    }


    // =========================================================
    // AI FEEDBACK FOR ONE INTERVIEW
    // =========================================================

    public List<AdminInterviewAnswerResponse>
    getInterviewFeedback(
            Long interviewId
    ) {

        List<InterviewAnswer> answers =
                interviewAnswerRepository
                        .findByInterviewId(
                                interviewId
                        );


        return answers
                .stream()
                .map(
                        answer ->
                                new AdminInterviewAnswerResponse(
                                        answer.getId(),
                                        answer.getQuestion(),
                                        answer.getAnswer(),
                                        answer.getAiScore(),
                                        answer.getFeedback(),
                                        answer.getCorrectPoints(),
                                        answer.getImprovements(),
                                        answer.getLearnNext(),
                                        answer.getBetterAnswer()
                                )
                )
                .toList();
    }
}