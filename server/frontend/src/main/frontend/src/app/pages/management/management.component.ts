import {Component} from "@angular/core";
import {User} from "../../shared/user";
import {EskimoService} from "../../services/eskimo.service";
import {UserService} from "../../services/user.service";
import {ValidationResult} from "../../shared/validation-response";

@Component({
    selector: 'app-management',
    templateUrl: './management.component.html'
})
export class ManagementComponent {

    users: User[];
    currentUser = this.userService.currentUserInfo;

    newUser: User = new User();
    validationResult: ValidationResult = ValidationResult.getEmpty();

    constructor(private eskimoService: EskimoService, private userService: UserService) {
        this.eskimoService.getUsers().subscribe(users => {
            this.users = users;
        });
    }

    deleteUser(user: User) {
        this.eskimoService.deleteUser(user.id).subscribe(() => {
            let index = this.users.indexOf(user);
            this.users.splice(index, 1);
        });
    }

    onCreateUser() {
        this.validateNewUser();
        if (!this.validationResult.isEmpty()) {
            return;
        }
        this.eskimoService.createUser(this.newUser).subscribe((objectWithVr) => {
            this.validationResult = ValidationResult.getEmpty();
            this.validationResult.setErrors(objectWithVr.validationResult);
            if (this.validationResult.isEmpty()) {
                this.newUser = new User();
                this.users.push(objectWithVr.createdObject);
            }
        });
    }

    private validateNewUser() {
        this.validationResult = ValidationResult.getEmpty();
        let username = this.newUser.username;
        if (!username || username.length < 1 || username.length > 128) {
            this.validationResult
                .addError("username", "Name should be not empty and not greater than 128 symbols");
        } else if (!/^[\d\w]+$/.test(username)) {
            this.validationResult
                .addError("username", "Name should contain only latin letters and digits");
        }
        let password = this.newUser.password;
        if (!password || password.length < 1 || password.length > 128) {
            this.validationResult
                .addError("password", "Password should be not empty and not greater than 128 symbols");
        } else if (!/^[\d\w]+$/.test(password)) {
            this.validationResult
                .addError("password", "Password should contain only latin letters and digits");
        }
    }

    changePasswordVisibility(user: User, val: boolean) {
        user.passwordVisible = val;
    }

    fieldChanged(fieldName: string) {
        this.validationResult.errors[fieldName] = null;
    }
}
