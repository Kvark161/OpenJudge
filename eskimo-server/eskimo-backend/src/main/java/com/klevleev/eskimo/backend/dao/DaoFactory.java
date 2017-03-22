package com.klevleev.eskimo.backend.dao;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by stepank on 21.03.2017.
 */
@Component
public class DaoFactory {

    @Getter private static ContestDao contestDao;
    @Getter private static StatementDao statementDao;
    @Getter private static ProblemDao problemDao;

    @Autowired
    public void init(ContestDao contestDao,
              StatementDao statementDao,
              ProblemDao problemDao) {
        DaoFactory.contestDao = contestDao;
        DaoFactory.problemDao = problemDao;
        DaoFactory.statementDao = statementDao;
    }
}
