package com.aiinterview.aiinterviewbackend.dto;

public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String token;

    public UserResponse() {
    }

    public UserResponse(
            Long id,
            String name,
            String email,
            String phone
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.token = null;
    }

    public UserResponse(
            Long id,
            String name,
            String email,
            String phone,
            String token
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.token = token;
    }

    // =========================================================
    // ID
    // =========================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    // PHONE
    // =========================================================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // =========================================================
    // TOKEN
    // =========================================================

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}