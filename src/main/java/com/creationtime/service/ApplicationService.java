package com.creationtime.service;

import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.recruitment.Application;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.user.User;
import com.creationtime.repository.ApplicationRepository;
import com.creationtime.repository.TeamRepository;
import com.creationtime.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public Application applyToTeam(Long teamId, Long applicantId, String message) {
        Team team = findTeam(teamId);
        User applicant = findUser(applicantId);
        return applicationRepository.save(team.apply(applicant, message));
    }

    public void approveApplication(Long applicationId, Long leaderId, String teamRole) {
        Application application = findApplication(applicationId);
        application.targetTeam().approveApplication(application, findUser(leaderId), teamRole);
        applicationRepository.save(application);
        teamRepository.save(application.targetTeam());
    }

    public void rejectApplication(Long applicationId, Long leaderId) {
        Application application = findApplication(applicationId);
        application.targetTeam().rejectApplication(application, findUser(leaderId));
        applicationRepository.save(application);
    }

    private Application findApplication(Long id) {
        return applicationRepository.findById(id).orElseThrow(() -> new DomainException("application not found."));
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new DomainException("team not found."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("user not found."));
    }
}
