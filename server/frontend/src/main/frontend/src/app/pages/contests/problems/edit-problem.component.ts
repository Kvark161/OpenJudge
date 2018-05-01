import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ValidationResult} from "../../../shared/validation-response";
import {EditProblemRequest} from "../../../shared/requests/edit-problem.request";

@Component({
    templateUrl: './edit-problem.component.html'
})
export class EditProblemComponent {
    contestId: number;
    problemIndex: number;

    problem: EditProblemRequest;
    checkerFiles: FileList;
    validationResult: ValidationResult = ValidationResult.getEmpty();

    error: string;

    problemEditedSuccessfully = false;
    testsEditedSuccessfully = false;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.problemIndex = +this.route.snapshot.paramMap.get('problemIndex');
        this.eskimoService.getProblemForEdit(this.contestId, this.problemIndex)
            .subscribe(problemForEdit => this.problem = problemForEdit);
    }

    onSubmit() {
        this.validate();
        if (this.validationResult.isEmpty()) {
            this.problem.checkerFile = this.checkerFiles ? this.checkerFiles[0] : null;
            this.eskimoService.editProblem(this.contestId, this.problemIndex, this.problem)
                .subscribe(validationResult => {
                    this.validationResult = ValidationResult.getEmpty();
                    this.validationResult.setErrors(validationResult.errors);
                    if (this.validationResult.isEmpty()) {
                        this.problemEditedSuccessfully = true;
                    }
                },
                    error => this.validationResult.setGeneralError(error))
        }
    }

    private validate() {
        this.validationResult = ValidationResult.getEmpty();
        if (!this.problem.timeLimit) {
            this.validationResult.addError("timeLimit", "Time limit should be set");
        } else {
            if (this.problem.timeLimit <= 0) {
                this.validationResult.addError("timeLimit", "Time limit should not be less than 1");
            }
        }
        if (!this.problem.memoryLimit) {
            this.validationResult.addError("memoryLimit", "Memory limit should be set");
        } else {
            if (this.problem.timeLimit <= 0) {
                this.validationResult.addError("memoryLimit", "Memory limit should not be less than 1");
            }
        }
        if (!this.problem.name || this.problem.name == "") {
            this.validationResult.addError("name", "Should not be empty");
        }
    }

    fieldChanged(fieldName: string) {
        this.problemEditedSuccessfully = false;
        this.testsEditedSuccessfully = false;
        this.validationResult.errors[fieldName] = null;
    }

    fileChange(event) {
        this.checkerFiles = event.target.files;
    }

    editTests() {
        this.validateTests();
        if (this.validationResult.isEmpty()) {
            this.eskimoService.editTests(this.contestId, this.problemIndex, this.problem)
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
        for (let i = 0; i < this.problem.tests.length; ++i) {
            let test = this.problem.tests[i];
            if (!test.input || test.input == "") {
                this.validationResult.addError('tests[' + i + '].input', "Should not be empty");
            }
        }
    }

    downloadChecker() {
        this.eskimoService.downloadChecker(this.contestId, this.problemIndex);
    }

    downloadStatementsPdf() {
        this.eskimoService.getStatementsPdf(this.contestId, this.problemIndex);
    }

    generalErrorPath = ValidationResult.generalErrorPath;
}
