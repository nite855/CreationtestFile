package com.creationtime.domain.collaboration;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.user.User;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    protected Post() {
    }

    public Post(String title, String content, PostCategory category, User author, Team team) {
        team.assertMember(author);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        this.category = Objects.requireNonNull(category);
        this.author = Objects.requireNonNull(author);
        this.team = Objects.requireNonNull(team);
        this.createdAt = LocalDateTime.now();
    }

    public void modify(User editor, String title, String content, PostCategory category) {
        if (!isWrittenBy(editor)) {
            throw new DomainException("only author can modify post.");
        }
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        this.category = Objects.requireNonNull(category);
    }

    public Comment writeComment(User author, String content) {
        team.assertMember(author);
        Comment comment = new Comment(content, author, this);
        comments.add(comment);
        return comment;
    }

    public void deleteComment(User requester, Comment comment) {
        if (!comment.isWrittenBy(requester)) {
            throw new DomainException("only author can delete comment.");
        }
        comments.remove(comment);
    }

    public boolean isWrittenBy(User user) {
        return author == user;
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

    public PostCategory category() {
        return category;
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

    public List<Comment> comments() {
        return Collections.unmodifiableList(comments);
    }
}
