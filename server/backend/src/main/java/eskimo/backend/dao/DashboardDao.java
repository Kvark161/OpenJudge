package eskimo.backend.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eskimo.backend.entity.dashboard.Dashboard;
import eskimo.backend.entity.dashboard.DashboardRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class DashboardDao {

    private static final Logger logger = LoggerFactory.getLogger(DashboardDao.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private DashboardRowMapper rowMapper = new DashboardRowMapper();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public Dashboard getDashboard(long contestId) {
        try {
            String sql = "SELECT * FROM dashboard WHERE contest_id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, contestId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateDashboard(Dashboard dashboard) {
        try {
            String sql = "INSERT INTO dashboard(contest_id, data, last_update) " +
                    "VALUES(?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "data = ?," +
                    "last_update = ?";
            String data = objectMapper.writeValueAsString(dashboard.getTable());
            Timestamp now = Timestamp.from(Instant.now());
            jdbcTemplate.update(sql, dashboard.getContestId(), data, now, data, now);
        } catch (JsonProcessingException e) {
            logger.error("Can't convert dashboard table to json", e);
            throw new RuntimeException(e);
        }
    }

    private static class DashboardRowMapper implements RowMapper<Dashboard> {
        @Override
        public Dashboard mapRow(ResultSet resultSet, int i) throws SQLException {
            Dashboard dashboard = new Dashboard();
            try {
                dashboard.setContestId(resultSet.getLong("contest_id"));
                dashboard.setLastUpdate(resultSet.getTimestamp("last_update").toInstant());
                dashboard.setTable(objectMapper.readValue(resultSet.getString("data"), new TypeReference<List<DashboardRow>>() {
                }));
            } catch (IOException e) {
                logger.error("Cant parse dashboard data: " + resultSet.getString("data"), e);
            }
            return dashboard;
        }
    }
}
