package ru.openjudge.server.dao.postgresql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;

@Service
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User getByLogin(String login) throws DaoException {
        return null;
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
    }

    @Override
    public void remove(User user) {
    }

    @Override
    public User getById(Long id) throws DaoException {
        return null;
    }
}
