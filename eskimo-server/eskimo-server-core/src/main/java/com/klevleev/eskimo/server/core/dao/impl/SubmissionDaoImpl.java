package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.SubmissionDao;
import com.klevleev.eskimo.server.core.domain.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

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
	public List<Submission> getAllSubmissions() {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions";
		return jdbcTemplate.query(sql, new SubmissionRowMapper());
	}

	@Override
	public Submission getSubmissionById(Long id) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions " +
				"WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new SubmissionRowMapper(), id);
	}

	@Override
	public List<Submission> getUserSubmissions(Long userId) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict FROM submissions " +
				"WHERE user_id = ?";
		return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId);
	}

	@Override
	public void insertSubmission(Submission submission) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("submissions")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", submission.getUserId());
		params.put("contest_id", submission.getContestId());
		params.put("problem_id", submission.getProblemId());
		params.put("source_code", submission.getSourceCode());
		params.put("verdict", submission.getVerdict());
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
		submission.setId(key.longValue());
	}

	@Override
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
				submission.getUserId(),
				submission.getContestId(),
				submission.getProblemId(),
				submission.getSourceCode(),
				submission.getVerdict().name(),
				submission.getId());
	}

	private static class SubmissionRowMapper implements RowMapper<Submission> {
		@Override
		public Submission mapRow(ResultSet resultSet, int i) throws SQLException {
			Submission submission = new Submission();
			submission.setId(resultSet.getLong("id"));
			submission.setUserId(resultSet.getLong("user_id"));
			submission.setContestId(resultSet.getLong("contest_id"));
			submission.setProblemId(resultSet.getLong("problem_id"));
			submission.setSourceCode(resultSet.getString("source_code"));
			submission.setVerdict(Submission.Verdict.valueOf(resultSet.getString("verdict")));
			return submission;
		}
	}
}
