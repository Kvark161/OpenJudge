package ru.openjudge.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.openjudge.server.dao.SubmissionDao;
import ru.openjudge.server.entity.Submission;

import java.util.List;

@Service("submissionService")
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    SubmissionDao submissionDao;

    @Override
    @Transactional
    public void insert(Submission submission) {
        submissionDao.insert(submission);
    }

    @Override
    @Transactional
    public void update(Submission submission) {
        submissionDao.update(submission);
    }

    @Override
    @Transactional
    public void remove(Submission submission) {
        submissionDao.remove(submission);
    }

    @Override
    public void rejudge(Submission submission) {
    }

    @Override
    @Transactional(readOnly = true)
    public Submission getById(Long id) {
        return submissionDao.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Submission> getAll() {
        return submissionDao.getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCount() {
        return submissionDao.getCount();
    }
}
