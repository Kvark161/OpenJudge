package ru.openjudge.server.dao;

import ru.openjudge.server.entity.Contest;
import ru.openjudge.server.entity.Problem;

import java.util.List;

public interface ProblemDao extends BaseDao<Problem> {

    List<Problem> getByContest(Contest contest);

}
