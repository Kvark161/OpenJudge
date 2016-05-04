package ru.openjudge.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.openjudge.server.dao.ProblemDao;
import ru.openjudge.server.entity.Contest;
import ru.openjudge.server.entity.Problem;

import java.util.List;

@Service("problemService")
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    ProblemDao problemDao;

    @Override
    @Transactional
    public void insert(Problem problem) {
        problemDao.insert(problem);
    }

    @Override
    @Transactional
    public void update(Problem problem) {
        problemDao.update(problem);
    }

    @Override
    @Transactional
    public void remove(Problem problem) {
        problemDao.remove(problem);
    }

    @Override
    @Transactional(readOnly = true)
    public Problem getById(Long id) {
        return problemDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Problem> getAll() {
        return problemDao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCount() {
        return problemDao.getCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Problem> getByContest(Contest contest) {
        return problemDao.getByContest(contest);
    }
}
