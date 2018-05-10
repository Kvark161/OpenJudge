package eskimo.backend.dao;

import eskimo.backend.BaseTest;
import eskimo.backend.entity.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest extends BaseTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void getAllUsers() {
        List<User> users = userDao.getAllUsers();
        assertTrue(2 <= users.size());
        users.forEach(user -> {
            assertNotNull(user.getId());
            assertNotNull(user.getUsername());
            assertNotNull(user.getPassword());
        });
    }

    @Test
    public void getUserById() {
        User user = userDao.getUserById(1L);
        assertEquals(new Long(1L), user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
    }

    @Test
    public void getUserByName() {
        User user = userDao.getUserByLogin("admin");
        assertEquals(new Long(1L), user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
    }

}
