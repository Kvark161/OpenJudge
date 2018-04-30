import {Component, Input} from "@angular/core";
import {UserService} from "../services/user.service";
import {CurrentUserInfo} from "../shared/current-user-info";

@Component({
    selector: 'contest-toolbar',
    templateUrl: './contest-toolbar.component.html',
})
export class ContestToolbarComponent {
    @Input() contestId: number;

    role: string;

    currentUserInfo: CurrentUserInfo;

    usernameInput: string = "";
    password: string = "";

    constructor(private userService: UserService) {
        this.role = userService.currentUserInfo.role;
        this.currentUserInfo = this.userService.currentUserInfo;
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
