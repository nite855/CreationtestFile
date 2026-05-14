package com.creationtime.domain.recruitment;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.team.TeamCategory;
import com.creationtime.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "recruit_posts")
public class RecruitPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private TeamCategory category;

    private String recruitField;
    private String techStack;
    private String requiredRole;
    private int recruitCount;
    private String progressMethod;
    private String contactLink;
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    private boolean closed;
    private LocalDateTime createdAt;

    protected RecruitPost() {
    }

    public RecruitPost(
            String title,
            String content,
            TeamCategory category,
            String recruitField,
            String techStack,
            String requiredRole,
            int recruitCount,
            String progressMethod,
            String contactLink,
            LocalDateTime deadline,
            User author,
            Team team
    ) {
        team.assertLeader(author);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        changeRecruitFields(category, recruitField, techStack, requiredRole, recruitCount, progressMethod, contactLink, deadline);
        this.author = Objects.requireNonNull(author);
        this.team = Objects.requireNonNull(team);
        this.createdAt = LocalDateTime.now();
    }

    public void modify(
            User editor,
            String title,
            String content,
            TeamCategory category,
            String recruitField,
            String techStack,
            String requiredRole,
            int recruitCount,
            String progressMethod,
            String contactLink,
            LocalDateTime deadline
    ) {
        team.assertLeader(editor);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        changeRecruitFields(category, recruitField, techStack, requiredRole, recruitCount, progressMethod, contactLink, deadline);
    }

    private void changeRecruitFields(
            TeamCategory category,
            String recruitField,
            String techStack,
            String requiredRole,
            int recruitCount,
            String progressMethod,
            String contactLink,
            LocalDateTime deadline
    ) {
        this.category = Objects.requireNonNull(category);
        this.recruitField = Required.text(recruitField, "recruitField");
        this.techStack = Required.text(techStack, "techStack");
        this.requiredRole = Required.text(requiredRole, "requiredRole");
        this.recruitCount = Required.positive(recruitCount, "recruitCount");
        this.progressMethod = Required.text(progressMethod, "progressMethod");
        this.contactLink = Required.text(contactLink, "contactLink");
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new DomainException("deadline must be in the future.");
        }
        this.deadline = deadline;
    }

    public void close(User requester) {
        team.assertLeader(requester);
        closed = true;
    }

    public boolean isRecruiting() {
        return !closed && deadline.isAfter(LocalDateTime.now());
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

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public TeamCategory category() {
        return category;
    }

    public String recruitField() {
        return recruitField;
    }

    public String techStack() {
        return techStack;
    }

    public String requiredRole() {
        return requiredRole;
    }

    public int recruitCount() {
        return recruitCount;
    }

    public String progressMethod() {
        return progressMethod;
    }

    public String contactLink() {
        return contactLink;
    }

    public LocalDateTime deadline() {
        return deadline;
    }

    public User author() {
        return author;
    }

    public Team team() {
        return team;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
