package com.creationtime.domain.user;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;


@Entity
@Table(name = "users")
public class User {
    public static final int RESTRICTED_PENALTY_SCORE = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String loginId;
    private String password;
    private String interestField;
    private String techStack;
    private String desiredRole;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private int penaltyScore;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    protected User() {
    }

    public User(String name, String email, String loginId, String password, String interestField, String techStack, String desiredRole) {
        this.name = Required.text(name, "name");
        this.email = Required.text(email, "email");
        this.loginId = Required.text(loginId, "loginId");
        this.password = Required.text(password, "password");
        this.interestField = Required.text(interestField, "interestField");
        this.techStack = Required.text(techStack, "techStack");
        this.desiredRole = Required.text(desiredRole, "desiredRole");
        this.role = UserRole.REGISTERED_USER;
        this.status = AccountStatus.ACTIVE;
    }

    public void changeRole(UserRole role) {
        this.role = Objects.requireNonNull(role);
    }

    public void updateProfile(String name, String email, String interestField, String techStack, String desiredRole) {
        this.name = Required.text(name, "name");
        this.email = Required.text(email, "email");
        this.interestField = Required.text(interestField, "interestField");
        this.techStack = Required.text(techStack, "techStack");
        this.desiredRole = Required.text(desiredRole, "desiredRole");
    }

    public void increasePenalty(int score) {
        if (score <= 0) {
            throw new DomainException("penalty score must be positive.");
        }
        penaltyScore += score;
        if (penaltyScore >= RESTRICTED_PENALTY_SCORE) {
            status = AccountStatus.RESTRICTED;
        }
    }

    public void decreasePenalty(int score) {
        if (score <= 0) {
            throw new DomainException("penalty score must be positive.");
        }
        penaltyScore = Math.max(0, penaltyScore - score);
        if (penaltyScore < RESTRICTED_PENALTY_SCORE) {
            status = AccountStatus.ACTIVE;
        }
    }

    public void assertCanUseService() {
        if (status == AccountStatus.RESTRICTED) {
            throw new DomainException("restricted users cannot use this service.");
        }
    }

    public boolean matchesPassword(String password) {
        return this.password.equals(password);
    }

    public Long id() {
        return id;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new DomainException("id is already assigned.");
        }
        this.id = Objects.requireNonNull(id);
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String loginId() {
        return loginId;
    }

    public String interestField() {
        return interestField;
    }

    public String techStack() {
        return techStack;
    }

    public String desiredRole() {
        return desiredRole;
    }

    public int penaltyScore() {
        return penaltyScore;
    }

    public AccountStatus status() {
        return status;
    }

    public UserRole role() {
        return role;
    }
}
