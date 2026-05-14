package com.creationtime.domain.recruitment;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.user.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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

    @Embedded
    private RecruitInfo recruitInfo;

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

    public RecruitPost(String title, String content, RecruitInfo recruitInfo, User author, Team team) {
        team.assertLeader(author);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        this.recruitInfo = Objects.requireNonNull(recruitInfo);
        this.author = Objects.requireNonNull(author);
        this.team = Objects.requireNonNull(team);
        this.createdAt = LocalDateTime.now();
    }

    public void modify(User editor, String title, String content, RecruitInfo recruitInfo) {
        team.assertLeader(editor);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        this.recruitInfo = Objects.requireNonNull(recruitInfo);
    }

    public void close(User requester) {
        team.assertLeader(requester);
        closed = true;
    }

    public boolean isRecruiting() {
        return !closed && recruitInfo.deadline().isAfter(LocalDateTime.now());
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

    public RecruitInfo recruitInfo() {
        return recruitInfo;
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
