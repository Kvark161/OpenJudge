package ru.openjudge.server.dao;

import ru.openjudge.server.entity.User;

public interface UserDao extends BaseDao<User> {

    User getByLogin(String login) throws DaoException;

}
