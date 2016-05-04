package ru.openjudge.server.dao.postgresql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openjudge.server.dao.ProblemDao;
import ru.openjudge.server.entity.Contest;
import ru.openjudge.server.entity.Problem;

import java.util.List;

@Service
public class ProblemDaoImpl implements ProblemDao {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void insert(Problem problem) {
        Session session = sessionFactory.getCurrentSession();
        session.save(problem);
    }

    @Override
    public void update(Problem problem) {
        Session session = sessionFactory.getCurrentSession();
        session.update(problem);
    }

    @Override
    public void remove(Problem problem) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(problem);
    }

    @Override
    public Problem getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Problem.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Problem> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Problem.class).list();
    }

    @Override
    public Long getCount() {
        Session session = sessionFactory.getCurrentSession();
        return (Long)session.createQuery("select count(*) from Problem").uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Problem> getByContest(Contest contest) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Problem.class).add(Restrictions.eq("contest", contest)).list();
    }

}
