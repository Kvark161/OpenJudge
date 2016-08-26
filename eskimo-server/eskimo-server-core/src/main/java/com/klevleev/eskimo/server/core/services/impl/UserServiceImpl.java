package com.klevleev.eskimo.server.core.services.impl;

import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.User;
import com.klevleev.eskimo.server.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Ekaterina Sokirkina on 26-Aug-16.
 */
@Component("userService")
public class UserServiceImpl implements UserService{

	private UserDao userDao;

	@Autowired
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.getAllUsers();
	}

	@Override
	public User getUserById(Long id) {
		return userDao.getUserById(id);
	}

	@Override
	public User getUserByName(String name) {
		return userDao.getUserByName(name);
	}
}
