package eskimo.backend.rest;

import eskimo.backend.entity.Contest;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.entity.enums.ScoringSystem;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.services.ContestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eskimo.backend.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("api")
public class ContestController {
    private static final Logger logger = LoggerFactory.getLogger(ContestController.class);

    @Autowired
    private DashboardService dashboardService;

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
    }

    @GetMapping("contests")
    @AccessLevel(role = Role.ANONYMOUS)
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    @GetMapping("contest/{id}")
    @AccessLevel(role = Role.ANONYMOUS)
    public Contest getContest(@PathVariable("id") Long contestId) {
        return contestService.getContestById(contestId);
    }

    @PostMapping("contest/create")
    @AccessLevel(role = Role.ADMIN)
    public Contest createContest(@RequestBody Contest contest) {
        Instant startTime = contest.getStartTime().minusMillis(TimeZone.getDefault().getRawOffset());
        contest.setStartTime(startTime);
        contest.setScoringSystem(ScoringSystem.KIROV);
        return contestService.createContest(contest);
    }

    @PostMapping("contest/{id}/edit")
    @AccessLevel(role = Role.ADMIN)
    public void editContest(@PathVariable("id") Long contestId, @RequestBody Contest contest) {
        if (!contestId.equals(contest.getId())) {
            logger.error("Contest id in path ({}) doesn't match id in request body ({})", contestId, contest.getId());
            return;
        }
        contestService.editContest(contest);
    }


    @GetMapping("contest/{id}/rebuild-dashboard")
    @AccessLevel(role = Role.ADMIN)
    public String rebuildDashboard(@PathVariable("id") Long contestId) {
        dashboardService.rebuild(contestId);
        return "done";
    }
}
