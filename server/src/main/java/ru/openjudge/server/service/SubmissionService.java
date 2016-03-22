package ru.openjudge.server.service;

import ru.openjudge.server.entity.Submission;

public interface SubmissionService {

    void save(Submission submission);

    void remove(Submission submission);

    Submission get(Long id);

}
