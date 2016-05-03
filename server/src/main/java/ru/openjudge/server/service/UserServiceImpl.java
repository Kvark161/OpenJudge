package ru.openjudge.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.openjudge.server.dao.UserDao;
import ru.openjudge.server.entity.User;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    public void remove(User user) {

    }

    @Override
    public User get(Long id) {
        return null;
    }
}
