package ru.openjudge.server.service;

import java.util.List;

interface BaseService<T> {

    void insert(T t);

    void update(T t);

    void remove(T t);

    T getById(Long id);

    List<T> getAll();

    Long getCount();

}
