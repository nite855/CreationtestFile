# Creation Time

Creation Time is a team recruiting and collaboration domain model based on a rich domain design.

## Design Review

The draft class diagram has the right core nouns, but these points needed correction before implementation:

- `User.role` is the user's system authority: `REGISTERED_USER`, `TEAM_MEMBER`, or `TEAM_LEADER`.
- `TeamMember.teamRole` is the free-form role inside a team, such as backend, frontend, server, designer, or planner.
- `Application.approve()` should not secretly mutate `Team`. Approval is a team rule, so `Team.approveApplication(...)` validates the leader, capacity, duplicate membership, and then changes the application and members together.
- `RecruitPost extends Post` mixes public recruiting posts with team-internal board posts. They are separate domain concepts here.
- `delete()` flags inside entities are weaker than explicit aggregate behavior. This project uses `close`, `cancel`, `remove`, and repository deletion at the service layer.
- To keep the undergraduate project scale reasonable, small value objects such as profile info, team info, recruit info, and vote option were absorbed into their owning entity classes.

## Package Structure

```text
src/main/java/com/creationtime
  controller      Spring Web controller classes
  domain          JPA rich domain entities and small enums
  repository      Spring Data JPA repository contracts
  service         transactional use-case orchestration
```

## Current Role Model

- `UserRole.REGISTERED_USER`: signed-up user who has not joined or created a team.
- `UserRole.TEAM_MEMBER`: user accepted into a team.
- `UserRole.TEAM_LEADER`: user who created/manages a team.
- `TeamMember.teamRole`: free-form team responsibility written by the team leader.

## Run

```powershell
mvn spring-boot:run
```
