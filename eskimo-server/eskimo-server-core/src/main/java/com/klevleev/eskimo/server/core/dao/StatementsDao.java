package com.klevleev.eskimo.server.core.dao;

import com.klevleev.eskimo.server.core.domain.Statements;

import java.util.List;


/**
 * Created by Sokirkina Ekaterina on 03-Feb-2017.
 */
public interface StatementsDao {

	void insertStatements(Statements statements, Long contestId);

	List<Statements> getAllStatements(Long id);

	Statements getStatements(Long contestId, String language);

}
