package ru.openjudge.server.dao.mapImpl;

import org.junit.Test;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UserDaoImplTest {
    @Test
    public void saveTest1() throws DaoException {
        UserDao dao = new UserDaoImpl();
        User user = new User("user", "pass");
        dao.save(user);
        dao.getById(user.getId());
    }

    @Test
    public void saveTest2() throws DaoException {
        UserDao dao = new UserDaoImpl();
        User user = new User("user", "pass");
        try {
            dao.getByLogin(user.getLogin());
            fail("User already exists");
        } catch (DaoException e) {

        }
        dao.save(user);
        assertEquals(dao.getByLogin(user.getLogin()), user);
        long oldId = user.getId();
        user.setLogin("cat");
        user.setPassword("dog");
        dao.save(user);
        assertEquals(dao.getByLogin(user.getLogin()), user);
        assertTrue(dao.getByLogin(user.getLogin()).getId() == oldId);
    }

}
