package ru.openjudge.server.service;

import ru.openjudge.server.entity.Problem;

public interface ProblemService {

    void save(Problem problem);

    void remove(Problem problem);

    Problem get(Long id);

}
