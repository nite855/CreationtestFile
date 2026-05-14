package com.creationtime.repository;

import com.creationtime.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByInfoName(String name);
}
