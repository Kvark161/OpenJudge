package ru.openjudge.server.service;

import ru.openjudge.server.entity.Contest;

import java.util.List;

public interface ContestService {

    void insert(Contest contest);

    void update(Contest contest);

    void remove(Contest contest);

    Contest getById(Long id);

    List<Contest> getAll();

}
