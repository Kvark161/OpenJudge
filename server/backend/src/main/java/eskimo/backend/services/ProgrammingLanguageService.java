package eskimo.backend.services;

import eskimo.backend.dao.ProgrammingLanguageDao;
import eskimo.backend.entity.ProgrammingLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        programmingLanguageDao.insert(programmingLanguage);
    }

    public void editProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        programmingLanguageDao.edit(programmingLanguage);
    }

    public ProgrammingLanguage getProgrammingLanguage(Long id) {
        return programmingLanguageDao.getProgrammingLanguage(id);
    }

    public ProgrammingLanguage getProgrammingLanguage(String name) {
        return programmingLanguageDao.getProgrammingLanguage(name);
    }

    public ProgrammingLanguage getProgrammingLanguageByExtension(String extension) {
        List<ProgrammingLanguage> languages = getAllProgrammingLanguages();
        for (ProgrammingLanguage language : languages) {
            if (language.getExtension().equals(extension)) {
                return language;
            }
        }
        return null;
    }

    public ProgrammingLanguage insert(String name) {
        ProgrammingLanguage programmingLanguage = new ProgrammingLanguage();
        programmingLanguage.setName(name);
        programmingLanguage.setCompilationMemoryLimit(5242880);
        programmingLanguage.setCompilationMemoryLimit(30000);
        programmingLanguage.setBinaryExtension("exe");
        programmingLanguage.setCompiled(true);
        programmingLanguage.setDescription(name);
        programmingLanguage.setCompileCommand(new ArrayList<>());
        programmingLanguage.setRunCommand(new ArrayList<>());
        return programmingLanguageDao.insert(programmingLanguage);
    }
}
