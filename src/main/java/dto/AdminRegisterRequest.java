package com.aiinterview.aiinterviewbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminRegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
    )
    private String password;

    public AdminRegisterRequest() {
    }

    // =========================================================
    // NAME
    // =========================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // =========================================================
    // EMAIL
    // =========================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================================================
    // PASSWORD
    // =========================================================

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}