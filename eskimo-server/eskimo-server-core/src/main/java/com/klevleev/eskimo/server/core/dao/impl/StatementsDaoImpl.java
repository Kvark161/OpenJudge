package com.klevleev.eskimo.server.core.dao.impl;

import com.klevleev.eskimo.server.core.dao.StatementsDao;
import com.klevleev.eskimo.server.core.domain.Statements;
import com.klevleev.eskimo.server.core.utils.FileUtils;
import com.klevleev.eskimo.server.core.utils.StorageNamesGenerator;
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
public class StatementsDaoImpl implements StatementsDao {

	private static final Logger logger = LoggerFactory.getLogger(StatementsDaoImpl.class);

	private final JdbcTemplate jdbcTemplate;

	private final FileUtils fileUtils;

	private final StorageNamesGenerator storageNamesGenerator;

	@Autowired
	public StatementsDaoImpl(JdbcTemplate jdbcTemplate,
	                         FileUtils fileUtils,
	                         StorageNamesGenerator storageNamesGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.fileUtils = fileUtils;
		this.storageNamesGenerator = storageNamesGenerator;
	}

	@Override
	@Transactional
	public void insertStatements(Statements statements, Long contestId) {
		try {
			SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
					.withTableName("statements")
					.usingGeneratedKeyColumns("id");
			Map<String, Object> params = new HashMap<>();
			params.put("contest_id", contestId);
			params.put("language", statements.getLanguage());
			params.put("file_path", fileUtils.copyFileToFolder(statements.getFilePath(),
					storageNamesGenerator.getStatementsFolder(contestId)));
			jdbcInsert.execute(new MapSqlParameterSource(params));
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Statements> getAllStatements(Long contestId) {
		try {
			String sql = "SELECT id, language, file_path FROM statements WHERE contest_id = ?";
			return jdbcTemplate.query(sql, new Object[]{contestId}, new StatementsRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get statements by contest id = " + contestId, e);
			return null;
		}
	}

	@Override
	@Transactional
	public Statements getStatements(Long contestId, String language) {
		try {
			String sql = "SELECT id, language, file_path FROM statements WHERE contest_id = ? AND language = ?";
			return jdbcTemplate.queryForObject(sql, new Object[]{contestId, language}, new StatementsRowMapper());
		} catch (EmptyResultDataAccessException e) {
			logger.warn("can not get statements: contestId = " + contestId + ", language = " + language, e);
			return null;
		}
	}

	private static class StatementsRowMapper implements RowMapper<Statements> {
		@Override
		public Statements mapRow(ResultSet resultSet, int i) throws SQLException {
			Statements statements = new Statements();
			statements.setId(resultSet.getLong("id"));
			statements.setLanguage(resultSet.getString("language"));
			statements.setFilePath(new File(resultSet.getString("file_path")));
			return statements;
		}
	}
}
