import {Component} from "@angular/core";
import {StatementsResponse} from "../../../shared/statements.response";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-statements',
    styleUrls: ['./statements.component.css'],
    templateUrl: './statements.component.html'
})
export class StatementsComponent {
    contestId: number;
    problemIndex: number;
    statements: StatementsResponse;
    memoryUnits: string = "";

    constructor(private route: ActivatedRoute, private eskimoService: EskimoService){
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.problemIndex = +this.route.snapshot.paramMap.get('problemIndex');
        eskimoService.getStatements(this.contestId, this.problemIndex, "")
            .subscribe(statements => {
                if (!statements.error || statements.error == '') {
                    if (statements.memoryLimit < 1024) {
                        this.memoryUnits = "bytes";
                    } else if (statements.memoryLimit < 1024*1024) {
                        statements.memoryLimit /= 1024;
                        this.memoryUnits = "kilobytes";
                    } else {
                        statements.memoryLimit /= 1024 * 1024;
                        this.memoryUnits = "megabytes";
                    }
                }
                this.statements = statements;

            });
    }
}
