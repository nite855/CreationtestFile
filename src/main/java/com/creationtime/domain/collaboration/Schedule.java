package com.creationtime.domain.collaboration;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
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
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Lob
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    protected Schedule() {
    }

    public Schedule(String title, String content, LocalDateTime startDateTime, LocalDateTime endDateTime, User creator, Team team) {
        team.assertLeader(creator);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
        validatePeriod(startDateTime, endDateTime);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.team = Objects.requireNonNull(team);
    }

    public void modify(User editor, String title, String content) {
        team.assertLeader(editor);
        this.title = Required.text(title, "title");
        this.content = Required.text(content, "content");
    }

    public void changePeriod(User editor, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        team.assertLeader(editor);
        validatePeriod(startDateTime, endDateTime);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public boolean isActiveAt(LocalDateTime dateTime) {
        return !dateTime.isBefore(startDateTime) && !dateTime.isAfter(endDateTime);
    }

    private void validatePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null || endDateTime.isBefore(startDateTime)) {
            throw new DomainException("schedule period is invalid.");
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
}
