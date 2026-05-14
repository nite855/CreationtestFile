package com.creationtime.repository;

import com.creationtime.domain.collaboration.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
