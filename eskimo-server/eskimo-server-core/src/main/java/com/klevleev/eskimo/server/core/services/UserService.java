package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.User;

import java.util.List;

/**
 * Created by Ekaterina Sokirkina on 26-Aug-16.
 */
public interface UserService {

	List<User> getAllUsers();

	User getUserById(Long id);

	User getUserByName(String name);
}
