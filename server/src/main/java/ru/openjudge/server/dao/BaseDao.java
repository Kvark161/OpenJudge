package ru.openjudge.server.dao;

import java.util.List;

interface BaseDao<T> {

    void insert(T t);

    void update(T t);

    void remove(T t);

    T getById(Long id);

    List<T> getAll();

}
