package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Contest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Repository
public class ContestDao {

    private static final Logger logger = LoggerFactory.getLogger(ContestDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ContestDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Contest> getAllContests() {
        String sql = "SELECT id, name, start_time, duration_in_minutes FROM contests";
        return jdbcTemplate.query(sql, new ContestRowMapper());
    }

    public Long insertContest(Contest contest) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("contests")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("name", contest.getName());
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    public Contest getContestInfo(long id) {
        try {
            String sql = "SELECT id, name, start_time, duration_in_minutes FROM contests WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ContestDao.ContestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.warn("can not get contest by id = " + id, e);
            return null;
        }
    }

    public boolean contestExists(long id) {
        String sql = "SELECT count(*) FROM contests WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class) > 0;
    }

    private static class ContestRowMapper implements RowMapper<Contest> {

        @Override
        public Contest mapRow(ResultSet resultSet, int i) throws SQLException {
            Contest contest = new Contest();
            contest.setId(resultSet.getLong("id"));
            contest.setName(resultSet.getString("name"));
            Timestamp startTime = resultSet.getTimestamp("start_time");
            if (startTime != null) {
                contest.setStartTime(startTime.toLocalDateTime());
            } else {
                contest.setStartTime(null);
            }
            contest.setDuration(resultSet.getInt("duration_in_minutes"));
            return contest;
        }
    }
}
