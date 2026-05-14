package com.creationtime.repository;

import com.creationtime.domain.collaboration.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
