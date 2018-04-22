package eskimo.backend.rest;

import eskimo.backend.entity.Contest;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.response.UserInfoResponse;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.DashboardService;
import eskimo.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import static eskimo.backend.rest.interceptor.AuthenticationInterceptor.ESKIMO_TOKEN_COOKIE_NAME;
import static eskimo.backend.rest.interceptor.AuthenticationInterceptor.ESKIMO_UID_COOKIE_NAME;

@RestController
@RequestMapping("api")
public class PublicApiController {

    private final ContestService contestService;
    private final UserService userService;
    private final DashboardService dashboardService;

    private final AuthenticationHolder authenticationHolder;


    @Autowired
    public PublicApiController(ContestService contestService, UserService userService, DashboardService dashboardService, AuthenticationHolder authenticationHolder) {
        this.contestService = contestService;
        this.userService = userService;
        this.dashboardService = dashboardService;
        this.authenticationHolder = authenticationHolder;
    }

    @GetMapping("contests")
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    @GetMapping("contest/{id}")
    public Contest getContest(@PathVariable("id") Long contestId) {
        return contestService.getContestById(contestId);
    }


    @GetMapping("role")
    public Role getRole() {
        User user = authenticationHolder.getUser();
        UserSession userSession = authenticationHolder.getUserSession();
        return user == null || userSession == null ? Role.ANONYMOUS : user.getRole();
    }

    //в user должны присутствовать username, password
    @PostMapping("log-in")
    public boolean login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        User actualUser = userService.getUserByName(user.getUsername());
        if (actualUser == null) {
            return false;
        }
        if (!actualUser.getPassword().equals(user.getPassword())) {
            throw  new RuntimeException("Wrong password");
        }
        if (actualUser.isBlocked()) {
            throw  new RuntimeException("This user is blocked");
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            throw new IllegalArgumentException("can't login (missed header 'User-Agent')");
        }
        String ip = request.getRemoteAddr();
        UserSession userSession = userService.addUserSession(actualUser.getId(), userAgent, ip);
        response.addCookie(new Cookie(ESKIMO_UID_COOKIE_NAME, actualUser.getId().toString()));
        response.addCookie(new Cookie(ESKIMO_TOKEN_COOKIE_NAME, userSession.getToken()));
        return true;
    }

    @GetMapping("log-out")
    public void logOut() {
        User user = authenticationHolder.getUser();
        UserSession userSession = authenticationHolder.getUserSession();
        if (user == null || userSession == null) {
            //no user to log out
            return;
        }
        userService.deleteUserSession(userSession.getId());
    }

    @GetMapping("current-user")
    public UserInfoResponse getCurrentUserInfo() {
        User user = authenticationHolder.getUser();
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @GetMapping("contest/{id}/dashboard")
    public Dashboard getDashboard(@PathVariable("id") Long contestId) {
        return dashboardService.getFullDashboard(contestId);
    }

    @GetMapping("server-time")
    public String getServerTime() {
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(Instant.now().atZone(TimeZone.getDefault().toZoneId()));
    }
}
