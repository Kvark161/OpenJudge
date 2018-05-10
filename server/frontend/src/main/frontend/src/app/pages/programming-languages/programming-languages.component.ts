import {Component} from '@angular/core';
import {EskimoService} from "../../services/eskimo.service";
import {UserService} from "../../services/user.service";

@Component({
    selector: 'programming-languages',
    templateUrl: './programming-languages.component.html'
})
export class ProgrammingLanguagesComponent {

    programmingLanguages = [];

    constructor(private eskimoService: EskimoService, private userService: UserService) {
        this.eskimoService.getProgrammingLanguages().subscribe(languages => {
            this.programmingLanguages = languages;
        });
    }
}
