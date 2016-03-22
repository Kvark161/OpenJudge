package ru.openjudge.server.service;

import ru.openjudge.server.entity.Contest;

public interface ContestService {

    void save(Contest contest);

    void remove(Contest contest);

    Contest get(Long id);

}
