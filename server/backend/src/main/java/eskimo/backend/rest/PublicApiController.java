package eskimo.backend.rest;

import eskimo.backend.authorization.AuthenticationHolder;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static eskimo.backend.rest.interceptors.AuthenticationInterceptor.ESKIMO_TOKEN_COOKIE_NAME;
import static eskimo.backend.rest.interceptors.AuthenticationInterceptor.ESKIMO_UID_COOKIE_NAME;

@RestController
@RequestMapping("api")
public class PublicApiController {

    private final ContestService contestService;
    private final UserService userService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    public PublicApiController(ContestService contestService, UserService userService) {
        this.contestService = contestService;
        this.userService = userService;
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
        if (actualUser == null || !actualUser.getPassword().equals(user.getPassword())) {
            return false;
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

    //в user должны присутствовать username, password
    @PostMapping("sign-in")
    public void signIn(@RequestBody User user) {
        userService.addUser(user);
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

    @GetMapping("username")
    public String getCurrentUsername() {
        User user = authenticationHolder.getUser();
        UserSession userSession = authenticationHolder.getUserSession();
        return userSession == null ? null : user.getUsername();
    }
}
