import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {Router} from "@angular/router";
import {Contest} from "../../../shared/contest";

@Component({
    selector: 'app-contest',
    templateUrl: './new-contest.component.html'
})
export class NewContestComponent {

    contest: Contest = new Contest(null, null, null, null);
    error: string;

    constructor(private eskimoService: EskimoService, private router: Router) {
    }

    onSubmit() {
        this.eskimoService.createContest(this.contest).subscribe(
            contest => this.router.navigateByUrl("/contests"),
            error => {
                let json = error.json();
                this.error = json.message;
            }
        );
    }

}
