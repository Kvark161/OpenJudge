import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-contest',
    templateUrl: './new-contest.component.html'
})
export class NewContestComponent {

    error: string;
    fileList: FileList;

    constructor(private eskimoService: EskimoService, private router: Router) {
    }


    fileChange(event) {
        this.error = null;
        this.fileList = event.target.files;
    }

    onSubmit() {
        if (this.fileList.length <= 0) {
            this.error = "No file chosen";
            return;
        }
        this.eskimoService.createContest(this.fileList[0]).subscribe(
            contest => this.router.navigateByUrl("/contests"),
            error => {
                let json = error.json();
                this.error = json.message;
            }
        );
    }

}
