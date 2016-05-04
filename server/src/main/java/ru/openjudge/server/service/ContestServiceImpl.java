package ru.openjudge.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.openjudge.server.dao.ContestDao;
import ru.openjudge.server.entity.Contest;

import java.util.List;

@Service
public class ContestServiceImpl implements ContestService {

    @Autowired
    private ContestDao contestDao;

    @Override
    @Transactional
    public void insert(Contest contest) {
        contestDao.insert(contest);
    }

    @Override
    @Transactional
    public void update(Contest contest) {
        contestDao.update(contest);
    }

    @Override
    @Transactional
    public void remove(Contest contest) {
        contestDao.remove(contest);
    }

    @Override
    @Transactional
    public Contest getById(Long id) {
        return contestDao.getById(id);
    }

    @Override
    @Transactional
    public List<Contest> getAll() {
        return contestDao.getAll();
    }
}
