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

    private newUser: User = new User();
    validationResult: ValidationResult = ValidationResult.getEmpty();

    formUser: User = this.newUser;
    isAdminChecked = false;
    editing: boolean = false;

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

    onSubmit() {
        this.validateFormUser();
        if (!this.validationResult.isEmpty()) {
            return;
        }
        if (this.editing) {
            this.setFormUserRole();
            this.eskimoService.editUser(this.formUser).subscribe((objectWithVr) => {
                this.validationResult = ValidationResult.getEmpty();
                this.validationResult.setErrors(objectWithVr.validationResult.errors);
                if (this.validationResult.isEmpty()) {
                    let editedUserIndex = this.users.findIndex(user => user.id == objectWithVr.changedObject.id);
                    this.users[editedUserIndex] = objectWithVr.changedObject;
                    this.newUser = new User();
                    this.formUser = this.newUser;
                    this.editing = false;
                }
            });
        } else {
            this.setFormUserRole();
            this.eskimoService.createUser(this.newUser).subscribe((objectWithVr) => {
                this.validationResult = ValidationResult.getEmpty();
                this.validationResult.setErrors(objectWithVr.validationResult.errors);
                if (this.validationResult.isEmpty()) {
                    this.users.push(objectWithVr.changedObject);
                    this.newUser = new User();
                    this.formUser = this.newUser;
                }
            });
        }
    }

    private setFormUserRole() {
        this.formUser.role = this.isAdminChecked ? "ADMIN" : "USER";
    }

    private validateFormUser() {
        this.validationResult = ValidationResult.getEmpty();
        let username = this.formUser.username;
        if (!username || username.length < 1 || username.length > 128) {
            this.validationResult
                .addError("username", "Name should be not empty and not greater than 128 symbols");
        } else if (!/^[\d\w]+$/.test(username)) {
            this.validationResult
                .addError("username", "Name should contain only latin letters and digits");
        }
        let password = this.formUser.password;
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

    editUser(user: User) {
        this.formUser = User.copyOf(user);
        this.isAdminChecked = user.isAdmin();
        this.editing = true;
    }

    backToCreating() {
        this.formUser = this.newUser;
        this.editing = false;
    }
}
