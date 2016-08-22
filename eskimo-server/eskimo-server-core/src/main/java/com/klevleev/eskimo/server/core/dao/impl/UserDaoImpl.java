package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.UserDao;
import com.klevleev.eskimo.server.core.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
@Repository("userDao")
public class UserDaoImpl implements UserDao {

	private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public UserDaoImpl(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "jdbcTemplate is null in UserDaoImpl");
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public List<User> getAllUsers() {
		String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u";
		return jdbcTemplate.query(sql, new Object[]{}, new UserRowMapper());
	}

	@Override
	@Transactional
	public User getUserById(Long id) {
		try {
			String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u WHERE u.id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get user by id=" + id, e);
			return null;
		}
	}

	@Override
	@Transactional
	public User getUserByName(String name) {
		try {
			String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u WHERE u.name = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{name}, new UserRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get user by name=" + name, e);
			return null;
		}
	}

	private static class UserRowMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet resultSet, int i) throws SQLException {
			User user = new User();
			user.setId(resultSet.getLong("id"));
			user.setUsername(resultSet.getString("name"));
			user.setPassword(resultSet.getString("password"));
			user.setAdmin(resultSet.getBoolean("is_admin"));
			user.setLocale(new Locale(resultSet.getString("locale")));
			return user;
		}
	}
}
