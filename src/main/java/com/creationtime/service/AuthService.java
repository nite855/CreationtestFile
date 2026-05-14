package com.creationtime.service;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.user.CompetencyInfo;
import com.creationtime.domain.user.ProfileInfo;
import com.creationtime.domain.user.User;
import com.creationtime.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(ProfileInfo profileInfo, CompetencyInfo competencyInfo) {
        if (userRepository.findByProfileInfoLoginId(profileInfo.loginId()).isPresent()) {
            throw new DomainException("loginId already exists.");
        }
        if (userRepository.existsByProfileInfoEmail(profileInfo.email())) {
            throw new DomainException("email already exists.");
        }
        return userRepository.save(new User(profileInfo, competencyInfo));
    }

    public User authenticateUser(String loginId, String password) {
        User user = userRepository.findByProfileInfoLoginId(loginId)
                .orElseThrow(() -> new DomainException("invalid login id or password."));
        if (!user.matchesPassword(password)) {
            throw new DomainException("invalid login id or password.");
        }
        user.assertCanUseService();
        return user;
    }
}
