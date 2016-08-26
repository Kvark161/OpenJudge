package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;
import com.klevleev.eskimo.server.core.domain.Submission;
import com.klevleev.eskimo.server.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by Stepan Klevleev on 15-Aug-16.
 */
@Repository("submissionDao")
public class SubmissionDaoImpl implements SubmissionDao {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public SubmissionDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public List<Submission> getAllSubmissions() {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions";
		return jdbcTemplate.query(sql, new SubmissionRowMapper());
	}

	@Override
	@Transactional
	public Submission getSubmissionById(Long id) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions " +
				"WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new SubmissionRowMapper(), id);
	}

	@Override
	@Transactional
	public List<Submission> getUserSubmissions(Long userId) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions " +
				"WHERE user_id = ?";
		return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId);
	}

	@Override
	public List<Submission> getUserSubmissions(Long userId, Long contestId) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions " +
				"WHERE user_id = ? AND contest_id = ?";
		return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId, contestId);
	}

	@Override
	@Transactional
	public void insertSubmission(Submission submission) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("submissions")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", submission.getUser().getId());
		params.put("contest_id", submission.getContest().getId());
		params.put("problem_id", submission.getProblem().getId());
		params.put("source_code", submission.getSourceCode());
		params.put("verdict", submission.getVerdict());
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
		submission.setId(key.longValue());
	}

	@Override
	@Transactional
	public void updateSubmission(Submission submission) {
		String sql = "UPDATE submissions " +
				"SET " +
				"user_id = ?, " +
				"contest_id = ?, " +
				"problem_id = ?, " +
				"source_code = ?, " +
				"verdict = ? " +
				"WHERE id = ?";
		jdbcTemplate.update(sql,
				submission.getUser().getId(),
				submission.getContest().getId(),
				submission.getProblem().getId(),
				submission.getSourceCode(),
				submission.getVerdict().name(),
				submission.getId());
	}

	private static class SubmissionRowMapper implements RowMapper<Submission> {
		@Override
		public Submission mapRow(ResultSet resultSet, int i) throws SQLException {
			Submission submission = new Submission();
			submission.setId(resultSet.getLong("id"));
			User user = new User();
			user.setId(resultSet.getLong("user_id"));
			submission.setUser(user);
			Contest contest = new Contest();
			contest.setId(resultSet.getLong("contest_id"));
			submission.setContest(contest);
			Problem problem = new Problem();
			problem.setId(resultSet.getLong("problem_id"));
			submission.setProblem(problem);
			submission.setSourceCode(resultSet.getString("source_code"));
			submission.setVerdict(Submission.Verdict.valueOf(resultSet.getString("verdict")));
			return submission;
		}
	}
}
