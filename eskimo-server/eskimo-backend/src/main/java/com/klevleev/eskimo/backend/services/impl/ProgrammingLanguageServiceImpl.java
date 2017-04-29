package com.klevleev.eskimo.backend.services.impl;

import com.klevleev.eskimo.backend.dao.ProgrammingLanguageDao;
import com.klevleev.eskimo.backend.domain.ProgrammingLanguage;
import com.klevleev.eskimo.backend.services.ProgrammingLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Sokirkina Ekaterina on 29-Apr-2017.
 */
@Service("programmingLanguageService")
public class ProgrammingLanguageServiceImpl implements ProgrammingLanguageService {

	private final ProgrammingLanguageDao programmingLanguageDao;

	@Autowired
	public ProgrammingLanguageServiceImpl(ProgrammingLanguageDao programmingLanguageDao) {
		this.programmingLanguageDao = programmingLanguageDao;
	}

	@Override
	public List<ProgrammingLanguage> getAllProgrammingLanguages() {
		return programmingLanguageDao.getAllProgrammingLanguages();
	}

	@Override
	public void insertProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
		programmingLanguageDao.insertProgrammingLanguage(programmingLanguage);
	}

	@Override
	public ProgrammingLanguage getProgrammingLanguage(Long id) {
		return programmingLanguageDao.getProgrammingLanguage(id);
	}
}
