package ru.openjudge.server.dao.postgresql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openjudge.server.dao.ContestDao;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.entity.Contest;
import ru.openjudge.server.entity.User;

import java.util.List;

@Service
public class ContestDaoImpl implements ContestDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void insert(Contest contest) {
        Session session = sessionFactory.getCurrentSession();
        session.save(contest);
    }

    @Override
    public void update(Contest contest) {
        Session session = sessionFactory.getCurrentSession();
        session.update(contest);
    }

    @Override
    public void remove(Contest contest) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(contest);
    }

    @Override
    public Contest getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Contest.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Contest> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Contest.class).list();
    }

    @Override
    public Long getCount() {
        Session session = sessionFactory.getCurrentSession();
        return (Long)session.createQuery("select count(*) from Contest").uniqueResult();
    }

}
