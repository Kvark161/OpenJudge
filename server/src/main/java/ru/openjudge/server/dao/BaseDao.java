package ru.openjudge.server.dao;

interface BaseDao<T> {

    void save(T t);

    void remove(T t);

    T getById(Long id) throws DaoException;

}
