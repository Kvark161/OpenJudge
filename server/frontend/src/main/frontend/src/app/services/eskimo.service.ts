import {Contest} from "../shared/contest";
import {Http} from "@angular/http";
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
    private urlContestCreate = this.urlHost + 'contest/create/from/zip';
    private urlSubmit = this.urlHost + "contest/submit";

    private getUrlContest(contestId: number) {
        return this.urlHost + "contest/" + contestId;
    }

    private getUrlProblems(contestId: number) {
        return this.urlHost + "contest/" + contestId + "/problems";
    }

    private getUrlSubmissions(contestId: number) {
        return this.urlHost + "contest/" + contestId + "/submissions";
    }

    constructor(private http: Http) {
    }

    getContests(): Observable<Contest[]> {
        return this.http.get(this.urlContests)
            .map(res => res.json())
            .catch(this.handleError);
    }

    createContest(file: File): Observable<Contest> {
        let formData: FormData = new FormData();
        formData.append('file', file, file.name);
        return this.http.post(this.urlContestCreate, formData)
            .map(res => res.json())
            .catch(this.handleError);
    }

    getContest(contestId: number) : Observable<Contest> {
        return this.http.get(this.getUrlContest(contestId))
            .map(res => res.json())
            .catch(this.handleError);
    }

    getProblems(contestId: number) : Observable<Problem[]> {
        return this.http.get(this.getUrlProblems(contestId))
            .map(res => res.json())
            .catch(this.handleError);
    }

    submitProblem(contestId: number, problemId: number, sourceCode: string) : Observable<any> {
        return this.http.post(this.urlSubmit, {contestId: contestId, problemId: problemId, sourceCode: sourceCode});
    }

    getSubmissions(contestId: number) {
        return this.http.get(this.getUrlSubmissions(contestId))
            .map(res => res.json())
            .catch(this.handleError);
    }

    // noinspection JSMethodCanBeStatic
    private handleError(error: any) {
        console.error('errror', error);
        return Observable.throw(error.message || error);
    }

}
