import {Component} from "@angular/core";
import {StatementsResponse} from "../../../shared/responses/statements.response";
import {EskimoService} from "../../../services/eskimo.service";
import {ActivatedRoute} from "@angular/router";
import {Utils} from "../../../utils/utils";

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

    notFound: boolean = false;

    constructor(private route: ActivatedRoute, private eskimoService: EskimoService) {
        this.contestId = +this.route.snapshot.paramMap.get('contestId');
        this.problemIndex = +this.route.snapshot.paramMap.get('problemIndex');
        eskimoService.getStatements(this.contestId, this.problemIndex)
            .subscribe(statements => {
                    this.statements = statements;
                    if (!statements.error || statements.error == '') {
                        let optimized = Utils.getMemoryInOptimalUnits(statements.memoryLimit);
                        this.statements.memoryLimit = optimized.count;
                        this.memoryUnits = optimized.units;
                    }
                },
                error => {
                    if (error.status == 404) {
                        this.notFound  = true;
                    }
                });
    }

    openProblemPdf() {
        this.eskimoService.getStatementsPdf(this.contestId, this.problemIndex);
    }

}
