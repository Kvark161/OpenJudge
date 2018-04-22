package eskimo.backend.services;

import eskimo.backend.dao.UserDao;
import eskimo.backend.dao.UserSessionsDao;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.response.ChangingResponse;
import eskimo.backend.rest.response.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.IntStream;

import static eskimo.backend.utils.Utils.generatePassword;
import static java.util.stream.Collectors.toList;

@Component
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final Duration TOKEN_LIVE_TIME = Duration.ofDays(7);
    private static final String USERNAME_STRING_FIELDS_MATCHER = "^[\\d\\w]+$";
    private static final int MAX_GENERATE_USERS_ATTEMPTS = 10;

    private final UserDao userDao;
    private final UserSessionsDao userSessionsDao;

    @Autowired
    public UserService(UserDao userDao, UserSessionsDao userSessionsDao) {
        this.userDao = userDao;
        this.userSessionsDao = userSessionsDao;
    }

    public ChangingResponse<User> addUser(User user) {
        ChangingResponse<User> changingResponse = new ChangingResponse<>();
        ValidationResult validationResponse = validateCommon(user);
        validateAdd(user, validationResponse);
        changingResponse.setValidationResult(validationResponse);
        if (validationResponse.hasErrors()) {
            return changingResponse;
        }
        user.setLocale(Locale.ENGLISH);
        if (user.getRole() != null) {
            user.setRole(user.getRole());
        } else {
            user.setRole(Role.USER);
        }
        Long id = userDao.addUser(user);
        user.setId(id);

        changingResponse.setChangedObject(user);
        return changingResponse;
    }

    public ChangingResponse<User> editUser(User user) {
        ChangingResponse<User> changingResponse = new ChangingResponse<>();
        ValidationResult validationResponse = validateCommon(user);
        validateEdit(user, validationResponse);
        changingResponse.setValidationResult(validationResponse);
        if (validationResponse.hasErrors()) {
            return changingResponse;
        }
        User oldUser = userDao.getUserById(user.getId());
        boolean passwordChanged = !oldUser.getPassword().equals(user.getPassword());
        userDao.editUser(user);
        if (passwordChanged || oldUser.isBlocked()) {
            userSessionsDao.deleteByUserId(oldUser.getId());
        }
        changingResponse.setChangedObject(userDao.getUserById(user.getId()));
        return changingResponse;
    }

    public ChangingResponse<List<User>> createNUsers(Integer usersNumber) {
        ChangingResponse<List<User>> response = new ChangingResponse<>();
        ValidationResult validationResult = new ValidationResult();
        if (usersNumber == null || usersNumber < 1 || usersNumber > 100) {
            validationResult.addError("usersNumber", "Should be between 1 and 100");
        }
        response.setValidationResult(validationResult);
        if (validationResult.hasErrors()) {
            return response;
        }

        List<User> users = generateUsers(usersNumber);
        response.setChangedObject(users);
        return response;
    }

    private List<User> generateUsers(int usersNumber) {
        List<User> users = IntStream.range(0, usersNumber)
                .boxed()
                .map(i -> prepareUser())
                .collect(toList());
        int attempt = 1;
        Long nextId = userDao.getNextUsernameNumber();
        for (int i = 0; i < users.size(); ++i) {
            users.get(i).setUsername("user" + (nextId + i));
        }
        do {
            try {
                return userDao.addUsers(users);
            } catch (Exception e) {
                logger.info("Generate users. Bad attempt " + attempt, e);
            }
        } while (attempt++ < MAX_GENERATE_USERS_ATTEMPTS);
        throw new RuntimeException("Can't generate users after " + MAX_GENERATE_USERS_ATTEMPTS + " attempts");
    }

    private User prepareUser() {
        User user = new User();
        user.setPassword(generatePassword());
        user.setRole(Role.USER);
        user.setLocale(Locale.ENGLISH);
        user.setBlocked(false);
        return user;
    }

    private ValidationResult validateCommon(User user) {
        ValidationResult validationResponse = new ValidationResult();
        if (user.getUsername() == null || user.getUsername().equals("")) {
            validationResponse.addError("username", "Should not be empty");
        } else if (!Pattern.matches(USERNAME_STRING_FIELDS_MATCHER, user.getUsername())) {
            validationResponse.addError("username", "Name should contain only latin letters and digits");
        }
        if (user.getPassword() == null || user.getPassword().equals("")) {
            validationResponse.addError("password", "Should not be empty");
        } else if (!Pattern.matches(USERNAME_STRING_FIELDS_MATCHER, user.getPassword())) {
            validationResponse.addError("username", "Password should contain only latin letters and digits");
        }
        return validationResponse;
    }

    private void validateAdd(User user, ValidationResult validationResponse) {
        if (!validationResponse.hasErrorsOnField("username") && userDao.userExists(user.getUsername())) {
            validationResponse.addError("username", "User already exists");
        }
    }

    /**
     * Additional validation for edit action
     */
    private void validateEdit(User user, ValidationResult validationResponse) {
        if (user.getId() == null) {
            validationResponse.addError("id", "Id can not be empty");
        } else {
            User oldUser = userDao.getUserById(user.getId());
            if (oldUser == null) {
                validationResponse.addError("id", "User doesn't exist");
            } else if (!user.getUsername().equals(oldUser.getUsername()) &&
                    userDao.userExists(user.getUsername())) {
                validationResponse.addError("username", "User already exists");
            }
        }
        if (user.getRole() == null) {
            validationResponse.addError("role", "Role can not be empty");
        } else if (user.getRole() == Role.ANONYMOUS) {
            validationResponse.addError("role", "Can't set anonymous role to user");
        }
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
