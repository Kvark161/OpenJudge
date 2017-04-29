package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.domain.ProgrammingLanguage;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
public interface ProgrammingLanguageService {
	List<ProgrammingLanguage> getAllProgrammingLanguages();

	void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage);

	ProgrammingLanguage getProgrammingLanguage(Long id);
}
