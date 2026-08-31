package com.aiinterview.aiinterviewbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService
    ) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // =====================================================
        // GET AUTHORIZATION HEADER
        // =====================================================

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println(
                "JWT Authorization Header: "
                        + authorizationHeader
        );

        // =====================================================
        // CHECK BEARER TOKEN
        // =====================================================

        if (
                authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        // =====================================================
        // EXTRACT TOKEN
        // =====================================================

        String token =
                authorizationHeader.substring(7);

        try {

            // =================================================
            // EXTRACT EMAIL
            // =================================================

            String email =
                    jwtService.extractEmail(token);

            // =================================================
            // EXTRACT ROLE
            // =================================================

            String role =
                    jwtService.extractRole(token);

            System.out.println(
                    "JWT Email: " + email
            );

            System.out.println(
                    "JWT Role: " + role
            );

            // =================================================
            // VALIDATE
            // =================================================

            if (
                    email != null &&
                    role != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null
            ) {

                boolean valid =
                        jwtService.isTokenValid(
                                token,
                                email
                        );

                System.out.println(
                        "JWT Valid: " + valid
                );

                if (valid) {

                    // =========================================
                    // SPRING ROLE
                    // =========================================

                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role.toUpperCase()
                            );

                    // =========================================
                    // AUTHENTICATION
                    // =========================================

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(
                                            authority
                                    )
                            );

                    // =========================================
                    // SECURITY CONTEXT
                    // =========================================

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    System.out.println(
                            "Authenticated: "
                                    + email
                                    + " with "
                                    + authority.getAuthority()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT Authentication Error: "
                            + e.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }

        // =====================================================
        // CONTINUE
        // =====================================================

        filterChain.doFilter(
                request,
                response
        );
    }
}