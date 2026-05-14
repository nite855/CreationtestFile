package com.creationtime.repository;

import com.creationtime.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProfileInfoLoginId(String loginId);

    boolean existsByProfileInfoEmail(String email);
}
