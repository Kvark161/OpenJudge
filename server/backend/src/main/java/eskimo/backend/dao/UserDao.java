package eskimo.backend.dao;

import eskimo.backend.entity.User;
import eskimo.backend.entity.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Long addUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("login", user.getUsername());
        params.put("name", user.getUsername());
        params.put("password", user.getPassword());
        params.put("locale", user.getLocale().getLanguage());
        params.put("role", user.getRole());
        params.put("is_blocked", user.isBlocked());
        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    @Transactional
    public List<User> addUsers(List<User> users) {
        String sql = "INSERT INTO users " +
                "(login, name, password, role, locale, is_blocked) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getRole().name());
                ps.setString(5, user.getLocale().getLanguage());
                ps.setBoolean(6, user.isBlocked());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
        return getUsersByLogins(users.stream().map(User::getUsername).collect(Collectors.toList()));
    }

    @Transactional
    public List<User> getUsersByLogins(List<String> logins) {
        String sql = "SELECT u.id, u.login, u.name, u.password, u.locale, u.role, u.is_blocked FROM users AS u " +
                "WHERE login in (:logins)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("logins", logins);
        return namedParameterJdbcTemplate.query(sql, parameters, new UserRowMapper());
    }

    public void editUser(User user) {
        String sql = "UPDATE users SET " +
                "login = ?, " +
                "name = ?, " +
                "password = ?, " +
                "role = ?, " +
                "is_blocked = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getName(), user.getPassword(), user.getRole().name(), user.isBlocked(), user.getId());
    }

    @Transactional
    public List<User> getAllUsers() {
        String sql = "SELECT u.id, u.login, u.name, u.password, u.locale, u.role, u.is_blocked FROM users AS u";
        return jdbcTemplate.query(sql, new Object[]{}, new UserRowMapper());
    }

    @Transactional
    public User getUserById(Long id) {
        try {
            String sql = "SELECT u.id, u.login, u.name, u.password, u.locale, u.role, u.is_blocked FROM users AS u WHERE u.id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by id=" + id, e);
            return null;
        }
    }

    public Long getNextUsernameNumber() {
        String prefix = "user";
        String sql = "SELECT login from users WHERE login LIKE '" + prefix + "%'";
        List<String> logins = jdbcTemplate.queryForList(sql, String.class);
        return logins.stream()
                .map(login -> login.substring(prefix.length()))
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    public boolean userExists(String login) {
        String sql = "SELECT users.id FROM users WHERE users.login = ?";
        return !jdbcTemplate.query(sql, new Object[]{login}, (rs, rowNum) -> null).isEmpty();
    }

    @Transactional
    public User getUserByLogin(String login) {
        try {
            String sql = "SELECT u.id, u.login, u.name, u.password, u.locale, u.role, u.is_blocked FROM users AS u WHERE u.login = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{login}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by login=" + login, e);
            return null;
        }
    }

    public void blockUser(Long id) {
        String sql = "UPDATE users SET is_blocked = true";
        jdbcTemplate.update(sql, id);
    }

    public void unblockUser(Long id) {
        String sql = "UPDATE users SET is_blocked = false";
        jdbcTemplate.update(sql, id);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setName(resultSet.getString("name"));
            user.setUsername(resultSet.getString("login"));
            user.setPassword(resultSet.getString("password"));
            user.setLocale(new Locale(resultSet.getString("locale")));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            user.setBlocked(resultSet.getBoolean("is_blocked"));
            return user;
        }
    }
}
