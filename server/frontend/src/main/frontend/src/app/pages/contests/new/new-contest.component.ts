import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Contest} from "../../../shared/contest";
import {NgbDateStruct, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";
import {ValidationResult} from "../../../shared/validation-response";

@Component({
    selector: 'app-contest',
    templateUrl: './new-contest.component.html'
})
export class NewContestComponent {

    editMode = false;
    contestId: number;

    contest: Contest = new Contest();
    errorName: string;
    errorDuration: string;
    errorDateTime: string;
    startDate: NgbDateStruct;
    startTime: NgbTimeStruct;

    scoringSystems: string[] = ['ACM', 'KIROV'];

    validationResult: ValidationResult = ValidationResult.getEmpty();

    constructor(private eskimoService: EskimoService, private router: Router, private route: ActivatedRoute) {
        if (this.router.url.indexOf('edit') != -1) {
            this.editMode = true;
            this.contestId = +this.route.snapshot.paramMap.get('contestId');
            this.eskimoService.getContest(this.contestId).subscribe(contest => {
                this.contest = contest;
                this.parseStartTime(this.contest.startTime);
            });
        }
    }

    parseStartTime(startTime: string) {
        let dateTimeMatcher = new RegExp("^(\\d\\d)-(\\d\\d)-(\\d\\d\\d\\d) (\\d\\d):(\\d\\d):(\\d\\d)$");
        let result = dateTimeMatcher.exec(startTime);
        this.startDate = {year:Number(result[3]), month:Number(result[2]),day:Number(result[1])};
        this.startTime = {hour:Number(result[4]), minute:Number(result[5]) ,second:Number(result[6])};
    }

    validate() {
        this.validationResult = ValidationResult.getEmpty();
        if (this.contest.name == null || this.contest.name == "") {
            this.validationResult.addError("name", "Can\'t be empty");
        }
        if (this.contest.duration == null || this.contest.duration < 0 || this.contest.duration > 5256000) {
            this.validationResult.addError("duration", "Should be from 0 to 5256000");
        }
        if (this.startDate == null || this.startTime == null) {
            this.validationResult.addError("startTime", "Can't be empty");
        }
        if (this.contest.scoringSystem == null) {
            this.validationResult.addError("scoringSystem", "Can't be empty");
        }
    }

    adZeros(num) {
        var s = num + "";
        while (s.length < 2) s = "0" + s;
        return s;
    }

    onSubmit() {
        this.validate();
        if (!this.validationResult.isEmpty()) {
            return;
        }
        this.contest.startTime = this.startDate.year + '-' + this.adZeros(this.startDate.month) + '-' + this.adZeros(this.startDate.day) + 'T' + this.adZeros(this.startTime.hour) + ':' + this.adZeros(this.startTime.minute) + ':00.00Z';
        if (this.editMode) {
            this.eskimoService.editContest(this.contest).subscribe(
                () => this.router.navigateByUrl("/contests"),
                error => console.error(error)
            )
        } else {
            this.eskimoService.createContest(this.contest).subscribe(
                contest => this.router.navigateByUrl("/contests"),
                error => console.error(error)
            );
        }
    }

}
