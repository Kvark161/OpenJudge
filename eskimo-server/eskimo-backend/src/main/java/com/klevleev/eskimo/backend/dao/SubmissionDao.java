package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Contest;
import com.klevleev.eskimo.backend.domain.Problem;
import com.klevleev.eskimo.backend.domain.Submission;
import com.klevleev.eskimo.backend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stepan Klevleev on 15-Aug-16.
 */
@Repository
public class SubmissionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public List<Submission> getAllSubmissions() {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict, sending_date_time, test_number " +
				"FROM submissions";
		return jdbcTemplate.query(sql, new SubmissionRowMapper());
	}

	@Transactional
	public Submission getSubmissionById(Long id) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict, sending_date_time, test_number " +
				"FROM submissions " +
				"WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new SubmissionRowMapper(), id);
	}

	@Transactional
	public List<Submission> getUserSubmissions(Long userId) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict, sending_date_time, test_number " +
				"FROM submissions " +
				"WHERE user_id = ?";
		return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId);
	}

	public List<Submission> getUserSubmissions(Long userId, Long contestId) {
		String sql = "SELECT id, user_id, contest_id, problem_id, source_code, verdict, sending_date_time, test_number " +
				"FROM submissions " +
				"WHERE user_id = ? AND contest_id = ?";
		return jdbcTemplate.query(sql, new SubmissionRowMapper(), userId, contestId);
	}

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
		params.put("sending_date_time", Timestamp.valueOf(submission.getSendingDateTime()));
		params.put("test_number", submission.getTestNumber());
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params));
		submission.setId(key.longValue());
	}

	@Transactional
	public void updateSubmission(Submission submission) {
		String sql = "UPDATE submissions " +
				"SET " +
				"user_id = ?, " +
				"contest_id = ?, " +
				"problem_id = ?, " +
				"source_code = ?, " +
				"verdict = ? ," +
				"sending_date_time = ? ," +
				"test_number = ? " +
				"WHERE id = ?";
		jdbcTemplate.update(sql,
				submission.getUser().getId(),
				submission.getContest().getId(),
				submission.getProblem().getId(),
				submission.getSourceCode(),
				submission.getVerdict().name(),
				Timestamp.valueOf(submission.getSendingDateTime()),
				submission.getTestNumber(),
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
			submission.setSendingDateTime(resultSet.getTimestamp("sending_date_time").toLocalDateTime());
			submission.setTestNumber(resultSet.getLong("test_number"));
			return submission;
		}
	}
}
