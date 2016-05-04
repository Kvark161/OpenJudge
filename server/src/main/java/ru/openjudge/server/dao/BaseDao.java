package ru.openjudge.server.dao;

import java.util.List;

interface BaseDao<T> {

    void save(T t);

    void remove(T t);

    T getById(Long id);

    List<T> getAll();

}
