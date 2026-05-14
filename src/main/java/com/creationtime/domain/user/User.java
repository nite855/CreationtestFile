package com.creationtime.domain.user;

import com.creationtime.domain.common.DomainException;
import jakarta.persistence.Embedded;
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

    @Embedded
    private ProfileInfo profileInfo;

    @Embedded
    private CompetencyInfo competencyInfo;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private int penaltyScore;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    protected User() {
    }

    public User(ProfileInfo profileInfo, CompetencyInfo competencyInfo) {
        this.profileInfo = Objects.requireNonNull(profileInfo);
        this.competencyInfo = Objects.requireNonNull(competencyInfo);
        this.role = UserRole.REGISTERED_USER;
        this.status = AccountStatus.ACTIVE;
    }

    public void changeRole(UserRole role) {
        this.role = Objects.requireNonNull(role);
    }

    public void updateProfile(ProfileInfo profileInfo, CompetencyInfo competencyInfo) {
        this.profileInfo = Objects.requireNonNull(profileInfo);
        this.competencyInfo = Objects.requireNonNull(competencyInfo);
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
        return profileInfo.password().equals(password);
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

    public ProfileInfo profileInfo() {
        return profileInfo;
    }

    public CompetencyInfo competencyInfo() {
        return competencyInfo;
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
