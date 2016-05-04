package ru.openjudge.server.service;

import ru.openjudge.server.entity.User;

import java.util.List;

public interface UserService extends BaseService<User> {

    User getByLogin(String login);

}
