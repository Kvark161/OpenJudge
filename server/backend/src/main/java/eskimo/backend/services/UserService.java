package eskimo.backend.services;

import eskimo.backend.dao.UserDao;
import eskimo.backend.dao.UserSessionsDao;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Component
public class UserService {
    private static final Duration TOKEN_LIVE_TIME = Duration.ofDays(7);

    private final UserDao userDao;
    private final UserSessionsDao userSessionsDao;

    @Autowired
    public UserService(UserDao userDao, UserSessionsDao userSessionsDao) {
        this.userDao = userDao;
        this.userSessionsDao = userSessionsDao;
    }


    public Long addUser(User user) {
        if (user.getUsername() == null || user.getUsername().equals("")) {
            throw new IllegalArgumentException("username should not be empty");
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            throw new IllegalArgumentException("password should not be empty");
        }

        if (userDao.userExists(user.getUsername())) {
            throw new IllegalArgumentException("user already exists");
        }
        user.setLocale(Locale.ENGLISH);
        user.setRole(Role.USER);
        return userDao.addUser(user);
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
