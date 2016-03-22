package ru.openjudge.server.dao.mapImpl;

import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class UserDaoImpl implements UserDao {

    private ConcurrentMap<Long, User> data;
    private AtomicLong maxId;

    public UserDaoImpl() {
        data = new ConcurrentHashMap<>();
        maxId = new AtomicLong(100);
        {
            User admin = new User("admin", "admin");
            data.put(1L, admin);
        }
    }

    @Override
    public void save(User user) {
        if (data.get(user.getId()) == null) {
            Utils.generateObjectId(user, maxId);
        }
        data.put(user.getId(), user);
    }

    @Override
    public void remove(User user) {
        data.remove(user.getId());
    }

    @Override
    public User getById(Long id) throws DaoException {
        User user = data.get(id);
        if (user == null) {
            throw new DaoException(String.format("User with id = %d not found", id));
        }
        return data.get(id);
    }

    @Override
    public User getByLogin(String login) throws DaoException {
        for (Map.Entry<Long, User> p : data.entrySet()) {
            if (p.getValue().getLogin().equals(login)) {
                return p.getValue();
            }
        }
        throw new DaoException(String.format("User with login = \"%s\" not found", login));
    }

}
