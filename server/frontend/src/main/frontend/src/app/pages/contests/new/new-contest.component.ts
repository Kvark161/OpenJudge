import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {Router} from "@angular/router";
import {Contest} from "../../../shared/contest";
import {NgbDateStruct, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'app-contest',
    templateUrl: './new-contest.component.html'
})
export class NewContestComponent {

    contest: Contest = new Contest(null, null, null, null);
    errorName: string;
    errorDuration: string;
    errorDateTime: string;
    startDate: NgbDateStruct;
    startTime: NgbTimeStruct;

    constructor(private eskimoService: EskimoService, private router: Router) {
    }

    validate() {
        this.errorName = null;
        this.errorDuration = null;
        this.errorDateTime = null;
        let result = true;
        if (this.contest.name == null || this.contest.name == "") {
            this.errorName = 'Cant be empty';
            result = false;
        }
        if (this.contest.duration == null || this.contest.duration < 0 || this.contest.duration > 5256000) {
            this.errorDuration = 'Should be from 0 to 5256000';
            result = false;
        }
        if (this.startDate == null || this.startTime == null) {
            this.errorDateTime = 'Cant be empty';
            result = false;
        }
        return result;
    }

    adZeros(num) {
        var s = num + "";
        while (s.length < 2) s = "0" + s;
        return s;
    }

    onSubmit() {
        if (this.validate() == false) {
            return;
        }
        this.contest.startTime = this.startDate.year + '-' + this.adZeros(this.startDate.month) + '-' + this.adZeros(this.startDate.day) + 'T' + this.adZeros(this.startTime.hour) + ':' + this.adZeros(this.startTime.minute) + ':00.00Z';
        this.eskimoService.createContest(this.contest).subscribe(
            contest => this.router.navigateByUrl("/contests"),
            error => {
            }
        );
    }

}
