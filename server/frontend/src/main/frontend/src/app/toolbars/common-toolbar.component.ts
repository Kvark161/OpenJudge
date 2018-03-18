import {Component} from "@angular/core";
import {UserService} from "../services/user.service";
import {Router} from "@angular/router";
import {CurrentUserInfo} from "../shared/current-user-info";

@Component({
    selector: 'common-toolbar',
    templateUrl: './common-toolbar.component.html',
    styleUrls: ['./common-toolbar.component.css']
})
export class CommonToolbarComponent {
    currentUserInfo: CurrentUserInfo;

    usernameInput: string = "";
    password: string = "";

    constructor(private userService: UserService, private router: Router) {
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
                this.router.navigateByUrl('');
            },
            () => alert("Error occurred while logging out")
        );
    }
}
