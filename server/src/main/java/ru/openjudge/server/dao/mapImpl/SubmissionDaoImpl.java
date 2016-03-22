package ru.openjudge.server.dao.mapImpl;

import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.SubmissionDao;
import ru.openjudge.server.entity.Submission;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class SubmissionDaoImpl implements SubmissionDao {

    private ConcurrentMap<Long, Submission> data;

    private AtomicLong maxId;

    public SubmissionDaoImpl() {
        data = new ConcurrentHashMap<>();
        maxId = new AtomicLong(100);
    }

    @Override
    public void save(Submission submission) {
        if (data.get(submission.getId()) == null) {
            Utils.generateObjectId(submission, maxId);
        }
        data.put(submission.getId(), submission);
    }

    @Override
    public void remove(Submission submission) {
        data.remove(submission.getId());
    }

    @Override
    public Submission getById(Long id) throws DaoException {
        Submission submission = data.get(id);
        if (submission == null) {
            throw new DaoException(String.format("submission with id = %d not found", id));
        }
        return data.get(id);
    }
}
