import {Contest} from "../shared/contest";
import {Http, RequestOptions} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";
import {Problem} from "../shared/problem";

@Injectable()
export class EskimoService {

    private urlHost = 'http://localhost:8080/api/';
    private urlContests = this.urlHost + 'contests';
    private urlContestCreate = this.urlHost + 'contest/create';
    private urlSubmit = this.urlHost + "contest/submit";

    private optionsWithCredentials = new RequestOptions({withCredentials: true});

    private getUrlContest(contestId: number) {
        return this.urlHost + "contest/" + contestId;
    }

    private getUrlProblems(contestId: number) {
        return this.urlHost + "contest/" + contestId + "/problems";
    }

    private getUrlSubmissions(contestId: number) {
        return this.urlHost + "contest/" + contestId + "/submissions";
    }

    private getUrlAddProblem(contestId: number) {
        return this.urlHost + "contest/" + contestId + "/problem/add";
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

    getProblems(contestId: number) : Observable<Problem[]> {
        return this.http.get(this.getUrlProblems(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    submitProblem(contestId: number, problemId: number, sourceCode: string) : Observable<any> {
        return this.http.post(this.urlSubmit, {contestId: contestId, problemId: problemId, sourceCode: sourceCode},
            this.optionsWithCredentials);
    }

    getSubmissions(contestId: number) {
        return this.http.get(this.getUrlSubmissions(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    // noinspection JSMethodCanBeStatic
    private handleError(error: any) {
        console.error('error', error);
        return Observable.throw(error.message || error);
    }

}
