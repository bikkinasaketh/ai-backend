package com.aiinterview.aiinterviewbackend.controller;

import com.aiinterview.aiinterviewbackend.dto.UserResponse;
import com.aiinterview.aiinterviewbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://ai-interviw.netlify.app",
        "https://ai-intervi.netlify.app"
})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getProfile(
            @PathVariable Long userId
    ) {

        UserResponse user =
                userService.getProfile(userId);

        return ResponseEntity.ok(user);
    }
}