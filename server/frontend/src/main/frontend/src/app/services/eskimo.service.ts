import {Contest} from "../shared/contest";
import {Headers, Http, RequestOptions} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";
import {Problem} from "../shared/problem";
import {StatementsResponse} from "../shared/statements.response";
import {ValidationResult} from "../shared/validation-response";
import {EditProblemRequest} from "../shared/requests/edit-problem.request";
import {User} from "../shared/user";
import {CreatingResponse} from "../shared/creating-response";


@Injectable()
export class EskimoService {

    private urlHost = 'http://localhost:8080/api/';
    private urlContests = this.urlHost + 'contests';
    private urlContestCreate = this.urlHost + 'contest/create';
    private urlSubmit = this.urlHost + "contest/submit";
    private urlGetUsers = this.urlHost + "users";
    private urlCreateUser = this.urlHost + "user";

    private optionsWithCredentials = new RequestOptions({withCredentials: true});

    private getUrlContest(contestId: number) {
        return this.urlHost + "contest/" + contestId;
    }

    private getUrlProblems(contestId: number) {
        return this.getUrlContest(contestId) + "/problems";
    }

    private getUrlServerTime() {
        return this.urlHost + "server-time";
    }

    private getUrlDashboard(contestId: number) {
        return this.getUrlContest(contestId) + "/dashboard";
    }

    private getUrlUserContestSubmissions(contestId: number) {
        return this.getUrlContest(contestId) + "/submissions";
    }

    private getUrlSubmission(submissionId: number) {
        return this.urlHost + "submission/" + submissionId;
    }

    private getUrlAddProblem(contestId: number) {
        return this.getUrlContest(contestId) + "/problem/add";
    }

    private getUrlContestProblem(contestId: number, problemIndex: number) {
        return this.getUrlContest(contestId) + "/problem/" + problemIndex;
    }

    private getUrlStatements(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex);
    }

    private getUrlStatementsPdf(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/pdf";
    }
    
    private getUrlAdminProblems(contestId: number) {
        return this.getUrlContest(contestId) + "/problems/admin";
    }

    private getUrlGenerateAnswers(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/answers/generate";
    }

    private getUrlSubmitParameters(contestId: number) {
        return this.getUrlContest(contestId) + "/submitParameters";
    }

    private getUrlGetProblemForEdit(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/edit";
    }

    private getUrlEditProblem(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/edit";
    }

    private getUrlEditUser(userId: number) {
        return this.urlHost + "user/" + userId;
    }

    private getUrlCreateUsers(usersNumber) {
        return this.urlHost + "users/?usersNumber=" + usersNumber;
    }

    private getUrlEditTests(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/edit_tests";
    }

    private getUrlGetChecker(contestId: number, problemIndex: number) {
        return this.getUrlContestProblem(contestId, problemIndex) + "/checker";
    }

    private userMapper(jsonUser): User {
      return User.copyOf(jsonUser);
    }

    private userListMapper(jsonUsers) {
        let result = [];
        let users: User[] = jsonUsers;
        for (let user of users) {
            result.push(User.copyOf(user));
        }
        return result;
    }

    constructor(private http: Http) {
    }

    getContests(): Observable<Contest[]> {
        return this.http.get(this.urlContests)
            .map(res => res.json())
            .catch(this.handleError);
    }

    createContest(contest: Contest): Observable<Contest> {
        return this.http.post(this.urlContestCreate, contest, this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    addProblems(contestId: number, file: File) : Observable<void> {
        let formData: FormData = new FormData();
        formData.append('file', file, file.name);
        return this.http.post(this.getUrlAddProblem(contestId), formData, this.optionsWithCredentials)
            .catch(this.handleError);
    }

    getContest(contestId: number) : Observable<Contest> {
        return this.http.get(this.getUrlContest(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getDashboard(contestId: number): Observable<Contest> {
        return this.http.get(this.getUrlDashboard(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getProblems(contestId: number) : Observable<Problem[]> {
        return this.http.get(this.getUrlProblems(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }
    
    getAdminProblems(contestId: number) : Observable<Problem[]> {
        return this.http.get(this.getUrlAdminProblems(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    generateAnswers(contestId: number, problemIndex: number) {
        return this.http.post(this.getUrlGenerateAnswers(contestId, problemIndex), {}, this.optionsWithCredentials)
            .catch(this.handleError);
    }

    getStatements(contestId: number, problemIndex: number): Observable<StatementsResponse> {
        return this.http.get(this.getUrlStatements(contestId, problemIndex), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getStatementsPdf(contestId: number, problemIndex: number) {
        window.open(this.getUrlStatementsPdf(contestId, problemIndex));
    }

    submitProblem(contestId: number, problemId: number, sourceCode: string, selectedLanguage: number) : Observable<any> {
        return this.http.post(this.urlSubmit, {contestId: contestId, problemId: problemId, sourceCode: sourceCode,
            languageId: selectedLanguage},
            this.optionsWithCredentials);
    }

    getUserContestSubmissions(contestId: number) {
        return this.http.get(this.getUrlUserContestSubmissions(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getSubmission(submissionId: number) {
        return this.http.get(this.getUrlSubmission(submissionId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getSubmitParameters(contestId: number) {
        return this.http.get(this.getUrlSubmitParameters(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getProblemForEdit(contestId: number, problemId: number) {
        return this.http.get(this.getUrlGetProblemForEdit(contestId, problemId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getServerTime() {
        return this.http.get(this.getUrlServerTime()).map(res => res.text()).catch(this.handleError);
    }

    editProblem(contestId: number, problemId: number, problem: EditProblemRequest): Observable<ValidationResult> {
        let formData = new FormData();
        if (problem.checkerFile != null) {
            formData.append('checkerFile', problem.checkerFile, problem.checkerFile.name);
        }
        if (problem.statementsPdf != null) {
            formData.append('statementsPdf', problem.statementsPdf, problem.statementsPdf.name);
        }
        formData.append('problem', new Blob([JSON.stringify(problem)], {
            type: "application/json"
        }));

        let headers = new Headers();
        //headers.append('Content-Type', undefined);
        let options = new RequestOptions({withCredentials: true, headers: headers});
        return this.http.post(this.getUrlEditProblem(contestId, problemId),
            formData, options)
            .map(res => res.json())
            .catch(this.handleError);
    }

    editTests(contestId: number, problemId: number, problem: EditProblemRequest) {
        return this.http.post(this.getUrlEditTests(contestId, problemId), problem.tests, this.optionsWithCredentials)
            .map(res => CreatingResponse.fromJson(res.json(), this.userMapper))
            .catch(this.handleError);
    }

    deleteProblem(contestId: number, problemIndex: number) {
        return this.http.delete(this.getUrlContestProblem(contestId, problemIndex), this.optionsWithCredentials)
            .catch(this.handleError);
    }

    createUser(user: User): Observable<CreatingResponse> {
        return this.http.post(this.urlCreateUser, user, this.optionsWithCredentials)
            .map(res => CreatingResponse.fromJson(res.json(), this.userMapper))
            .catch(this.handleError);
    }

    editUser(user: User): Observable<CreatingResponse> {
        return this.http.post(this.getUrlEditUser(user.id), user, this.optionsWithCredentials)
            .map(res => CreatingResponse.fromJson(res.json(), this.userMapper))
            .catch(this.handleError);
    }

    getUsers(): Observable<User[]> {
        return this.http.get(this.urlGetUsers, this.optionsWithCredentials)
            .map(res => {
                let result: User[] = [];
                let jsonUsers: User[] = res.json();
                for (let jsonUser of jsonUsers) {
                    result.push(this.userMapper(jsonUser));
                }
                return result;
            })
            .catch(this.handleError);
    }

    createNUsers(usersNumber: number): Observable<CreatingResponse> {
        return this.http.post(this.getUrlCreateUsers(usersNumber), {}, this.optionsWithCredentials)
            .map(res => CreatingResponse.fromJson(res.json(), this.userListMapper))
            .catch(this.handleError);
    }

    downloadChecker(contestId: number, problemIndex: number) {
        window.open(this.getUrlGetChecker(contestId, problemIndex));

    }

    // noinspection JSMethodCanBeStatic
    private handleError(error: any) {
        console.error('error', error);
        return Observable.throw(error.message || error);
    }

}
