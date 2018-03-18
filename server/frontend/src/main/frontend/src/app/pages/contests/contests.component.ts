import {Component} from "@angular/core";
import {EskimoService} from "../../services/eskimo.service";
import {Contest} from "../../shared/contest";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'app-contests',
    templateUrl: './contests.component.html'
})
export class ContestsComponent {

    role: string = "ANONYMOUS";
    contests: Contest[];

    constructor(private eskimoService: EskimoService, private userService: UserService) {
        this.role = userService.currentUserInfo.role;
        eskimoService.getContests().subscribe(contests => this.contests = contests);
    }

}
