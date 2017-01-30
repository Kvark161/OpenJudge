package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.ContestDao;
import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sokirkina Ekaterina on 27-Dec-2016.
 */
@Repository("contestDao")
public class ContestDaoImpl implements ContestDao {

	private static final Logger logger = LoggerFactory.getLogger(ContestDaoImpl.class);

	private final JdbcTemplate jdbcTemplate;
	private Storage storage;

	@Autowired
	public ContestDaoImpl(JdbcTemplate jdbcTemplate, Storage storage) {
		this.jdbcTemplate = jdbcTemplate;
		Assert.notNull(storage, "storage is null in ContestDaoImpl");
		this.storage = storage;
	}

	@Override
	@Transactional
	public List<Contest> getAllContests() {
		String sql = "SELECT c.id, c.name, c.start_time, c.duration_in_minutes FROM contests AS c";
		return jdbcTemplate.query(sql, new ContestDaoImpl.ContestRowMapper());
	}

	@Override
	@Transactional
	public Contest insertContest(File contestDirectory) {
		ParseInfo parseInfo = null;
		try {
			parseInfo = storage.parseContest(contestDirectory);
			StorageContest storageContest = parseInfo.getStorageContest();
			SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
					.withTableName("contests")
					.usingGeneratedKeyColumns("id");
			Map<String, Object> params = new HashMap<>();
			params.put("name", storageContest.getName());
			Long contestId = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
			insertProblems(storageContest.getProblems(), contestId);
			storage.insertContest(parseInfo, contestId);
			return getContestById(contestId);
		} catch (StorageException e){
			if (parseInfo != null)
				storage.removeFailParsedContest(parseInfo);
			throw new RuntimeException("Transaction rollback because of storage exception ", e);
		}
	}

	@Transactional
	private void insertProblems(List<StorageProblem> problems, Long contestId){
		long i = 1;
		for (StorageProblem problem : problems){
			SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
					.withTableName("problems")
					.usingGeneratedKeyColumns("id");
			Map<String, Object> params = new HashMap<>();
			params.put("contest_id", contestId);
			params.put("number_in_contest", i);
			params.put("name", problem.getName());
			params.put("time_limit", problem.getTimeLimit());
			params.put("memory_limit", problem.getMemoryLimit());
			params.put("tests_count", problem.getTestCount());
			jdbcInsert.execute(new MapSqlParameterSource(params));
			++i;
		}
	}

	@Override
	@Transactional
	public Contest getContestById(long id) {
		try {
			String sql = "SELECT c.id, c.name, c.start_time, c.duration_in_minutes FROM contests AS c WHERE c.id = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ContestDaoImpl.ContestRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get contest by id=" + id, e);
			return null;
		}
	}

	@Override
	@Transactional
	public boolean contestExists(long id) {
		String sql = "SELECT id FROM contests WHERE id = ?";
		return jdbcTemplate.queryForList(sql, new Object[]{id}).size() > 0;
	}

	@Override
	@Transactional
	public InputStream getStatements(Long contestId) {
		return storage.getStatements(contestId);
	}

	private static class ContestRowMapper implements RowMapper<Contest> {
		@Override
		public Contest mapRow(ResultSet resultSet, int i) throws SQLException {
			Contest contest = new Contest();
			contest.setId(resultSet.getLong("id"));
			contest.setName(resultSet.getString("name"));
			Timestamp startTime = resultSet.getTimestamp("start_time");
			if (startTime != null)
				contest.setStartTime(startTime.toLocalDateTime());
			else
				contest.setStartTime(null);
			contest.setDuration(resultSet.getInt("duration_in_minutes"));
			return contest;
		}
	}
}
