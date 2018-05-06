import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Problem} from "../../../shared/problem";
import {UserService} from "../../../services/user.service";
import {InformationModalComponent} from "../../modal_dialogs/information-modal.component";
import {MatDialog} from "@angular/material";

@Component({
    selector: 'app-submit',
    templateUrl: './problems.component.html',
    styleUrls: ['./problems.component.css']
})
export class ProblemsComponent {
    role: string;
    contestId: number;
    problems: Problem[];

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService,
                private userService: UserService, private dialog: MatDialog) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.role = this.userService.currentUserInfo.role;
        if (this.role == 'ADMIN') {
            this.eskimoService.getAdminProblems(this.contestId).subscribe(problems => this.problems = problems);
        } else {
            this.eskimoService.getProblems(this.contestId).subscribe(problems => this.problems = problems);
        }
    }

    showAnswersGenerationMessage(problem: Problem) {
        this.dialog.open(InformationModalComponent, {
            data: {
                title: "Answers generation status message",
                info: problem.answersGenerationMessage
            }
        });
    }

    generateAnswers(problem: Problem) {
        this.eskimoService.generateAnswers(this.contestId, problem.index)
            .subscribe(
                () => console.log("successful post generate answers"),
                error => console.log(error)
            );
    }

    hideProblem(problemIndex: number) {
        this.eskimoService.hideProblem(this.contestId, this.problems[problemIndex].index)
            .subscribe(() => {
                this.problems[problemIndex].hidden = true;
            });
    }

    showProblem(problemIndex: number) {
        this.eskimoService.showProblem(this.contestId, this.problems[problemIndex].index)
            .subscribe(() => {
                this.problems[problemIndex].hidden = false;
            });
    }
}
