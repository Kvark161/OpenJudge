import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ValidationResult} from "../../../shared/validation-response";
import {Test} from "../../../shared/test";

@Component({
    templateUrl: './edit-tests.component.html'
})
export class EditTestsComponent {
    contestId: number;
    problemIndex: number;

    tests: Test[] = [];
    validationResult: ValidationResult = ValidationResult.getEmpty();

    error: string;

    testsEditedSuccessfully = false;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.problemIndex = +this.route.snapshot.paramMap.get('problemIndex');
        this.eskimoService.getTestsForEdit(this.contestId, this.problemIndex)
            .subscribe(tests => this.tests = tests);
    }

    fieldChanged(fieldName: string) {
        this.testsEditedSuccessfully = false;
        this.validationResult.errors[fieldName] = null;
    }

    editTests() {
        this.validateTests();
        if (this.validationResult.isEmpty()) {
            this.eskimoService.editTests(this.contestId, this.problemIndex, this.tests)
                .subscribe(validationResult => {
                        this.validationResult = ValidationResult.getEmpty();
                        this.validationResult.setErrors(validationResult.errors);
                        if (this.validationResult.isEmpty()) {
                            this.testsEditedSuccessfully = true;
                        }
                    },
                    error => this.validationResult.setGeneralError(error))
        }
    }

    private validateTests() {
        this.validationResult = ValidationResult.getEmpty();
        for (let i = 0; i < this.tests.length; ++i) {
            let test = this.tests[i];
            if (!test.input || test.input == "") {
                this.validationResult.addError('tests[' + i + '].input', "Should not be empty");
            }
        }
    }

    generalErrorPath = ValidationResult.generalErrorPath;
}
