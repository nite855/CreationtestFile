package com.creationtime.repository;

import com.creationtime.domain.recruitment.RecruitPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {
}
