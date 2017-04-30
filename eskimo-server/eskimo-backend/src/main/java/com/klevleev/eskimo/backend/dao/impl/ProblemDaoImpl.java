package com.klevleev.eskimo.backend.dao.impl;

import com.klevleev.eskimo.backend.dao.ProblemDao;
import com.klevleev.eskimo.backend.domain.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Repository("problemDao")
@Slf4j
public class ProblemDaoImpl implements ProblemDao {


	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Long insertProblem(Problem problem, Long contestId) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("problems")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> params = new HashMap<>();
		params.put("contest_id", contestId);
		params.put("name", problem.getName());
		params.put("time_limit", problem.getTimeLimit());
		params.put("memory_limit", problem.getMemoryLimit());
		params.put("tests_count", problem.getTests().size());
		params.put("number_in_contest", problem.getIndex());
		return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
	}

	@Override
	@Transactional
	public List<Problem> getContestProblems(Long contestId) {
		String sql = "SELECT p.id FROM problems AS p" +
				" WHERE p.contest_id = ?" +
				" ORDER BY p.number_in_contest";
		return jdbcTemplate.query(sql, new Object[]{contestId}, new ProblemIdRowMapper());
	}

	@Override
	@Transactional
	public Problem getProblemInfo(Long id) {
		try {
			String sql = "SELECT p.id, p.number_in_contest, p.name, p.time_limit, p.memory_limit FROM problems AS p" +
					" WHERE p.id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProblemDaoImpl.ProblemRowMapper());
		} catch (EmptyResultDataAccessException e) {
			log.warn("can not get problem by id=" + id, e);
			return null;
		}
	}

	private static class ProblemIdRowMapper implements RowMapper<Problem> {
		@Override
		public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
			Problem problem = new LazyProblem(resultSet.getLong("id"));
			return problem;
		}
	}

	private static class ProblemRowMapper implements RowMapper<Problem> {
		@Override
		public Problem mapRow(ResultSet resultSet, int i) throws SQLException {
			Problem problem = new LazyProblem(resultSet.getLong("id"));
			problem.setIndex(resultSet.getLong("number_in_contest"));
			problem.setName(resultSet.getString("name"));
			problem.setTimeLimit(resultSet.getLong("time_limit"));
			problem.setMemoryLimit(resultSet.getLong("memory_limit"));
			return problem;
		}
	}

}
