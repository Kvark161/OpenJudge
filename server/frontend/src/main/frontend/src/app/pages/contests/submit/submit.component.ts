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
    selectedProblem: number;
    sourceCode: string;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getProblems(this.contestId).subscribe(problems => this.problems = problems);
    }

    onSubmit() {
        this.eskimoService.submitProblem(this.contestId, this.selectedProblem, this.sourceCode)
            .subscribe(o => this.router.navigateByUrl("/contest/" + this.contestId));
    }
}
