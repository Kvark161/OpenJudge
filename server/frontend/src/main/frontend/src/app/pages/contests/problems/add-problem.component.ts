import {Component} from "@angular/core";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-submit',
    templateUrl: './add-problem.component.html'
})
export class AddProblemComponent {
    contestId: number;
    inputType: string = "Polygon zip";
    fileList: FileList;
    error: string;

    constructor(private route: ActivatedRoute, private router: Router, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
    }

    fileChange(event) {
        this.error = '';
        this.fileList = event.target.files;
    }

    onSubmit() {
        if (!this.fileList || this.fileList.length <= 0) {
            this.error = "No file chosen";
            return;
        }
        this.eskimoService.addProblems(this.contestId, this.fileList[0]).subscribe(
            () => this.router.navigateByUrl("/contest/" + this.contestId + "/problems"),
            error => {
                let json = error.json();
                this.error = json.message;
            }
        );
    }

}
