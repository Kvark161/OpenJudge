import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ValidationResult} from "../../../shared/validation-response";

@Component({
    selector: 'app-submit',
    templateUrl: './add-problem.component.html'
})
export class AddProblemComponent {
    POLYGON_ZIP_TYPE = "Polygon zip";
    CUSTOM_TYPE = "Custom";

    contestId: number;
    inputType: string = this.CUSTOM_TYPE;
    fileList: FileList;

    problemName: string;

    validationResult: ValidationResult = ValidationResult.getEmpty();

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
    }

    fileChange(event) {
        this.fieldChanged("file");
        this.fileList = event.target.files;
    }

    onSubmit() {
        if (this.inputType == this.POLYGON_ZIP_TYPE) {
            if (!this.fileList || this.fileList.length <= 0) {
                this.validationResult.addError("file", "No file chosen");
                return;
            }
            this.eskimoService.addProblems(this.contestId, this.fileList[0]).subscribe(
                () => this.router.navigateByUrl("/u/contest/" + this.contestId + "/problems"),
                error => {
                    let json = error.json();
                    this.validationResult.addError("file", json.message);
                }
            );
        } else if (this.inputType == this.CUSTOM_TYPE) {
            this.validateCustomAdd();
            if (!this.validationResult.isEmpty()) {
                return;
            }
            this.eskimoService.addProblemCustom(this.contestId, this.problemName).subscribe(
                () => this.router.navigateByUrl("/u/contest/" + this.contestId + "/problems"),
                error => {
                    let json = error.json();
                    this.validationResult.addError("problemName", json.message);
                }
            );
        } else {
            console.error("Wrong input type")
        }
    }

    private validateCustomAdd() {
        this.validationResult = ValidationResult.getEmpty();
        if (this.problemName == null || this.problemName == "") {
            this.validationResult.addError("problemName", "Should not be empty");
        } else if (this.problemName.length > 128) {
            this.validationResult.addError("problemName", "Should not be longer than 128 symbols");
        }
    }

    fieldChanged(fieldName: string) {
        this.validationResult.errors[fieldName] = null;
    }

    inputTypeChanged() {
        this.validationResult = ValidationResult.getEmpty();
    }

}
