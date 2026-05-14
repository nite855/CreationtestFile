package com.creationtime.controller;

import com.creationtime.domain.collaboration.Schedule;
import com.creationtime.domain.collaboration.Vote;
import com.creationtime.domain.team.DismissalReason;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.team.TeamInfo;
import com.creationtime.service.ApplicationService;
import com.creationtime.service.TeamService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;
    private final ApplicationService applicationService;

    public TeamController(TeamService teamService, ApplicationService applicationService) {
        this.teamService = teamService;
        this.applicationService = applicationService;
    }

    @PostMapping
    public Team createTeam(@RequestBody CreateTeamRequest request) {
        return teamService.createTeam(request.leaderId(), request.teamInfo());
    }

    @PostMapping("/{teamId}/applications")
    public void applyToTeam(@PathVariable Long teamId, @RequestBody ApplyRequest request) {
        applicationService.applyToTeam(teamId, request.applicantId(), request.message());
    }

    @PostMapping("/applications/{applicationId}/review")
    public void reviewApplication(@PathVariable Long applicationId, @RequestBody ReviewApplicationRequest request) {
        if (request.approved()) {
            applicationService.approveApplication(applicationId, request.leaderId(), request.teamRole());
        } else {
            applicationService.rejectApplication(applicationId, request.leaderId());
        }
    }

    @PostMapping("/{teamId}/members/{targetUserId}/team-role")
    public void changeMemberTeamRole(
            @PathVariable Long teamId,
            @PathVariable Long targetUserId,
            @RequestBody ChangeTeamRoleRequest request
    ) {
        teamService.changeMemberTeamRole(teamId, request.leaderId(), targetUserId, request.teamRole());
    }

    @DeleteMapping("/{teamId}/members/{targetUserId}")
    public void dismissMember(
            @PathVariable Long teamId,
            @PathVariable Long targetUserId,
            @RequestBody DismissMemberRequest request
    ) {
        teamService.dismissMember(teamId, request.leaderId(), targetUserId, request.reason());
    }

    @PostMapping("/{teamId}/votes")
    public Vote createVote(@PathVariable Long teamId, @RequestBody CreateVoteRequest request) {
        return teamService.createVote(teamId, request.leaderId(), request.title(), request.description(), request.options(), request.deadline());
    }

    @PostMapping("/votes/{voteId}/ballots")
    public void participateVote(@PathVariable Long voteId, @RequestBody ParticipateVoteRequest request) {
        teamService.participateVote(voteId, request.userId(), request.optionId());
    }

    @PostMapping("/{teamId}/schedules")
    public Schedule createSchedule(@PathVariable Long teamId, @RequestBody CreateScheduleRequest request) {
        return teamService.createSchedule(teamId, request.leaderId(), request.title(), request.content(), request.start(), request.end());
    }

    public String exportCsv(Long teamId, Long leaderId) {
        return teamService.exportCsv(teamId, leaderId);
    }

    public record CreateTeamRequest(Long leaderId, TeamInfo teamInfo) {
    }

    public record ApplyRequest(Long applicantId, String message) {
    }

    public record ReviewApplicationRequest(Long leaderId, boolean approved, String teamRole) {
    }

    public record ChangeTeamRoleRequest(Long leaderId, String teamRole) {
    }

    public record DismissMemberRequest(Long leaderId, DismissalReason reason) {
    }

    public record CreateVoteRequest(Long leaderId, String title, String description, List<String> options, LocalDateTime deadline) {
    }

    public record ParticipateVoteRequest(Long userId, long optionId) {
    }

    public record CreateScheduleRequest(Long leaderId, String title, String content, LocalDateTime start, LocalDateTime end) {
    }
}
