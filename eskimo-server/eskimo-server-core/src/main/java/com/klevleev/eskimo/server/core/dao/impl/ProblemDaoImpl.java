package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.ProblemDao;
import com.klevleev.eskimo.server.core.domain.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Repository("problemDao")
public class ProblemDaoImpl implements ProblemDao {

	private static final Logger logger = LoggerFactory.getLogger(ProblemDaoImpl.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public ProblemDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public List<Problem> getContestProblems(Long contestId) {
		String sql = "SELECT p.id, p.number_in_contest, p.name, p.time_limit, p.memory_limit FROM problems AS p" +
				" WHERE p.contest_id = ?" +
				" ORDER BY p.number_in_contest";
		return jdbcTemplate.query(sql, new Object[]{contestId}, new ProblemDaoImpl.ProblemRowMapper());
	}

	@Override
	@Transactional
	public Problem getProblemById(Long id) {
		try {
			String sql = "SELECT p.id, p.number_in_contest, p.name, p.time_limit, p.memory_limit FROM problems AS p" +
					" WHERE p.id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProblemDaoImpl.ProblemRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get problem by id=" + id, e);
			return null;
		}
	}

	@Override
	@Transactional
	public InputStream getTestInput(Long problemId, Long testId) {
		try {
			//TODO call storage method
		} catch (EmptyResultDataAccessException e) {
			logger.error("can not get test input: problemId=" + problemId + " testId=" + testId);
		}
		return null;
	}

	@Override
	@Transactional
	public InputStream getTestAnswer(Long problemId, Long testId) {
		try {
			//TODO call storage method
		} catch (EmptyResultDataAccessException e) {
			logger.error("can not get test answer:  problemId=" + problemId + " testId=" + testId);
		}
		return null;
	}

	@Override
	@Transactional
	public InputStream getChecker(Long problemId) {
		try {
			//TODO call storage method
		} catch (EmptyResultDataAccessException e) {
			logger.error("can not get checker: problemId=" + problemId);
		}
		return null;
	}

	private static class ProblemRowMapper implements RowMapper<Problem> {
		@Override
		public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
			Problem problem = new Problem();
			problem.setId(resultSet.getLong("id"));
			long index = resultSet.getLong("number_in_contest");
			problem.setIndex("" + (char)(index - 1 + (int)'A'));
			problem.setName(resultSet.getString("name"));
			problem.setTimeLimit(resultSet.getLong("time_limit"));
			problem.setMemoryLimit(resultSet.getLong("memory_limit"));
			return problem;
		}
	}


}
