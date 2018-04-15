package eskimo.backend.dao;

import eskimo.backend.entity.User;
import eskimo.backend.entity.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long addUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getUsername());
        params.put("password", user.getPassword());
        params.put("locale", user.getLocale().getLanguage());
        params.put("role", user.getRole());
        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    @Transactional
    public List<User> getAllUsers() {
        String sql = "SELECT u.id, u.name, u.password, u.locale, u.role FROM users AS u";
        return jdbcTemplate.query(sql, new Object[]{}, new UserRowMapper());
    }

    @Transactional
    public User getUserById(Long id) {
        try {
            String sql = "SELECT u.id, u.name, u.password, u.locale, u.role FROM users AS u WHERE u.id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by id=" + id, e);
            return null;
        }
    }

    public boolean userExists(String name) {
        String sql = "SELECT users.id FROM users WHERE users.name = ?";
        return !jdbcTemplate.query(sql, new Object[]{name}, (rs, rowNum) -> null).isEmpty();
    }

    @Transactional
    public User getUserByName(String name) {
        try {
            String sql = "SELECT u.id, u.name, u.password, u.locale, u.role FROM users AS u WHERE u.name = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by name=" + name, e);
            return null;
        }
    }

    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getLong("id"));
            user.setUsername(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setLocale(new Locale(resultSet.getString("locale")));
            user.setRole(Role.valueOf(resultSet.getString("role")));
            return user;
        }
    }
}
