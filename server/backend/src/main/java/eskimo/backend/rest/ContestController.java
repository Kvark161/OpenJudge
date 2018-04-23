package eskimo.backend.rest;

import eskimo.backend.entity.Contest;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.services.ContestService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("api")
public class ContestController {

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
        return contestService.createContest(contest);
    }
}
