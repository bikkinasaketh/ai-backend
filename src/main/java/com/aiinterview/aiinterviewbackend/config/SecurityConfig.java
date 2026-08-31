package com.aiinterview.aiinterviewbackend.config;

import com.aiinterview.aiinterviewbackend.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // DISABLE CSRF
                // =================================================

                .csrf(csrf ->
                        csrf.disable()
                )

                // =================================================
                // ENABLE CORS
                // =================================================

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                // =================================================
                // STATELESS SESSION
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // SWAGGER
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // =================================================
                        // HEALTH CHECK
                        // =================================================

                        .requestMatchers(
                                "/healthz"
                        )
                        .permitAll()

                        // =================================================
                        // USER LOGIN + SIGNUP
                        // =================================================

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/signup"
                        )
                        .permitAll()

                        // =================================================
                        // ADMIN LOGIN + REGISTER
                        // =================================================

                        .requestMatchers(
                                "/api/admin/login",
                                "/api/admin/register"
                        )
                        .permitAll()

                        // =================================================
                        // ADMIN APIs
                        // =================================================

                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // USER APIs
                        // =================================================

                        .requestMatchers(
                                "/api/users/**"
                        )
                        .hasRole("USER")

                        // =================================================
                        // INTERVIEW APIs
                        // =================================================

                        .requestMatchers(
                                "/api/interviews/**"
                        )
                        .authenticated()

                        // =================================================
                        // AI APIs
                        // =================================================

                        .requestMatchers(
                                "/api/ai/**"
                        )
                        .authenticated()

                        // =================================================
                        // CORS PREFLIGHT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // =================================================
                        // OTHER REQUESTS
                        // =================================================

                        .anyRequest()
                        .authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // =========================================================
        // ALLOWED FRONTENDS
        // =========================================================

        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:5173",
                        "https://aiinte.netlify.app"
                )
        );

        // =========================================================
        // ALLOWED METHODS
        // =========================================================

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        // =========================================================
        // ALLOWED HEADERS
        // =========================================================

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        // =========================================================
        // EXPOSED HEADERS
        // =========================================================

        configuration.setExposedHeaders(
                Arrays.asList("*")
        );

        // =========================================================
        // CREDENTIALS
        // =========================================================

        configuration.setAllowCredentials(true);

        // =========================================================
        // REGISTER CORS CONFIGURATION
        // =========================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
