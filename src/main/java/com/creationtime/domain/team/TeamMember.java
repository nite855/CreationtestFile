package com.creationtime.domain.team;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.user.User;
import jakarta.persistence.Entity;
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
@Table(name = "team_members")
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    private String teamRole;
    private LocalDateTime joinedAt;

    protected TeamMember() {
    }

    public TeamMember(User user, Team team, String teamRole) {
        this.user = Objects.requireNonNull(user);
        this.team = Objects.requireNonNull(team);
        this.teamRole = Required.text(teamRole, "teamRole");
        this.joinedAt = LocalDateTime.now();
    }

    public void changeTeamRole(String teamRole) {
        this.teamRole = Required.text(teamRole, "teamRole");
    }

    public boolean belongsTo(Team team) {
        return this.team == team || Objects.equals(this.team.id(), team.id());
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

    public User user() {
        return user;
    }

    public Team team() {
        return team;
    }

    public String teamRole() {
        return teamRole;
    }

    public LocalDateTime joinedAt() {
        return joinedAt;
    }
}
