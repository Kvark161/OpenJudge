package ru.openjudge.server.datalayer.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.openjudge.server.datalayer.dao.ContestDao;
import ru.openjudge.server.datalayer.domain.Contest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Stepan Klevleev on 18-Jul-16
 */
@Repository("contestDao")
public class ContestDaoImpl implements ContestDao {

	private static final Logger logger = LoggerFactory.getLogger(ContestDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional
	public List<Contest> getAllContests() {
		return jdbcTemplate.query(getAllContestsSQL(), new ContestRowMapper());
	}

	@Override
	@Transactional
	public void insertContest(Contest contest) {
		jdbcTemplate.update(insertContestSQL(), contest.getName());
	}

	private String getAllContestsSQL() {
		return "select CONTEST.ID, CONTEST.NAME from CONTESTS as CONTEST";
	}

	private String insertContestSQL() {
		return "insert into CONTESTS (NAME) values (?)";
	}

	private static class ContestRowMapper implements RowMapper<Contest> {
		@Override
		public Contest mapRow(ResultSet resultSet, int i) throws SQLException {
			Contest contest = new Contest();
			contest.setId(resultSet.getLong("ID"));
			contest.setName(resultSet.getString("NAME"));
			return contest;
		}
	}
}
