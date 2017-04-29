package com.klevleev.eskimo.backend.dao.impl;

import com.klevleev.eskimo.backend.dao.ProgrammingLanguageDao;
import com.klevleev.eskimo.backend.domain.ProgrammingLanguage;
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
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
@Repository("programmingLanguageDao")
public class ProgrammingLanguageDaoImpl implements ProgrammingLanguageDao {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public ProgrammingLanguageDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<ProgrammingLanguage> getAllProgrammingLanguages() {
		String sql = "SELECT id, name, description FROM programming_languages";
		return jdbcTemplate.query(sql, new ProgrammingLanguageRowMapper());
	}

	@Override
	public void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("programming_languages")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> params = new HashMap<>();
		params.put("name", programmingLanguage.getName());
		params.put("description", programmingLanguage.getDescription());
		jdbcInsert.execute(new MapSqlParameterSource(params));
	}

	@Override
	public ProgrammingLanguage getProgrammingLanguage(Long id) {
		String sql = "SELECT id, name, description FROM programming_languages WHERE id = ?";
		return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProgrammingLanguageRowMapper());
	}

	private static class ProgrammingLanguageRowMapper implements RowMapper<ProgrammingLanguage> {

		@Override
		public ProgrammingLanguage mapRow(ResultSet resultSet, int i) throws SQLException {
			ProgrammingLanguage programmingLanguage = new ProgrammingLanguage();
			programmingLanguage.setId(resultSet.getLong("id"));
			programmingLanguage.setName(resultSet.getString("name"));
			programmingLanguage.setDescription(resultSet.getString("description"));
			return programmingLanguage;
		}
	}
}
