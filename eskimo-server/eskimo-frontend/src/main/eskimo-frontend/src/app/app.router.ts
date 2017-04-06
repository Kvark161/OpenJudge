import {Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {ContestsComponent} from "./pages/contests/contests.component";

export const router: Routes = [
    {path: '', component: HomeComponent},
    {path: 'home', component: HomeComponent},
    {path: 'contests', component: ContestsComponent},
    {path: 'about', component: AboutComponent}
];
