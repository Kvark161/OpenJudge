package com.klevleev.eskimo.backend.dao;

import com.klevleev.eskimo.backend.domain.ProgrammingLanguage;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
public interface ProgrammingLanguageDao {
	List<ProgrammingLanguage> getAllProgrammingLanguages();

	void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage);

	ProgrammingLanguage getProgrammingLanguage(Long id);
}
