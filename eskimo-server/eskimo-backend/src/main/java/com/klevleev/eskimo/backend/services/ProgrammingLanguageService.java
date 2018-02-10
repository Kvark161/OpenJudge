package com.klevleev.eskimo.backend.services;

import com.klevleev.eskimo.backend.dao.ProgrammingLanguageDao;
import com.klevleev.eskimo.backend.domain.ProgrammingLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
@Service
public class ProgrammingLanguageService {

	private final ProgrammingLanguageDao programmingLanguageDao;

	@Autowired
	public ProgrammingLanguageService(ProgrammingLanguageDao programmingLanguageDao) {
		this.programmingLanguageDao = programmingLanguageDao;
	}

	public List<ProgrammingLanguage> getAllProgrammingLanguages() {
		return programmingLanguageDao.getAllProgrammingLanguages();
	}

	public void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
		programmingLanguageDao.insertProgrammingLanguage(programmingLanguage);
	}

	public ProgrammingLanguage getProgrammingLanguage(Long id) {
		return programmingLanguageDao.getProgrammingLanguage(id);
	}
}