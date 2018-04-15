import {AceEditorModule} from 'ng2-ace-editor';
import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";

import {router} from "./app.router";
import {EskimoService} from "./services/eskimo.service";
import {ContestsComponent} from "./pages/contests/contests.component";
import {AppComponent} from "./app.component";
import {AboutComponent} from "./pages/about/about.component";
import {HomeComponent} from "./pages/home/home.component";
import {NewContestComponent} from "./pages/contests/new/new-contest.component";
import {CommonToolbarComponent} from "./toolbars/common-toolbar.component";
import {ContestToolbarComponent} from "./toolbars/contest-toolbar.component";
import {ContestComponent} from "./pages/contests/info/contest.component";
import {SubmitComponent} from "./pages/contests/submit/submit.component";
import {SubmissionsComponent} from "./pages/contests/submissions/submissions.component";
import {SubmissionComponent} from "./pages/contests/submission/submission.component";
import {AddProblemComponent} from "./pages/contests/problems/add-problem.component";
import {ProblemsComponent} from "./pages/contests/problems/problems.component";
import {UserService} from "./services/user.service";
import {StatementsComponent} from "./pages/contests/problems/statements.component";
import {InformationModalComponent} from "./pages/modal_dialogs/information-modal.component";
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDialogModule} from "@angular/material";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {EditProblemComponent} from "./pages/contests/problems/edit-problem.component";
import {Angular2FontawesomeModule} from "angular2-fontawesome";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DashboardComponent} from "./pages/contests/dashboard/dashboard.component";

@NgModule({
    imports: [
        AceEditorModule,
        Angular2FontawesomeModule,
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        MatDialogModule,
        RouterModule.forRoot(router),
        NgbModule.forRoot()
    ],
    declarations: [
        AppComponent,
        AboutComponent,
        HomeComponent,
        ContestsComponent,
        NewContestComponent,
        CommonToolbarComponent,
        ContestToolbarComponent,
        ContestComponent,
        SubmitComponent,
        SubmissionsComponent,
        SubmissionComponent,
        ProblemsComponent,
        AddProblemComponent,
        StatementsComponent,
        InformationModalComponent,
        EditProblemComponent,
        DashboardComponent
    ],
    entryComponents: [
        InformationModalComponent
    ],
    providers: [EskimoService, UserService,
        {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}}],
    bootstrap: [AppComponent]
})
export class AppModule {
}
