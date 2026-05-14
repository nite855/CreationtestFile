package com.creationtime.controller;

import com.creationtime.domain.user.User;
import com.creationtime.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public User signUp(@RequestBody SignUpRequest request) {
        return authService.registerUser(
                request.name(),
                request.email(),
                request.loginId(),
                request.password(),
                request.interestField(),
                request.techStack(),
                request.desiredRole()
        );
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return authService.authenticateUser(request.loginId(), request.password());
    }

    public record SignUpRequest(
            String name,
            String email,
            String loginId,
            String password,
            String interestField,
            String techStack,
            String desiredRole
    ) {
    }

    public record LoginRequest(String loginId, String password) {
    }
}
