import {Component} from '@angular/core';
import {EskimoService} from "../../services/eskimo.service";
import {UserService} from "../../services/user.service";
import {ProgrammingLanguage} from "../../shared/programming-language";
import {ActivatedRoute, Router} from "@angular/router";
import {ValidationResult} from "../../shared/validation-response";
import {Utils} from "../../utils/utils";

@Component({
    selector: 'edit-programming-language',
    templateUrl: './edit-programming-language.html'
})
export class EditProgrammingLanguage {

    editMode: boolean = false;

    langId:number;
    language: ProgrammingLanguage = new ProgrammingLanguage();

    validationResult: ValidationResult = ValidationResult.getEmpty();

    constructor(private eskimoService: EskimoService, private userService: UserService, private route: ActivatedRoute,
                private router: Router) {
        if (this.router.url.indexOf('edit') != -1) {
            this.editMode = true;
            this.langId = +this.route.snapshot.paramMap.get('langId');
            eskimoService.getProgrammingLanguage(this.langId).subscribe(lang => this.language = lang);
        }
    }

    onSubmit() {
        this.validate();
        if (!this.validationResult.isEmpty()) {
            return;
        }
        if (this.editMode) {
            this.eskimoService.editProgrammingLanguage(this.language).subscribe(
                () => this.router.navigateByUrl("/a/programming-languages"),
                error => console.error(error)
            );
        } else {
            this.eskimoService.addProgrammingLanguage(this.language).subscribe(
                () => this.router.navigateByUrl("/a/programming-languages"),
                error => console.error(error)
            );
        }
    }

    private validate() {
        this.validationResult = ValidationResult.getEmpty();
        Utils.validateNotEmptyAndNotLarger(this.language.name, "name", 128, this.validationResult);
        Utils.validateNotEmptyAndNotLarger(this.language.description, "description", 256, this.validationResult);
        Utils.validateEmptyOrNotLarger(this.language.compilerPath, "compilerPath", 4096, this.validationResult);
        Utils.validateNotNull(this.language.isCompiled, "isCompiled", this.validationResult);
        Utils.validateEmptyOrNotLarger(this.language.interpreterPath, "interpreterPath", 4096, this.validationResult);
        Utils.validateNotEmptyAndNotLarger(this.language.extension, "extension", 10, this.validationResult);
        Utils.validateNotEmptyAndNotLarger(this.language.binaryExtension, "binaryExtension", 10, this.validationResult);
        Utils.validateEmptyOrNotLarger(this.language.compileCommand, "compileCommand", 4096, this.validationResult);
        Utils.validateNotEmptyAndNotLarger(this.language.runCommand, "runCommand", 4096, this.validationResult);
        Utils.validateNotNull(this.language.compilationMemoryLimit, "compilationMemoryLimit", this.validationResult);
        Utils.validateNotNull(this.language.compilationTimeLimit, "compilationTimeLimit", this.validationResult);
    }

    fieldChanged(fieldName: string) {
        this.validationResult.errors[fieldName] = null;
    }
}
