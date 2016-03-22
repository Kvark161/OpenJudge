package ru.openjudge.server.service;

import ru.openjudge.server.entity.User;

public interface UserService {

    void save(User user);

    void remove(User user);

    User get(Long id);

}
