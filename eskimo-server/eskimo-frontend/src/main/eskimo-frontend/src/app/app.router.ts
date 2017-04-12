import {Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {ContestsComponent} from "./pages/contests/contests.component";
import {NewContestComponent} from "./pages/contests/new/new-contestcomponent";

export const router: Routes = [
    {path: '', component: HomeComponent},
    {path: 'home', component: HomeComponent},
    {path: 'contests', component: ContestsComponent},
    {path: 'contests/new', component: NewContestComponent},
    {path: 'about', component: AboutComponent}
];
