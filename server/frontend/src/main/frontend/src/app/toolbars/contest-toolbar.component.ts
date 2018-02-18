import {Component, Input} from "@angular/core";

@Component({
    selector: 'contest-toolbar',
    templateUrl: './contest-toolbar.component.html',
})
export class ContestToolbarComponent {
    @Input() contestId: number;
}
