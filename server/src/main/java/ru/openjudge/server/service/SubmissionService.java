package ru.openjudge.server.service;

import ru.openjudge.server.entity.Submission;

import java.util.List;

public interface SubmissionService extends BaseService<Submission> {

    void rejudge(Submission submission);

}
