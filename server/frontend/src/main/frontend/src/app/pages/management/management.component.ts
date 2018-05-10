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

    userNumber: number;

    validationResult: ValidationResult = ValidationResult.getEmpty();

    formUser: User;
    isAdminChecked = false;
    editingIndex: number = null;

    constructor(private eskimoService: EskimoService, private userService: UserService) {
        this.eskimoService.getUsers().subscribe(users => {
            this.users = users;
        });
    }

    createNUsers() {
        if (!this.userNumber || this.userNumber < 1 || this.userNumber > 100) {
            this.validationResult.addError("userNumber", "Should be between 1 and 100");
        }
        this.eskimoService.createNUsers(this.userNumber)
            .subscribe((objectWithVr) => {
                this.validationResult = ValidationResult.getEmpty();
                this.validationResult.setErrors(objectWithVr.validationResult.errors);
                if (this.validationResult.isEmpty()) {
                    this.userNumber = 0;
                    this.users = this.users.concat(objectWithVr.changedObject);
                }
            })
    }

    submitUserChanges() {
        this.validateFormUser();
        if (!this.validationResult.isEmpty()) {
            return;
        }
        this.setFormUserRole();
        this.eskimoService.editUser(this.formUser).subscribe((objectWithVr) => {
            this.validationResult = ValidationResult.getEmpty();
            this.validationResult.setErrors(objectWithVr.validationResult.errors);
            if (this.validationResult.isEmpty()) {
                this.users[this.editingIndex] = objectWithVr.changedObject;
                this.formUser = null;
                this.editingIndex = null;
            }
        });
    }

    cancelEditing() {
        this.editingIndex = null;
        this.formUser = null;
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
        let name = this.formUser.name;
        if (!name || name == "" || !name.replace(/\s/g, '').length) {
            this.validationResult.addError("name", "Name should not be empty");
        }
    }

    changePasswordVisibility(user: User, val: boolean) {
        user.passwordVisible = val;
    }

    fieldChanged(fieldName: string) {
        this.validationResult.errors[fieldName] = null;
    }

    editUser(index: number) {
        this.formUser = User.copyOf(this.users[index]);
        this.isAdminChecked = this.formUser.isAdmin();
        this.editingIndex = index;
    }
}
