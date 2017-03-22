package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.User;

import java.util.List;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
public interface UserDao {

	List<User> getAllUsers();

	User getUserById(Long id);

	User getUserByName(String name);

}
