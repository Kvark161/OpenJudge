package eskimo.backend.services;

import eskimo.backend.dao.ProgrammingLanguageDao;
import eskimo.backend.entity.ProgrammingLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ProgrammingLanguage getProgrammingLanguage(String name) {
        return programmingLanguageDao.getProgrammingLanguage(name);
    }
}
