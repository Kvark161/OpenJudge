package ru.openjudge.server.dao.postgresql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openjudge.server.dao.SubmissionDao;
import ru.openjudge.server.entity.Submission;

import java.util.List;

@Service
public class SubmissionDaoImpl implements SubmissionDao {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public void insert(Submission submission) {
        Session session = sessionFactory.getCurrentSession();
        session.save(submission);
    }

    @Override
    public void update(Submission submission) {
        Session session = sessionFactory.getCurrentSession();
        session.update(submission);
    }

    @Override
    public void remove(Submission submission) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(submission);
    }

    @Override
    public Submission getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Submission.class, id);
    }

    @Override
    public List<Submission> getAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Submission.class).list();
    }

    @Override
    public Long getCount() {
        Session session = sessionFactory.getCurrentSession();
        return (Long)session.createQuery("select count(*) from Submission").uniqueResult();
    }
}
