package com.creationtime.domain.collaboration;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.user.User;
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
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime createdAt;

    protected Comment() {
    }

    public Comment(String content, User author, Post post) {
        this.content = Required.text(content, "content");
        this.author = Objects.requireNonNull(author);
        this.post = Objects.requireNonNull(post);
        this.createdAt = LocalDateTime.now();
    }

    public void modify(User editor, String content) {
        if (!isWrittenBy(editor)) {
            throw new DomainException("only author can modify comment.");
        }
        this.content = Required.text(content, "content");
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

    public String content() {
        return content;
    }

    public User author() {
        return author;
    }

    public Post post() {
        return post;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
