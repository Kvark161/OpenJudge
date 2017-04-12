import {Component} from "@angular/core";
import {EskimoService} from "../../services/eskimo.service";
import {Contest} from "../../shared/contest";
import {Http} from "@angular/http";

@Component({
    selector: 'app-contests',
    templateUrl: './contests.component.html',
    styleUrls: ['./contests.component.css']
})
export class ContestsComponent {

    contests: Contest[];

    constructor(private eskimoService: EskimoService, private http: Http) {
        eskimoService.getContests().subscribe(contests => this.contests = contests);
    }

    fileChange(event) {
        let fileList: FileList = event.target.files;
        if (fileList.length > 0) {
            this.eskimoService.createContest(fileList[0]).subscribe(contest => console.log(contest));
        }
    }

}
