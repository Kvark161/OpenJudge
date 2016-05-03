package ru.openjudge.server.dao.postgresql;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;

import java.util.List;

@Service
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User getByLogin(String login) throws DaoException {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        User user = (User)criteria.add(Restrictions.eq("login", login)).uniqueResult();
        return user;
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
    }

    @Override
    public void remove(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(user);
    }

    @Override
    public User getById(Long id) throws DaoException {
        Session session = sessionFactory.getCurrentSession();
        User user = (User)session.get(User.class, id);
        return user;
    }

    @Override
    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        List<User> users = session.createCriteria(User.class).list();
        return users;
    }


}
