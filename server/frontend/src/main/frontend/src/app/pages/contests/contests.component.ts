import {Component} from "@angular/core";
import {EskimoService} from "../../services/eskimo.service";
import {Contest} from "../../shared/contest";

@Component({
    selector: 'app-contests',
    templateUrl: './contests.component.html'
})
export class ContestsComponent {

    contests: Contest[];

    constructor(private eskimoService: EskimoService) {
        eskimoService.getContests().subscribe(contests => this.contests = contests);
    }

}
