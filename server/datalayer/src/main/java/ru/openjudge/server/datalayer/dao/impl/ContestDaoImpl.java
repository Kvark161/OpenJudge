package ru.openjudge.server.datalayer.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.openjudge.server.datalayer.dao.ContestDao;
import ru.openjudge.server.datalayer.domain.Contest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stepan Klevleev on 18-Jul-16
 */
@Repository("contestDao")
public class ContestDaoImpl implements ContestDao {

	private static final Logger logger = LoggerFactory.getLogger(ContestDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	public ContestDaoImpl(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "jdbcTemplate is null in ContestDaoImpl");
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public List<Contest> getAllContests() {
		return jdbcTemplate.query(getAllContestsSQL(), new ContestRowMapper());
	}

	@Override
	@Transactional
	public void insertContest(Contest contest) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("contests")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> params = new HashMap<>();
		params.put("name", contest.getName());
		Number id = jdbcInsert.executeAndReturnKey(params);
		contest.setId(id.longValue());
	}

	private String getAllContestsSQL() {
		return "SELECT contest.id, contest.name FROM contests AS contest";
	}

	private String insertContestSQL() {
		return "INSERT INTO contests (name) VALUES (?)";
	}

	private static class ContestRowMapper implements RowMapper<Contest> {
		@Override
		public Contest mapRow(ResultSet resultSet, int i) throws SQLException {
			Contest contest = new Contest();
			contest.setId(resultSet.getLong("id"));
			contest.setName(resultSet.getString("name"));
			return contest;
		}
	}
}
