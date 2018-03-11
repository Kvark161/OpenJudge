package eskimo.backend.dao;

import eskimo.backend.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

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
        User user = userDao.getUserByName("admin");
        assertEquals(new Long(1L), user.getId());
        assertEquals("admin", user.getUsername());
        assertEquals("admin", user.getPassword());
    }

}
