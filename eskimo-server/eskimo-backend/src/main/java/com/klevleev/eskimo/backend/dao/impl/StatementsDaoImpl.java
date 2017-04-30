package com.klevleev.eskimo.backend.dao.impl;

import com.klevleev.eskimo.backend.dao.StatementDao;
import com.klevleev.eskimo.backend.domain.Statement;
import com.klevleev.eskimo.backend.storage.StorageService;
import com.klevleev.eskimo.backend.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
@Repository("statementsDao")
public class StatementsDaoImpl implements StatementDao {

	private static final Logger logger = LoggerFactory.getLogger(StatementsDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private FileUtils fileUtils;

	@Autowired
	private StorageService storageService;

	@PostConstruct
	public void init() {
		int x = 2;
	}

	@Override
	public void insertStatement(Statement statement, Long contestId) {
		try {
			SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
					.withTableName("statements")
					.usingGeneratedKeyColumns("id");
			Map<String, Object> params = new HashMap<>();
			params.put("contest_id", contestId);
			params.put("language", statement.getLanguage());
			params.put("file_path", fileUtils.copyFileToFolder(statement.getFile(),
					storageService.getStatementsFolder(contestId)));
			jdbcInsert.execute(new MapSqlParameterSource(params));
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Statement> getAllStatements(Long contestId) {
		try {
			String sql = "SELECT id, language, file_path FROM statements WHERE contest_id = ?";
			return jdbcTemplate.query(sql, new Object[]{contestId}, new StatementsRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get statements by contest id = " + contestId, e);
			return null;
		}
	}

	@Override
	public Statement getStatement(Long contestId, String language) {
		try {
			String sql = "SELECT id, language, file_path FROM statements WHERE contest_id = ? AND language = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{contestId, language}, new StatementsRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get statements: contestId = " + contestId + ", language = " + language, e);
			return null;
		}
	}

	private static class StatementsRowMapper implements RowMapper<Statement> {
		@Override
		public Statement mapRow(ResultSet resultSet, int i) throws SQLException {
			Statement statement = new Statement();
			statement.setId(resultSet.getLong("id"));
			statement.setLanguage(resultSet.getString("language"));
			statement.setFile(new File(resultSet.getString("file_path")));
			return statement;
		}
	}
}
