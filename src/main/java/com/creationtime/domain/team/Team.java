package com.creationtime.domain.team;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.recruitment.Application;
import com.creationtime.domain.recruitment.ApplicationStatus;
import com.creationtime.domain.user.User;
import com.creationtime.domain.user.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamCategory category;

    private int maxMemberCount;
    private String requiredTechStack;
    private String description;
    private LocalDateTime createdAt;
    private boolean deleted;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TeamMember> members = new ArrayList<>();

    protected Team() {
    }

    public Team(String name, TeamCategory category, int maxMemberCount, String requiredTechStack, String description, User leader) {
        this.name = Required.text(name, "team name");
        this.category = Objects.requireNonNull(category);
        this.maxMemberCount = Required.positive(maxMemberCount, "maxMemberCount");
        this.requiredTechStack = Required.text(requiredTechStack, "requiredTechStack");
        this.description = Required.text(description, "description");
        this.createdAt = LocalDateTime.now();
        addInitialLeader(leader);
    }

    private void addInitialLeader(User leader) {
        leader.assertCanUseService();
        leader.changeRole(UserRole.TEAM_LEADER);
        members.add(new TeamMember(leader, this, "leader"));
    }

    public Application apply(User applicant, String message) {
        assertUsable();
        applicant.assertCanUseService();
        if (hasMember(applicant)) {
            throw new DomainException("already joined team.");
        }
        if (isFull()) {
            throw new DomainException("team is full.");
        }
        return new Application(applicant, this, message);
    }

    public TeamMember approveApplication(Application application, User reviewer, String teamRole) {
        assertLeader(reviewer);
        if (!application.isForTeam(this)) {
            throw new DomainException("application targets another team.");
        }
        if (application.status() != ApplicationStatus.PENDING) {
            throw new DomainException("application is already reviewed.");
        }
        if (hasMember(application.applicant())) {
            throw new DomainException("applicant is already a team member.");
        }
        if (isFull()) {
            throw new DomainException("team is full.");
        }
        application.approve();
        application.applicant().changeRole(UserRole.TEAM_MEMBER);
        TeamMember member = new TeamMember(application.applicant(), this, teamRole);
        members.add(member);
        return member;
    }

    public void rejectApplication(Application application, User reviewer) {
        assertLeader(reviewer);
        if (!application.isForTeam(this)) {
            throw new DomainException("application targets another team.");
        }
        application.reject();
    }

    public void changeMemberTeamRole(User leader, User targetUser, String teamRole) {
        assertLeader(leader);
        findMember(targetUser).changeTeamRole(teamRole);
    }

    public void dismissMember(User leader, User targetUser, DismissalReason reason) {
        assertLeader(leader);
        TeamMember target = findMember(targetUser);
        if (target.user() == leader) {
            throw new DomainException("cannot dismiss the leader.");
        }
        members.remove(target);
        targetUser.increasePenalty(reason.penaltyScore());
    }

    public void delete(User leader, String confirmTeamName) {
        assertLeader(leader);
        if (!name.equals(confirmTeamName)) {
            throw new DomainException("team name confirmation does not match.");
        }
        deleted = true;
        members.clear();
    }

    public void assertLeader(User user) {
        assertUsable();
        if (user.role() != UserRole.TEAM_LEADER || members.stream().noneMatch(member -> member.user() == user)) {
            throw new DomainException("team leader permission is required.");
        }
    }

    public void assertMember(User user) {
        assertUsable();
        if (!hasMember(user)) {
            throw new DomainException("team member permission is required.");
        }
    }

    public boolean hasMember(User user) {
        return members.stream().anyMatch(member -> member.user() == user);
    }

    public boolean isFull() {
        return members.size() >= maxMemberCount;
    }

    public int memberCount() {
        return members.size();
    }

    public void assertUsable() {
        if (deleted) {
            throw new DomainException("deleted team cannot be used.");
        }
    }

    private TeamMember findMember(User user) {
        return members.stream()
                .filter(member -> member.user() == user)
                .findFirst()
                .orElseThrow(() -> new DomainException("team member not found."));
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

    public String name() {
        return name;
    }

    public TeamCategory category() {
        return category;
    }

    public int maxMemberCount() {
        return maxMemberCount;
    }

    public String requiredTechStack() {
        return requiredTechStack;
    }

    public String description() {
        return description;
    }

    public List<TeamMember> members() {
        return Collections.unmodifiableList(members);
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public boolean deleted() {
        return deleted;
    }
}
