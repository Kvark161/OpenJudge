import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";

@Component({
    selector: 'information-dialog',
    templateUrl: 'information-modal.component.html',
})
export class InformationModalComponent {
    constructor(public dialogRef: MatDialogRef<InformationModalComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any) {
    }

    close() {
        this.dialogRef.close();
    }
}
