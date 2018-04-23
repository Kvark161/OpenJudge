package eskimo.backend.rest;

import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.rest.response.ChangingResponse;
import eskimo.backend.rest.response.UserInfoResponse;
import eskimo.backend.services.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static eskimo.backend.rest.interceptor.AuthenticationInterceptor.ESKIMO_TOKEN_COOKIE_NAME;
import static eskimo.backend.rest.interceptor.AuthenticationInterceptor.ESKIMO_UID_COOKIE_NAME;

@RestController
@RequestMapping("api")
public class UserController {

    private final UserService userService;
    private final AuthenticationHolder authenticationHolder;

    public UserController(UserService userService, AuthenticationHolder authenticationHolder) {
        this.userService = userService;
        this.authenticationHolder = authenticationHolder;
    }

    @GetMapping("role")
    @AccessLevel(role = Role.ANONYMOUS)
    public Role getRole() {
        User user = authenticationHolder.getUser();
        UserSession userSession = authenticationHolder.getUserSession();
        return user == null || userSession == null ? Role.ANONYMOUS : user.getRole();
    }

    //в user должны присутствовать username, password
    @PostMapping("log-in")
    @AccessLevel(role = Role.ANONYMOUS)
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
    @AccessLevel(role = Role.ANONYMOUS)
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
    @AccessLevel(role = Role.ANONYMOUS)
    public UserInfoResponse getCurrentUserInfo() {
        User user = authenticationHolder.getUser();
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @GetMapping("users")
    @AccessLevel(role = Role.ADMIN)
    public List<User> getUsers() {
        return new ArrayList<>(userService.getUsers());
    }

    @PostMapping("/user")
    @AccessLevel(role = Role.ADMIN)
    public ChangingResponse<User> createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/user/{id}")
    @AccessLevel(role = Role.ADMIN)
    public ChangingResponse<User> editUser(@RequestBody User user) {
        return userService.editUser(user);
    }

    @PostMapping("users")
    @AccessLevel(role = Role.ADMIN)
    public ChangingResponse<List<User>> createUsers(@RequestParam("usersNumber") Integer usersNumber) {
        return userService.createNUsers(usersNumber);
    }
}
