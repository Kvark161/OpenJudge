package eskimo.backend.rest;

import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.services.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("contest/{id}/dashboard")
    @AccessLevel(role = Role.ANONYMOUS)
    public Dashboard getDashboard(@PathVariable("id") Long contestId) {
        return dashboardService.getFullDashboard(contestId);
    }
}
