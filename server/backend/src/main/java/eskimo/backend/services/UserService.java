package eskimo.backend.services;

import eskimo.backend.dao.UserDao;
import eskimo.backend.dao.UserSessionsDao;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.response.CreatingResponse;
import eskimo.backend.rest.response.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class UserService {
    private static final Duration TOKEN_LIVE_TIME = Duration.ofDays(7);
    private static final String USERNAME_STRING_FIELDS_MATCHER = "^[\\d\\w]+$";

    private final UserDao userDao;
    private final UserSessionsDao userSessionsDao;

    @Autowired
    public UserService(UserDao userDao, UserSessionsDao userSessionsDao) {
        this.userDao = userDao;
        this.userSessionsDao = userSessionsDao;
    }

    public CreatingResponse addUser(User user) {
        CreatingResponse creatingResponse = new CreatingResponse();
        ValidationResponse validationResponse = validateCreateUser(user);
        creatingResponse.setValidationResponse(validationResponse);
        if (validationResponse.hasErrors()) {
            return creatingResponse;
        }
        user.setLocale(Locale.ENGLISH);
        if (user.getRole() != null) {
            user.setRole(user.getRole());
        } else {
            user.setRole(Role.USER);
        }
        Long id = userDao.addUser(user);
        user.setId(id);

        creatingResponse.setCreatedObject(user);
        return creatingResponse;
    }

    private ValidationResponse validateCreateUser(User user) {
        ValidationResponse validationResponse = new ValidationResponse();
        if (user.getUsername() == null || user.getUsername().equals("")) {
            validationResponse.addError("username", "Should not be empty");
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            validationResponse.addError("password", "Should not be empty");
        }
        if (validationResponse.hasErrors()) {
            return  validationResponse;
        }
        if (!Pattern.matches(USERNAME_STRING_FIELDS_MATCHER, user.getUsername())) {
            validationResponse.addError("username", "Name should contain only latin letters and digits");
        }
        if (!Pattern.matches(USERNAME_STRING_FIELDS_MATCHER, user.getPassword())) {
            validationResponse.addError("username", "Password should contain only latin letters and digits");
        }
        if (validationResponse.hasErrors()) {
            return  validationResponse;
        }
        if (userDao.userExists(user.getUsername())) {
            validationResponse.addError("username", "User already exists");
        }
        return validationResponse;
    }

    public List<User> getUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }

    public User getUserByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("username should not be empty");
        }
        return userDao.getUserByName(name);
    }

    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }

    public UserSession addUserSession(Long userId, String userAgent, String ip) {
        byte[] bytes = new byte[20];
        try {
            SecureRandom.getInstanceStrong().nextBytes(bytes);
        } catch (NoSuchAlgorithmException e) {
            SecureRandom random = new SecureRandom();
            random.nextBytes(bytes);
        }
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        userSessionsDao.addUserSession(userId, token, userAgent, ip);
        return userSessionsDao.getUserSession(userId, token, userAgent, ip);
    }

    /**
     * Returns user session which is always not out of date
     */
    public UserSession getUserSession(Long userId, String token, String userAgent, String ip) {
        UserSession userSession = userSessionsDao.getUserSession(userId, token, userAgent, ip);
        if (userSession == null
                || userSession.getLastRequestTime().isBefore(LocalDateTime.now().minus(TOKEN_LIVE_TIME))) {
            return null;
        }
        return userSession;
    }

    public void deleteUserSession(Long userSessionId) {
        userSessionsDao.delete(userSessionId);
    }

    public void updateLastRequestTime(UserSession userSession) {
        userSession.setLastRequestTime(LocalDateTime.now());
        userSessionsDao.updateRequestTime(userSession);
    }
}
