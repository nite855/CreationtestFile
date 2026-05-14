package com.creationtime.service;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.user.User;
import com.creationtime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(String name, String email, String loginId, String password, String interestField, String techStack, String desiredRole) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new DomainException("loginId already exists.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DomainException("email already exists.");
        }
        return userRepository.save(new User(name, email, loginId, password, interestField, techStack, desiredRole));
    }

    public User authenticateUser(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new DomainException("invalid login id or password."));
        if (!user.matchesPassword(password)) {
            throw new DomainException("invalid login id or password.");
        }
        user.assertCanUseService();
        return user;
    }
}
