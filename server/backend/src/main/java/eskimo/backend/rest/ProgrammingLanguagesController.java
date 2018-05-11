package eskimo.backend.rest;

import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.services.ProgrammingLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class ProgrammingLanguagesController {

    @Autowired
    private ProgrammingLanguageService programmingLanguageService;

    @GetMapping("programming-languages")
    @AccessLevel(role = Role.ADMIN)
    public List<ProgrammingLanguage> getProgrammingLanguages() {
        return programmingLanguageService.getAllProgrammingLanguages();
    }

    @GetMapping("programming-language/{id}")
    @AccessLevel(role = Role.ADMIN)
    public ProgrammingLanguage getProgrammingLanguage(@PathVariable("id") Long id) {
        return programmingLanguageService.getProgrammingLanguage(id);
    }

    @PostMapping("programming-language")
    public void addProgrammingLanguage(@RequestBody ProgrammingLanguage programmingLanguage) {
        programmingLanguageService.insertProgrammingLanguage(programmingLanguage);
    }

    @PostMapping("programming-language/{id}")
    public void editProgrammingLanguage(@RequestBody ProgrammingLanguage programmingLanguage) {
        programmingLanguageService.editProgrammingLanguage(programmingLanguage);
    }

}
