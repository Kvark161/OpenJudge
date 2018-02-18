import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {Contest} from "../../../shared/contest";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-contest',
    templateUrl: './contest.component.html'
})
export class ContestComponent {
    contestId: number;
    contest: Contest = new Contest(null, null, null, null);

    constructor(private route: ActivatedRoute, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        eskimoService.getContest(this.contestId).subscribe(contest => this.contest = contest);
    }
}
