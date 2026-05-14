package com.creationtime.domain.collaboration;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.common.Required;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.user.User;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @ElementCollection
    private List<VoteOption> options;

    @Transient
    private final Map<User, VoteOption> ballots = new LinkedHashMap<>();
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    private boolean closed;

    protected Vote() {
    }

    public Vote(String title, String description, List<String> optionTexts, LocalDateTime deadline, User creator, Team team) {
        team.assertLeader(creator);
        this.title = Required.text(title, "title");
        this.description = Required.text(description, "description");
        if (optionTexts == null || optionTexts.size() < 2) {
            throw new DomainException("vote needs at least two options.");
        }
        this.options = IntStream.range(0, optionTexts.size())
                .mapToObj(index -> new VoteOption(index + 1L, optionTexts.get(index)))
                .toList();
        if (deadline == null || deadline.isBefore(LocalDateTime.now())) {
            throw new DomainException("deadline must be in the future.");
        }
        this.deadline = deadline;
        this.team = Objects.requireNonNull(team);
    }

    public void participate(User voter, long optionId) {
        team.assertMember(voter);
        if (closed || isExpired()) {
            throw new DomainException("vote is closed.");
        }
        VoteOption selected = options.stream()
                .filter(option -> option.id() == optionId)
                .findFirst()
                .orElseThrow(() -> new DomainException("vote option not found."));
        ballots.put(voter, selected);
    }

    public void close(User requester) {
        team.assertLeader(requester);
        closed = true;
    }

    public Map<String, Long> result() {
        return options.stream()
                .collect(Collectors.toMap(
                        VoteOption::text,
                        option -> ballots.values().stream().filter(option::equals).count(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    public boolean isParticipated(User user) {
        return ballots.containsKey(user);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(deadline);
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

    public List<VoteOption> options() {
        return Collections.unmodifiableList(options);
    }

    public Map<User, VoteOption> ballots() {
        return Collections.unmodifiableMap(ballots);
    }
}
