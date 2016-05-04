package ru.openjudge.server.service;

import ru.openjudge.server.entity.Contest;
import ru.openjudge.server.entity.Problem;

import java.util.List;

public interface ProblemService extends BaseService<Problem> {

    List<Problem> getByContest(Contest contest);

}
