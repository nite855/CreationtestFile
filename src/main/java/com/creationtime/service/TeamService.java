package com.creationtime.service;

import com.creationtime.domain.collaboration.Schedule;
import com.creationtime.domain.collaboration.Vote;
import com.creationtime.domain.common.DomainException;
import com.creationtime.domain.recruitment.Application;
import com.creationtime.domain.team.DismissalReason;
import com.creationtime.domain.team.Team;
import com.creationtime.domain.team.TeamCategory;
import com.creationtime.domain.user.User;
import com.creationtime.repository.ApplicationRepository;
import com.creationtime.repository.ScheduleRepository;
import com.creationtime.repository.TeamRepository;
import com.creationtime.repository.UserRepository;
import com.creationtime.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    public Team createTeam(Long leaderId, String name, TeamCategory category, int maxMemberCount, String requiredTechStack, String description) {
        if (teamRepository.existsByName(name)) {
            throw new DomainException("team name already exists.");
        }
        User leader = findUser(leaderId);
        return teamRepository.save(new Team(name, category, maxMemberCount, requiredTechStack, description, leader));
    }

    public void deleteTeam(Long teamId, Long leaderId, String confirmTeamName) {
        Team team = findTeam(teamId);
        team.delete(findUser(leaderId), confirmTeamName);
        teamRepository.deleteById(teamId);
    }

    public void changeMemberTeamRole(Long teamId, Long leaderId, Long targetUserId, String teamRole) {
        Team team = findTeam(teamId);
        team.changeMemberTeamRole(findUser(leaderId), findUser(targetUserId), teamRole);
        teamRepository.save(team);
    }

    public void dismissMember(Long teamId, Long leaderId, Long targetUserId, DismissalReason reason) {
        Team team = findTeam(teamId);
        User targetUser = findUser(targetUserId);
        team.dismissMember(findUser(leaderId), targetUser, reason);
        teamRepository.save(team);
        userRepository.save(targetUser);
    }

    public Vote createVote(Long teamId, Long leaderId, String title, String description, List<String> options, LocalDateTime deadline) {
        Team team = findTeam(teamId);
        Vote vote = new Vote(title, description, options, deadline, findUser(leaderId), team);
        return voteRepository.save(vote);
    }

    public void participateVote(Long voteId, Long userId, String selectedOption) {
        Vote vote = voteRepository.findById(voteId).orElseThrow(() -> new DomainException("vote not found."));
        vote.participate(findUser(userId), selectedOption);
        voteRepository.save(vote);
    }

    public Schedule createSchedule(Long teamId, Long leaderId, String title, String content, LocalDateTime start, LocalDateTime end) {
        Team team = findTeam(teamId);
        Schedule schedule = new Schedule(title, content, start, end, findUser(leaderId), team);
        return scheduleRepository.save(schedule);
    }

    public String exportCsv(Long teamId, Long leaderId) {
        Team team = findTeam(teamId);
        team.assertLeader(findUser(leaderId));
        StringBuilder csv = new StringBuilder("userId,name,userRole,teamRole\n");
        team.members().forEach(member -> csv.append(member.user().id())
                .append(",")
                .append(member.user().name())
                .append(",")
                .append(member.user().role())
                .append(",")
                .append(member.teamRole())
                .append("\n"));
        return csv.toString();
    }

    private Team findTeam(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new DomainException("team not found."));
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("user not found."));
    }
}
