import {Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {AboutComponent} from "./pages/about/about.component";
import {ContestsComponent} from "./pages/contests/contests.component";
import {NewContestComponent} from "./pages/contests/new/new-contest.component";
import {ContestComponent} from "./pages/contests/info/contest.component";
import {SubmitComponent} from "./pages/contests/submit/submit.component";
import {SubmissionsComponent} from "./pages/contests/submissions/submissions.component";
import {SubmissionComponent} from "./pages/contests/submission/submission.component";
import {AddProblemComponent} from "./pages/contests/problems/add-problem.component";
import {ProblemsComponent} from "./pages/contests/problems/problems.component";
import {UserService} from "./services/user.service";
import {StatementsComponent} from "./pages/contests/problems/statements.component";
import {EditProblemComponent} from "./pages/contests/problems/edit-problem.component";
import {ManagementComponent} from "./pages/management/management.component";
import {DashboardComponent} from "./pages/contests/dashboard/dashboard.component";

export const router: Routes = [
    {path: '', canActivate: [UserService], component: HomeComponent},
    {path: 'home', canActivate: [UserService], component: HomeComponent},
    {path: 'contests', canActivate: [UserService], component: ContestsComponent},
    {path: 'a/contests/new', canActivate: [UserService], component: NewContestComponent},
    {path: 'about', canActivate: [UserService], component: AboutComponent},
    {path: 'contest/:contestId', canActivate: [UserService], component: ContestComponent},
    {path: 'u/contest/:contestId/submit', canActivate: [UserService], component: SubmitComponent},
    {path: 'u/contest/:contestId/submissions', canActivate: [UserService], component: SubmissionsComponent},
    {path: 'u/contest/:contestId/submission/:submissionId', canActivate: [UserService], component: SubmissionComponent},
    {path: 'u/contest/:contestId/problems', canActivate: [UserService], component: ProblemsComponent},
    {path: 'a/contest/:contestId/problem/add', canActivate: [UserService], component: AddProblemComponent},
    {path: 'u/contest/:contestId/problem/:problemIndex', canActivate: [UserService], component: StatementsComponent},
    {path: 'a/contest/:contestId/problem/:problemIndex/edit', canActivate: [UserService], component: EditProblemComponent},
    {path: 'a/management', canActivate: [UserService], component: ManagementComponent},
    {path: 'p/contest/:contestId/dashboard', canActivate: [UserService], component: DashboardComponent}
];
