package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.dto.UserResponse;
import com.aiinterview.aiinterviewbackend.entity.User;
import com.aiinterview.aiinterviewbackend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(
            @Valid @RequestBody User user
    ) {

        UserResponse savedUser =
                userService.signup(user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        UserResponse user =
                userService.login(
                        request.getEmail(),
                        request.getPassword()
                );

        return ResponseEntity.ok(user);
    }

    public static class LoginRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}