package ru.openjudge.server.service;

import ru.openjudge.server.entity.User;

import java.util.List;

public interface UserService {

    void insert(User user);

    void update(User user);

    void remove(User user);

    User getById(Long id);

    User getByLogin(String login);

    List<User> getAll();

}
