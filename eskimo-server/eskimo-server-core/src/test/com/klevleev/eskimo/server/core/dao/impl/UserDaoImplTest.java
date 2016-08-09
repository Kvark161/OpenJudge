package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/spring/spring-core.xml"})
public class UserDaoImplTest {

	@Autowired
	private UserDao userDao;

	@Test
	public void getAllUsers() throws Exception {
		List<User> users = userDao.getAllUsers();
		assertTrue(2 <= users.size());
		users.forEach(user -> {
			assertNotNull(user.getId());
			assertNotNull(user.getUsername());
			assertNotNull(user.getPassword());
		});
	}

	@Test
	public void getUserById() throws Exception {
		User user = userDao.getUserById(1L);
		assertEquals(new Long(1L), user.getId());
		assertEquals("admin", user.getUsername());
		assertEquals("admin", user.getPassword());
		assertEquals(true, user.isAdmin());
	}

	@Test
	public void getUserByName() throws Exception {
		User user = userDao.getUserByName("admin");
		assertEquals(new Long(1L), user.getId());
		assertEquals("admin", user.getUsername());
		assertEquals("admin", user.getPassword());
		assertEquals(true, user.isAdmin());
	}

}