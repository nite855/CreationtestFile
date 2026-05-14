package com.creationtime.domain.recruitment;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team targetTeam;

    private String message;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    protected Application() {
    }

    public Application(User applicant, Team targetTeam, String message) {
        this.applicant = Objects.requireNonNull(applicant);
        this.targetTeam = Objects.requireNonNull(targetTeam);
        this.message = Required.text(message, "application message");
        this.status = ApplicationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() {
        assertPending();
        status = ApplicationStatus.APPROVED;
    }

    public void reject() {
        assertPending();
        status = ApplicationStatus.REJECTED;
    }

    public boolean isForTeam(Team team) {
        return targetTeam == team || Objects.equals(targetTeam.id(), team.id());
    }

    private void assertPending() {
        if (status != ApplicationStatus.PENDING) {
            throw new DomainException("application is already reviewed.");
        }
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new DomainException("id is already assigned.");
        }
        this.id = Objects.requireNonNull(id);
    }

    public Long id() {
        return id;
    }

    public User applicant() {
        return applicant;
    }

    public Team targetTeam() {
        return targetTeam;
    }

    public String message() {
        return message;
    }

    public ApplicationStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
