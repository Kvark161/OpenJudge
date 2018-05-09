import {Component, Input, OnInit} from "@angular/core";
import {UserService} from "../services/user.service";
import {CurrentUserInfo} from "../shared/current-user-info";
import {EskimoService} from "../services/eskimo.service";
import {Contest} from "../shared/contest";

@Component({
    selector: 'contest-toolbar',
    templateUrl: './contest-toolbar.component.html',
})
export class ContestToolbarComponent implements OnInit {
    @Input() contestId: number;

    role: string;

    currentUserInfo: CurrentUserInfo;

    usernameInput: string = "";
    password: string = "";
    contest: Contest = new Contest(null, null, null, null);

    constructor(private userService: UserService, private eskimoService: EskimoService) {
        this.role = userService.currentUserInfo.role;
        this.currentUserInfo = this.userService.currentUserInfo;
    }

    ngOnInit() {
        this.eskimoService.getContest(this.contestId).subscribe(contest => this.contest = contest);
    }


    logIn() {
        this.userService.logIn(this.usernameInput, this.password)
            .subscribe(isLoggedIn => {
                    if (isLoggedIn) {
                        window.location.reload();
                    }
                },
                () => {
                    this.password = "";
                }
            );
    }

    logOut() {
        this.userService.logOut().subscribe(
            () => {
                window.location.reload();
            },
            () => alert("Error occurred while logging out")
        );
    }
}
