import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";

import {AppComponent} from "./app.component";
import {AboutComponent} from "./pages/about/about.component";
import {HomeComponent} from "./pages/home/home.component";
import {router} from "./app.router";
import {RouterModule} from "@angular/router";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {EskimoService} from "./services/eskimo.service";
import {ContestsComponent} from "./pages/contests/contests.component";


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        RouterModule.forRoot(router),
        NgbModule
    ],
    declarations: [
        AppComponent,
        AboutComponent,
        HomeComponent,
        ContestsComponent
    ],
    providers: [EskimoService],
    bootstrap: [AppComponent]
})
export class AppModule {
}
