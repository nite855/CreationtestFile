package com.creationtime.repository;

import com.creationtime.domain.recruitment.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
