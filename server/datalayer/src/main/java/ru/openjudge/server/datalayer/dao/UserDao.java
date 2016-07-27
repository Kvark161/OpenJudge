package ru.openjudge.server.datalayer.dao;

import ru.openjudge.server.datalayer.domain.User;

import java.util.List;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
public interface UserDao {

	List<User> getAllUsers();

	User getUserById(Long id);

	User getUserByName(String name);

}
