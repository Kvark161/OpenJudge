package eskimo.backend.dao;

import eskimo.backend.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Stepan Klevleev on 27-Jul-16.
 */
@Repository
public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public List<User> getAllUsers() {
        String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u";
        return jdbcTemplate.query(sql, new Object[]{}, new UserRowMapper());
    }

    @Transactional
    public User getUserById(Long id) {
        try {
            String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u WHERE u.id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by id=" + id, e);
            return null;
        }
    }

    @Transactional
    public User getUserByName(String name) {
        try {
            String sql = "SELECT u.id, u.name, u.password, u.is_admin, u.locale FROM users AS u WHERE u.name = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{name}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("can not get user by name=" + name, e);
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
