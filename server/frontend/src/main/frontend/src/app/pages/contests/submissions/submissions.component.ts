import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Submission} from "../../../shared/submission";

@Component({
    selector: 'app-submissions',
    templateUrl: './submissions.component.html'
})
export class SubmissionsComponent {
    contestId: number;
    submissions: Submission[];

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getSubmissions(this.contestId).subscribe(submissions => this.submissions = submissions);
    }

}
