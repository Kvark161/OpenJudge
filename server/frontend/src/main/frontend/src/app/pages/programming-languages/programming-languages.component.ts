import {Component} from '@angular/core';
import {EskimoService} from "../../services/eskimo.service";
import {UserService} from "../../services/user.service";
import {ProgrammingLanguage} from "../../shared/programming-language";

@Component({
    selector: 'programming-languages',
    templateUrl: './programming-languages.component.html'
})
export class ProgrammingLanguagesComponent {

    role: string;
    programmingLanguages: ProgrammingLanguage[] = [];

    constructor(private eskimoService: EskimoService, private userService: UserService) {
        this.role = userService.currentUserInfo.role;
        this.eskimoService.getProgrammingLanguages().subscribe(languages => {
            this.programmingLanguages = languages;
        });
    }
}
