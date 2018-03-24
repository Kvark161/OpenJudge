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
    errorName: string;

    constructor(private eskimoService: EskimoService, private router: Router) {
    }

    onSubmit() {
        this.errorName = '';
        if (this.contest.name == null || this.contest.name == "") {
            this.errorName = 'Cant be empty';
            return;
        }
        this.eskimoService.createContest(this.contest).subscribe(
            contest => this.router.navigateByUrl("/contests"),
            error => {
            }
        );
    }

}
