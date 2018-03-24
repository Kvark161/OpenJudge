import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Problem} from "../../../shared/problem";

@Component({
    selector: 'app-submit',
    templateUrl: './submit.component.html'
})
export class SubmitComponent {
    contestId: number;
    problems: Problem[];
    selectedProblem: number = null;
    sourceCode: string;
    errorSelectedProblem: string;
    hasErrors: boolean = false;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getProblems(this.contestId).subscribe(problems => {
            this.problems = problems
        });
    }

    validate() {
        this.errorSelectedProblem = null;
        this.hasErrors = false;
        if (this.selectedProblem == null) {
            this.errorSelectedProblem = 'Problem is not selected';
            this.hasErrors = true;
        }
    }

    onSubmit() {
        this.validate();
        if (this.hasErrors) {
            return;
        }
        this.eskimoService.submitProblem(this.contestId, this.selectedProblem, this.sourceCode)
            .subscribe(() => this.router.navigateByUrl("/contest/" + this.contestId));
    }
}
