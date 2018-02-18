import {Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {ContestsComponent} from "./pages/contests/contests.component";
import {NewContestComponent} from "./pages/contests/new/new-contestcomponent";
import {ContestComponent} from "./pages/contests/info/contest.component";
import {SubmitComponent} from "./pages/contests/submit/submit.component";

export const router: Routes = [
    {path: '', component: HomeComponent},
    {path: 'home', component: HomeComponent},
    {path: 'contests', component: ContestsComponent},
    {path: 'contests/new', component: NewContestComponent},
    {path: 'about', component: AboutComponent},
    {path: 'contest/:contestId', component: ContestComponent},
    {path: 'contest/:contestId/submit', component: SubmitComponent}
];
