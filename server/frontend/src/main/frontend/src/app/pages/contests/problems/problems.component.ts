import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Problem} from "../../../shared/problem";
import {UserService} from "../../../services/user.service";

@Component({
    selector: 'app-submit',
    templateUrl: './problems.component.html'
})
export class ProblemsComponent {
    role: string;
    contestId: number;
    problems: Problem[];

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService,
                private userService: UserService) {
        this.userService.getCurrentRole().subscribe(role => this.role = role);
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getProblems(this.contestId).subscribe(problems => this.problems = problems);
    }
}
