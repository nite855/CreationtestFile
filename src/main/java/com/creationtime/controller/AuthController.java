package com.creationtime.controller;

import com.creationtime.domain.user.CompetencyInfo;
import com.creationtime.domain.user.ProfileInfo;
import com.creationtime.domain.user.User;
import com.creationtime.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public User signUp(@RequestBody SignUpRequest request) {
        return authService.registerUser(request.profileInfo(), request.competencyInfo());
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return authService.authenticateUser(request.loginId(), request.password());
    }

    public record SignUpRequest(ProfileInfo profileInfo, CompetencyInfo competencyInfo) {
    }

    public record LoginRequest(String loginId, String password) {
    }
}
