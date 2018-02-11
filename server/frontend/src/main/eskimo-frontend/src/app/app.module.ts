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
        NewContestComponent
    ],
    providers: [EskimoService],
    bootstrap: [AppComponent]
})
export class AppModule {
}