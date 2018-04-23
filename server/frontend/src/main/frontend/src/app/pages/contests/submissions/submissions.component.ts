import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Submission} from "../../../shared/submission";

@Component({
    selector: 'app-submissions',
    templateUrl: './submissions.component.html',
    styleUrls: ['submissions.component.css']
})
export class SubmissionsComponent {

    Statuses = {
        'SUBMITTED': {
            'color': 'black',
            'name': 'Submitted'
        },
        'PENDING': {
            'color': 'black',
            'name': 'Pending'
        },
        'COMPILING': {
            'color': 'black',
            'name': 'Compiling'
        },
        'RUNNING': {
            'color': 'black',
            'name': 'Running'
        },
        'COMPILATION_ERROR': {
            'color': 'darkblue',
            'name': 'Compilation error'
        },
        'COMPILATION_SUCCESS': {
            'color': 'black',
            'name': 'Compilation success'
        },
        'ACCEPTED': {
            'color': 'green',
            'name': 'Accepted'
        },
        'WRONG_ANSWER': {
            'color': 'darkblue',
            'name': 'Wrong answer'
        },
        'PRESENTATION_ERROR': {
            'color': 'darkblue',
            'name': 'Presentation error'
        },
        'RUNTIME_ERROR': {
            'color': 'darkblue',
            'name': 'Runtime error'
        },
        'TIME_LIMIT_EXCEED': {
            'color': 'darkblue',
            'name': 'Time limit exceed'
        },
        'INTERNAL_ERROR': {
            'color': 'red',
            'name': 'Internal error'
        }
    };

    contestId: number;
    submissions: Submission[];

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.eskimoService.getUserContestSubmissions(this.contestId).subscribe(submissions => this.submissions = submissions);
    }

    getStatusColor(status: string) {
        return this.Statuses[status]['color'];
    }

    getStatusShowName(submission: Submission) {
        let status = submission.status;
        let result = this.Statuses[status].name;
        if (status == 'WRONG_ANSWER' || status == 'PRESENTATION_ERROR' || status == 'RUNTIME_ERROR' || status == 'TIME_LIMIT_EXCEED') {
            result += ' on test ' + (submission.passedTests + 1);
        }
        return result;
    }

}
