import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {RouterModule} from "@angular/router";

import {router} from "./app.router";
import {EskimoService} from "./services/eskimo.service";
import {ContestsComponent} from "./pages/contests/contests.component";
import {AppComponent} from "./app.component";
import {AboutComponent} from "./pages/about/about.component";
import {HomeComponent} from "./pages/home/home.component";
import {NewContestComponent} from "./pages/contests/new/new-contestcomponent";
import {CommonToolbarComponent} from "./toolbars/common-toolbar.component";
import {ContestToolbarComponent} from "./toolbars/contest-toolbar.component";
import {ContestComponent} from "./pages/contests/info/contest.component";
import {SubmitComponent} from "./pages/contests/submit/submit.component";
import {SubmissionsComponent} from "./pages/contests/submissions/submissions.component";
import {ProblemsComponent} from "./pages/contests/problems/problems.component";

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(router)
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
        ProblemsComponent
    ],
    providers: [EskimoService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
