import {Component, Input} from "@angular/core";
import {UserService} from "../services/user.service";

@Component({
    selector: 'contest-toolbar',
    templateUrl: './contest-toolbar.component.html',
})
export class ContestToolbarComponent {
    @Input() contestId: number;

    role: string;

    constructor(private userService: UserService) {
        this.role = userService.currentUserInfo.role;
    }
}
