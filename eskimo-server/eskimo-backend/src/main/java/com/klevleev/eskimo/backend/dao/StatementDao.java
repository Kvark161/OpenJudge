package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.Statement;

import java.util.List;


/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public interface StatementDao {

	void insertStatement(Statement statement, Long contestId);

	List<Statement> getAllStatements(Long id);

	Statement getStatement(Long contestId, String language);

}
