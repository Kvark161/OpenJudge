package eskimo.backend.rest;

import eskimo.backend.entity.ProgrammingLanguage;
import eskimo.backend.entity.enums.Role;
import eskimo.backend.rest.annotations.AccessLevel;
import eskimo.backend.services.ProgrammingLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
