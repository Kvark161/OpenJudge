package ru.openjudge.server.dao.postgresql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;
import ru.openjudge.server.util.HibernateSessionFactory;

public class UserDaoImpl implements UserDao {

    private SessionFactory sessionFactory;

    public UserDaoImpl() {
        sessionFactory = HibernateSessionFactory.getInstance();
    }

    @Override
    public User getByLogin(String login) throws DaoException {
        return null;
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        session.saveOrUpdate(user);

        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void remove(User user) {
    }

    @Override
    public User getById(Long id) throws DaoException {
        return null;
    }
}
