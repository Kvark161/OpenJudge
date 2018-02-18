import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Problem} from "../../../shared/problem";

@Component({
    selector: 'app-submit',
    templateUrl: './problems.component.html'
})
export class ProblemsComponent {
    contestId: number;
    problems: Problem[];

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getProblems(this.contestId).subscribe(problems => this.problems = problems);
    }

    openStatements() {
        this.eskimoService.openStatements(this.contestId);
    }
}
