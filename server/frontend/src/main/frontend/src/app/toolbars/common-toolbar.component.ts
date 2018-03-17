import {Component} from "@angular/core";
import {UserService} from "../services/user.service";
import {Router} from "@angular/router";

@Component({
    selector: 'common-toolbar',
    templateUrl: './common-toolbar.component.html',
    styleUrls: ['./common-toolbar.component.css']
})
export class CommonToolbarComponent {
    role: string = "ANONYMOUS";
    username: string;

    usernameInput: string = "";
    password: string = "";

    constructor(private userService: UserService, private router: Router) {
        this.getUsernameAndRole();
    }

    logIn() {
        this.userService.logIn(this.usernameInput, this.password)
            .subscribe(isLoggedIn => {
                    if (isLoggedIn) {
                        this.usernameInput = "";
                        this.password = "";
                        this.getUsernameAndRole();
                    }
                },
                () => {
                    this.password = "";
                }
            );
    }

    private getUsernameAndRole() {
        this.userService.getCurrentRole().subscribe(role => {
            this.role = role;
            if (role != "ANONYMOUS") {
                this.userService.getUsername().subscribe(username => this.username = username);
            }
        });
    }

    signIn() {
        this.userService.signIn(this.usernameInput, this.password)
            .subscribe(() => {
                    alert("You were successfully signed in. Now you may log in");
                },
                () => {
                    alert("Error occurred while signing in");
                });
    }

    logOut() {
        this.userService.logOut().subscribe(
            () => {
                this.role = "ANONYMOUS";
                this.username = "";
                this.router.navigateByUrl('');
            },
            () => alert("Error occurred while logging out")
        );
    }
}
