import {Component, ViewChild} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Problem} from "../../../shared/problem";
import {ProgrammingLanguage} from "../../../shared/programming-language";

@Component({
    selector: 'app-submit',
    templateUrl: './submit.component.html'
})
export class SubmitComponent {
    @ViewChild('editor') editor;
    contestId: number;
    problems: Problem[];
    languages: ProgrammingLanguage[];
    selectedProblem: number = null;
    sourceCode: string;
    selectedLanguage: number = null;
    errorSelectedProblem: string;
    errorSelectedLanguage: string;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getSubmitParameters(this.contestId).subscribe(submitParameters => {
            this.problems = submitParameters.problems;
            this.languages = submitParameters.languages;
        });
    }

    problemChanged() {
        this.errorSelectedProblem = null;
    }

    languageChanged() {
        this.errorSelectedLanguage = null;
    }

    validate() {
        this.errorSelectedProblem = null;
        this.errorSelectedLanguage = null;
        if (this.selectedProblem == null) {
            this.errorSelectedProblem = 'Problem is not selected';
        }
        if (this.selectedLanguage == null) {
            this.errorSelectedLanguage = 'Language is not selected';
        }
    }

    private hasErrors() {
        return this.errorSelectedProblem != null || this.errorSelectedLanguage != null;
    }

    onSubmit() {
        this.sourceCode = this.editor.getEditor().getValue();
        this.validate();
        if (this.hasErrors()) {
            return;
        }
        this.eskimoService.submitProblem(this.contestId, this.selectedProblem, this.sourceCode, this.selectedLanguage)
            .subscribe(() => this.router.navigateByUrl("/contest/" + this.contestId));
    }
}
