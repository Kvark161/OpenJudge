package ru.openjudge.server.dao.mapImpl;

import ru.openjudge.server.dao.ContestDao;
import ru.openjudge.server.dao.DaoException;
import ru.openjudge.server.entity.Contest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class ContestDaoImpl implements ContestDao {
    private ConcurrentMap<Long, Contest> data;
    private AtomicLong maxId;

    public ContestDaoImpl() {
        data = new ConcurrentHashMap<>();
        maxId = new AtomicLong(100);

    }

    @Override
    public void save(Contest contest) {
        if (contest.getId() == null){
            Utils.generateObjectId(contest, maxId);
        }
        data.put(contest.getId(), contest);
    }

    @Override
    public void remove(Contest contest) {
        data.remove(contest.getId());
    }

    @Override
    public Contest getById(Long id) throws DaoException {
        Contest contest = data.get(id);
        if (contest == null) {
            throw new DaoException(String.format("Contest with id = %d not found", id));
        }
        return data.get(id);
    }
}
