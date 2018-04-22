package eskimo.backend.dao;

import eskimo.backend.entity.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserSessionsDao {
    private static final UserSessionsRowMapper ROW_MAPPER = new UserSessionsRowMapper();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserSessionsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long addUserSession(Long userId, String token, String userAgent, String ip) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_sessions")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("token", token);
        params.put("user_agent", userAgent);
        params.put("ip", ip);
        params.put("last_request_time", LocalDateTime.now());
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    public UserSession getUserSession(Long userId, String token, String userAgent, String ip) {
        String sql = "SELECT us.id, us.user_id, us.token, us.user_agent, us.ip, us.last_request_time FROM user_sessions AS us " +
                "WHERE us.user_id = ? AND us.token = ? AND us.user_agent = ? AND us.ip = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId, token, userAgent, ip}, ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM user_sessions WHERE user_sessions.id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * Delete all user sessions
     */
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM user_sessions WHERE user_sessions.user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public void updateRequestTime(UserSession userSession) {
        String sql = "INSERT INTO user_sessions(id, user_id, token, user_agent, ip, last_request_time) " +
                "VALUES(?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE last_request_time = ?";
        jdbcTemplate.update(sql, userSession.getId(), userSession.getUserId(), userSession.getToken(),
                userSession.getUserAgent(), userSession.getIp(), userSession.getLastRequestTime(),
                userSession.getLastRequestTime());
    }

    private static class UserSessionsRowMapper implements RowMapper<UserSession> {

        @Override
        public UserSession mapRow(ResultSet resultSet, int i) throws SQLException {
            UserSession userSession = new UserSession();
            userSession.setId(resultSet.getLong("id"));
            userSession.setUserId(resultSet.getLong("user_id"));
            userSession.setToken(resultSet.getString("token"));
            userSession.setUserAgent(resultSet.getString("user_agent"));
            userSession.setIp(resultSet.getString("ip"));
            userSession.setLastRequestTime(resultSet.getTimestamp("last_request_time").toLocalDateTime());
            return userSession;
        }
    }
}
