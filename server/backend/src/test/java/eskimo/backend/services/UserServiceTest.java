package eskimo.backend.services;

import eskimo.backend.BaseTest;
import eskimo.backend.dao.UserDao;
import eskimo.backend.dao.UserSessionsDao;
import eskimo.backend.entity.User;
import eskimo.backend.entity.UserSession;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.response.ChangingResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserSessionsDao userSessionsDao;

    //username validation

    @Test
    public void validateCommon_UsernameEmpty() {
        User user = new User();
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field username",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(true));
    }

    @Test
    public void validateCommon_UsernameHasWrongSymbols() {
        User user = new User();
        user.setUsername("asd7200-AG");
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field username",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(true));
    }

    @Test
    public void validateCommon_UsernameTooLong() {
        User user = new User();
        user.setUsername(Collections.nCopies(UserService.MAX_USERNAME_LENGTH + 1, 'a').stream()
                .map(Object::toString)
                .collect(joining(""))
        );
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field username",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(true));
    }

    @Test
    public void validateCommon_MaxCharactersUsername() {
        User user = new User();
        user.setUsername(Collections.nCopies(UserService.MAX_USERNAME_LENGTH, 'U').stream()
                .map(Object::toString)
                .collect(joining(""))
        );
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("username should be valid",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(false));
    }

    @Test
    public void validateCommon_ValidUsername() {
        User user = new User();
        user.setUsername("asdjk7899AHS");
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("username should be valid",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(false));
    }

    @Test
    public void validateAdd_UserAlreadyExists() {
        Long id = userDao.addUser(getValidUser());
        User validUserToAdd = getValidUser();
        ChangingResponse<User> userChangingResponse = userService.addUser(validUserToAdd);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field username",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(true));
    }

    //password validation

    @Test
    public void validateCommon_PasswordEmpty() {
        User user = new User();
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field password",
                userChangingResponse.getValidationResult().hasErrorsOnField("password"), is(true));
    }

    @Test
    public void validateCommon_PasswordHasWrongSymbols() {
        User user = new User();
        user.setPassword("asd7200-AG");
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field password",
                userChangingResponse.getValidationResult().hasErrorsOnField("password"), is(true));
    }

    @Test
    public void validateCommon_PasswordTooLong() {
        User user = new User();
        user.setPassword(Collections.nCopies(UserService.MAX_USERNAME_LENGTH + 1, 'a').stream()
                .map(Object::toString)
                .collect(joining(""))
        );
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("should be validation error on field password",
                userChangingResponse.getValidationResult().hasErrorsOnField("password"), is(true));
    }

    @Test
    public void validateCommon_MaxCharactersPassword() {
        User user = new User();
        user.setPassword(Collections.nCopies(UserService.MAX_USERNAME_LENGTH, 'U').stream()
                .map(Object::toString)
                .collect(joining(""))
        );
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("password should be valid",
                userChangingResponse.getValidationResult().hasErrorsOnField("password"), is(false));
    }

    @Test
    public void validateCommon_ValidPassword() {
        User user = new User();
        user.setPassword("asdjk7899AHS");
        ChangingResponse<User> userChangingResponse = userService.addUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("password should be valid",
                userChangingResponse.getValidationResult().hasErrorsOnField("password"), is(false));
    }

    @Test
    public void addUser_AddedCorrectly() {
        User userToAdd = new User();
        userToAdd.setUsername("aaa123");
        userToAdd.setPassword("lksdf234");
        ChangingResponse<User> userChangingResponse = userService.addUser(userToAdd);
        Long id = userChangingResponse.getChangedObject().getId();
        User actualUser = userDao.getUserById(id);

        User expectedUser = new User();
        expectedUser.setId(id);
        expectedUser.setUsername(userToAdd.getUsername());
        expectedUser.setPassword(userToAdd.getPassword());
        expectedUser.setLocale(Locale.ENGLISH);
        expectedUser.setRole(Role.USER);
        expectedUser.setBlocked(false);

        assertThat(actualUser, samePropertyValuesAs(expectedUser));
    }

    @Test
    public void addUser_WithAdminRole_AddedCorrectly() {
        User userToAdd = new User();
        userToAdd.setUsername("aaa123");
        userToAdd.setPassword("lksdf234");
        userToAdd.setRole(Role.ADMIN);
        ChangingResponse<User> userChangingResponse = userService.addUser(userToAdd);
        Long id = userChangingResponse.getChangedObject().getId();
        User actualUser = userDao.getUserById(id);

        User expectedUser = new User();
        expectedUser.setId(id);
        expectedUser.setUsername(userToAdd.getUsername());
        expectedUser.setPassword(userToAdd.getPassword());
        expectedUser.setLocale(Locale.ENGLISH);
        expectedUser.setRole(Role.ADMIN);
        expectedUser.setBlocked(false);

        assertThat(actualUser, samePropertyValuesAs(expectedUser));
    }

    @Test
    public void validateEdit_InvalidId() {
        User user = getValidUser();
        user.setId(null);
        ChangingResponse<User> userChangingResponse = userService.editUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("id should be invalid",
                userChangingResponse.getValidationResult().hasErrorsOnField("id"), is(true));
    }

    @Test
    public void validateEdit_UserDoesntExist() {
        User user = getValidUser();
        user.setId(123L);//only 1 and 2 exists
        ChangingResponse<User> userChangingResponse = userService.editUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("id should be invalid",
                userChangingResponse.getValidationResult().hasErrorsOnField("id"), is(true));
    }

    //actually try rename admin to user
    @Test
    public void validateEdit_ChangeNameToExisting() {
        User user = getValidUser();
        user.setId(1L);
        user.setUsername("user");
        ChangingResponse<User> userChangingResponse = userService.editUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("username should be invalid",
                userChangingResponse.getValidationResult().hasErrorsOnField("username"), is(true));
    }

    @Test
    public void validateEdit_EmptyRole() {
        User user = getValidUser();
        user.setRole(null);
        ChangingResponse<User> userChangingResponse = userService.editUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("role should be invalid",
                userChangingResponse.getValidationResult().hasErrorsOnField("role"), is(true));
    }

    @Test
    public void validateEdit_AnonymousRole() {
        User user = getValidUser();
        user.setRole(Role.ANONYMOUS);
        ChangingResponse<User> userChangingResponse = userService.editUser(user);
        assumeThat("validation result should exist", userChangingResponse.getValidationResult(), notNullValue());
        assertThat("role should be invalid",
                userChangingResponse.getValidationResult().hasErrorsOnField("role"), is(true));
    }

    @Test
    public void editUser_SessionsRemovedWhenPasswordChanged() {
        ChangingResponse<User> userChangingResponse = userService.addUser(getValidUser());
        User userToEdit = userChangingResponse.getChangedObject();

        userService.addUserSession(userToEdit.getId(), "userAgent", "1.2.3.4");

        userToEdit.setPassword("otherPassword");
        userService.editUser(userToEdit);

        List<UserSession> allUserSessions = userSessionsDao.getAllUserSessions(userToEdit.getId());
        assertThat(allUserSessions, hasSize(0));
    }

    @Test
    public void editUser_SessionsRemovedWhenUserBecomeBlocked() {
        ChangingResponse<User> userChangingResponse = userService.addUser(getValidUser());
        User userToEdit = userChangingResponse.getChangedObject();

        userService.addUserSession(userToEdit.getId(), "userAgent", "1.2.3.4");

        userToEdit.setBlocked(true);
        userService.editUser(userToEdit);

        List<UserSession> allUserSessions = userSessionsDao.getAllUserSessions(userToEdit.getId());
        assertThat(allUserSessions, hasSize(0));
    }

    @Test
    public void editUser_EditedSuccessfully() {
        ChangingResponse<User> userChangingResponse = userService.addUser(getValidUser());
        User userToEdit = userChangingResponse.getChangedObject();
        userToEdit.setUsername("anyOtherUsername");
        userToEdit.setPassword("otherPassword");
        userToEdit.setRole(Role.ADMIN);
        userToEdit.setBlocked(true);
        ChangingResponse<User> editResponse = userService.editUser(userToEdit);
        assertThat("no validation errors expected", editResponse.getValidationResult().hasErrors(), is(false));

        User actualUser = editResponse.getChangedObject();
        assertThat(actualUser, samePropertyValuesAs(userToEdit));
    }

    private User getValidUser() {
        User user = new User();
        user.setUsername("aaa123");
        user.setPassword("lksdf234");
        user.setBlocked(false);
        user.setLocale(Locale.ENGLISH);
        user.setRole(Role.USER);
        return user;
    }

}
