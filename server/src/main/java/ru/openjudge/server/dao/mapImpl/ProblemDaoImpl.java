package ru.openjudge.server.dao.mapImpl;

import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.dao.ProblemDao;
import ru.openjudge.server.entity.Problem;
import ru.openjudge.server.entity.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProblemDaoImpl implements ProblemDao{

    private ConcurrentMap<Long, Problem> data;
    private AtomicLong maxId;

    public ProblemDaoImpl() {
        data = new ConcurrentHashMap<>();
        maxId = new AtomicLong(100);
    }

    @Override
    public void save(Problem problem) {
        if (data.get(problem.getId()) == null) {
            Utils.generateObjectId(problem, maxId);
        }
        data.put(problem.getId(), problem);
    }

    @Override
    public void remove(Problem problem) {
        data.remove(problem.getId());
    }

    @Override
    public Problem getById(Long id) throws DaoException {
        Problem problem = data.get(id);
        if (problem == null) {
            throw new DaoException(String.format("Problem with id = %d not found", id));
        }
        return data.get(id);
    }
}
