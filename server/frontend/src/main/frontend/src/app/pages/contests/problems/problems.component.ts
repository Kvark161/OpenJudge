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
        this.userService.getCurrentRole().subscribe(role => {
            this.role = role;
            if (this.role == 'ADMIN') {
                this.getAnswersGenerationInfo();
            }
        });
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getProblems(this.contestId).subscribe(problems => this.problems = problems);
    }

    private getAnswersGenerationInfo() {
        this.eskimoService.getAnswersGenerationInfo(this.contestId).subscribe(problems => {
            let answersMap: Map<number, Problem> = new Map<number, Problem>();
            problems.forEach(p => answersMap.set(p.index, p));
            this.problems.forEach(p => {
                let answersGenerationInfo = answersMap.get(p.index);
                p.answersGenerationMessage = answersGenerationInfo.answersGenerationMessage;
                p.answersGenerationStatus = answersGenerationInfo.answersGenerationStatus;
            })
        })
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
        if (problem.answersGenerationStatus != "NOT_STARTED") {
            problem.answersGenerationStatus = "RESTARTED";
            problem.answersGenerationMessage = "";
        }
        this.eskimoService.generateAnswers(this.contestId, problem.index)
            .subscribe(
                () => console.log("successful post generate answers"),
                error => console.log(error)
            );
    }
}
