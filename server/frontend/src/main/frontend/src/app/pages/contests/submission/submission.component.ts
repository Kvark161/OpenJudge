import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Submission} from "../../../shared/submission";

@Component({
    selector: 'app-submission',
    templateUrl: './submission.component.html'
})
export class SubmissionComponent {
    submissionId: number;
    submission: Submission = new Submission();

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.submissionId = +this.route.snapshot.paramMap.get('submissionId');
        this.eskimoService.getSubmission(this.submissionId).subscribe(submission => this.submission = submission);
    }

}
