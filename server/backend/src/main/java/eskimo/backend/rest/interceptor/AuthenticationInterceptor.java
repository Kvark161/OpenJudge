package eskimo.backend.rest.interceptor;

import eskimo.backend.config.AppSettingsProvider;
import eskimo.backend.entity.Contest;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.ContestStatus;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.rest.holder.AuthenticationHolder;
import eskimo.backend.services.ContestService;
import eskimo.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.math.NumberUtils.createLong;

/**
 * Intercepts all requests to backend controllers. Decides can user do this request or not according to user role.
 * Each controller method should have {@link AccessLevel} annotation, which determines highest role,
 * that can access that method
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    public static final String ESKIMO_UID_COOKIE_NAME = "eskimoUid";
    public static final String ESKIMO_TOKEN_COOKIE_NAME = "eskimoToken";

    private static final Set<String> ESKIMO_COOKIES = Collections.unmodifiableSet(new HashSet<>(asList(
            ESKIMO_UID_COOKIE_NAME, ESKIMO_TOKEN_COOKIE_NAME
    )));

    private final UserService userService;

    @Autowired
    private AuthenticationHolder authenticationHolder;

    @Autowired
    private AppSettingsProvider appSettingsProvider;

    @Autowired
    private ContestService contestService;

    @Autowired
    public AuthenticationInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AccessLevel methodAnnotation = handlerMethod.getMethodAnnotation(AccessLevel.class);
        Role accessRole = methodAnnotation == null ? Role.ANONYMOUS : methodAnnotation.role();
        if (accessRole == Role.INVOKER) {
            return authenticateInvoker(request, response);
        } else {
            Map<String, String> cookies = getAuthorizationCookies(
                    Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}));

            Long userId = createLong(cookies.get(ESKIMO_UID_COOKIE_NAME));
            User user = userId == null ? new User() : userService.getUserById(userId);
            UserSession userSession = getUserSession(request, cookies, user);
            authenticationHolder.setUserSession(userSession);
            if (userSession == null) {
                if (user == null) {
                    user = new User();
                }
                user.setId(null);
                user.setUsername("");
                user.setRole(Role.ANONYMOUS);
            }
            authenticationHolder.setUser(user);
            boolean badUserRequest = user.getRole() == Role.USER && accessRole == Role.ADMIN;
            boolean badAnonymousRequest = user.getRole() == Role.ANONYMOUS
                    && (accessRole == Role.ADMIN || accessRole == Role.USER);
            if (!checkContestStatus(request, user, methodAnnotation == null ? ContestStatus.ANY : methodAnnotation.contestStatus())) {
                response.sendError(HttpStatus.FORBIDDEN.value());
                return false;
            }
            if (badUserRequest || badAnonymousRequest) {
                response.sendError(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }
        return true;
    }

    private boolean checkContestStatus(HttpServletRequest request, User user, ContestStatus accessContestStatus) {
        if (user.getRole() == Role.ADMIN || accessContestStatus == ContestStatus.ANY) {
            return true;
        }
        try {
            String contestIdStr = request.getParameter("contestId");
            Long contestId = Long.parseLong(contestIdStr);
            Contest contest = contestService.getContestById(contestId);
            Instant now = Instant.now();
            switch (accessContestStatus) {
                case NOT_STARTED:
                    return !contest.isStarted(now);
                case STARTED:
                    return contest.isStarted(now);
                case RUNNING:
                    return contest.isRunning(now);
                case FINISHED:
                    return contest.isFinished(now);
                default:
                    logger.warn("Not implemented case for accessing to contest " + accessContestStatus.name());
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets user session, if exists and not up-to-date or null.
     * If user session exists:
     * - if its last request time is out of date - removes session from db
     * - if last request time is OK - updates it on current time
     */
    private UserSession getUserSession(HttpServletRequest request, Map<String, String> cookies, User user) {
        if (user == null) {
            return null;
        }
        String token = cookies.get(ESKIMO_TOKEN_COOKIE_NAME);
        String userAgent = request.getHeader("User-Agent");
        String ip = "0.0.0.0";
        if (token == null || userAgent == null) {
            return null;
        }
        UserSession userSession = userService.getUserSession(user.getId(), token, userAgent, ip);
        if (userSession == null) {
            return null;
        } else {
            userService.updateLastRequestTime(userSession);
            return userSession;
        }
    }

    private boolean authenticateInvoker(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        if (token == null || !token.equalsIgnoreCase(appSettingsProvider.getInvokerToken())) {
            response.sendError(HttpStatus.FORBIDDEN.value());
            return false;
        }
        return true;
    }

    /**
     * Returns eskimo cookies. See {@link AuthenticationInterceptor#ESKIMO_COOKIES}
     */
    private Map<String, String> getAuthorizationCookies(Cookie[] cookies) {
        return Arrays.stream(cookies).filter(c -> ESKIMO_COOKIES.contains(c.getName()))
                .collect(toMap(Cookie::getName, Cookie::getValue));
    }
}
