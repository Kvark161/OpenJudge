import {Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {ContestsComponent} from "./pages/contests/contests.component";
import {NewContestComponent} from "./pages/contests/new/new-contestcomponent";
import {ContestComponent} from "./pages/contests/info/contest.component";
import {SubmitComponent} from "./pages/contests/submit/submit.component";
import {SubmissionsComponent} from "./pages/contests/submissions/submissions.component";
import {AddProblemComponent} from "./pages/contests/problems/add-problem.component";
import {ProblemsComponent} from "./pages/contests/problems/problems.component";
import {UserService} from "./services/user.service";
import {StatementsComponent} from "./pages/contests/problems/statements.component";

export const router: Routes = [
    {path: '', canActivate: [UserService], component: HomeComponent},
    {path: 'home', canActivate: [UserService], component: HomeComponent},
    {path: 'contests', canActivate: [UserService], component: ContestsComponent},
    {path: 'a/contests/new', canActivate: [UserService], component: NewContestComponent},
    {path: 'about', canActivate: [UserService], component: AboutComponent},
    {path: 'contest/:contestId', canActivate: [UserService], component: ContestComponent},
    {path: 'u/contest/:contestId/submit', canActivate: [UserService], component: SubmitComponent},
    {path: 'u/contest/:contestId/submissions', canActivate: [UserService], component: SubmissionsComponent},
    {path: 'u/contest/:contestId/problems', canActivate: [UserService], component: ProblemsComponent},
    {path: 'a/contest/:contestId/problem/add', canActivate: [UserService], component: AddProblemComponent},
    {path: 'u/contest/:contestId/problem/:problemIndex', canActivate: [UserService], component: StatementsComponent}
];
