package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.dto.UserResponse;
import com.aiinterview.aiinterviewbackend.entity.User;
import com.aiinterview.aiinterviewbackend.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthControllerIntegrationTest {

    // =========================================================
    // SIGNUP
    // =========================================================

    @Test
    void signupShouldReturnUserResponse() {

        UserService userService = mock(UserService.class);

        AuthController controller =
                new AuthController(userService);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Test User",
                        "test@example.com",
                        "9876543210"
                );

        when(userService.signup(any(User.class)))
                .thenReturn(response);

        User user = new User();

        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("Password@123");
        user.setPhone("9876543210");

        ResponseEntity<UserResponse> result =
                controller.signup(user);

        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());

        assertEquals(
                "test@example.com",
                result.getBody().getEmail()
        );

        verify(userService).signup(any(User.class));
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Test
    void loginShouldReturnUserResponse() {

        UserService userService = mock(UserService.class);

        AuthController controller =
                new AuthController(userService);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Test User",
                        "test@example.com",
                        "9876543210"
                );

        when(
                userService.login(
                        eq("test@example.com"),
                        eq("Password@123")
                )
        )
        .thenReturn(response);

        AuthController.LoginRequest request =
                new AuthController.LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("Password@123");

        ResponseEntity<UserResponse> result =
                controller.login(request);

        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());

        assertEquals(
                "test@example.com",
                result.getBody().getEmail()
        );

        verify(userService).login(
                "test@example.com",
                "Password@123"
        );
    }


    // =========================================================
    // LOGIN SERVICE FAILURE
    // =========================================================

    @Test
    void loginShouldCallUserServiceWithCorrectCredentials() {

        UserService userService = mock(UserService.class);

        AuthController controller =
                new AuthController(userService);

        UserResponse response =
                new UserResponse(
                        1L,
                        "Test User",
                        "test@example.com",
                        "9876543210"
                );

        when(
                userService.login(
                        "test@example.com",
                        "Password@123"
                )
        )
        .thenReturn(response);

        AuthController.LoginRequest request =
                new AuthController.LoginRequest();

        request.setEmail("test@example.com");
        request.setPassword("Password@123");

        controller.login(request);

        verify(userService, times(1))
                .login(
                        "test@example.com",
                        "Password@123"
                );
    }


    // =========================================================
    // SIGNUP SERVICE FAILURE
    // =========================================================

    @Test
    void signupShouldCallUserService() {

        UserService userService = mock(UserService.class);

        AuthController controller =
                new AuthController(userService);

        User user = new User();

        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("Password@123");
        user.setPhone("9876543210");

        UserResponse response =
                new UserResponse(
                        1L,
                        "Test User",
                        "test@example.com",
                        "9876543210"
                );

        when(userService.signup(any(User.class)))
                .thenReturn(response);

        controller.signup(user);

        verify(userService, times(1))
                .signup(user);
    }
}