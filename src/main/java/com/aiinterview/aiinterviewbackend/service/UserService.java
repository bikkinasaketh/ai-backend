package com.aiinterview.aiinterviewbackend.service;

import com.aiinterview.aiinterviewbackend.dto.UserResponse;
import com.aiinterview.aiinterviewbackend.entity.User;
import com.aiinterview.aiinterviewbackend.repository.UserRepository;
import com.aiinterview.aiinterviewbackend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;

        this.jwtService = jwtService;
    }


    // =========================================================
    // USER SIGNUP
    // =========================================================

    public UserResponse signup(User user) {

        // -----------------------------------------------------
        // CHECK EMAIL
        // -----------------------------------------------------

        if (
                userRepository
                        .findByEmail(user.getEmail())
                        .isPresent()
        ) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        // -----------------------------------------------------
        // ENCODE PASSWORD
        // -----------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );


        // -----------------------------------------------------
        // SAVE USER
        // -----------------------------------------------------

        User savedUser =
                userRepository.save(user);


        // -----------------------------------------------------
        // RETURN USER RESPONSE
        // -----------------------------------------------------

        return convertToResponse(savedUser);
    }


    // =========================================================
    // USER LOGIN
    // =========================================================

    public UserResponse login(
            String email,
            String password
    ) {

        // -----------------------------------------------------
        // FIND USER
        // -----------------------------------------------------

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );


        // -----------------------------------------------------
        // CHECK PASSWORD
        // -----------------------------------------------------

        if (
                !passwordEncoder.matches(
                        password,
                        user.getPassword()
                )
        ) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // -----------------------------------------------------
        // GENERATE JWT
        // -----------------------------------------------------

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );


        // -----------------------------------------------------
        // RETURN USER + JWT
        // -----------------------------------------------------

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                token
        );
    }


    // =========================================================
    // GET USER PROFILE
    // =========================================================

    public UserResponse getProfile(
            Long userId
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return convertToResponse(user);
    }


    // =========================================================
    // CONVERT USER TO RESPONSE
    // =========================================================

    private UserResponse convertToResponse(
            User user
    ) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}