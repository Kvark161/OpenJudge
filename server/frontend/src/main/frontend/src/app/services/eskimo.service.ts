import {Contest} from "../shared/contest";
import {Http, RequestOptions, URLSearchParams} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";
import {Problem} from "../shared/problem";
import {StatementsResponse} from "../shared/statements.response";


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
        return this.getUrlContest(contestId) + "/problems";
    }

    private getUrlContestSubmissions(contestId: number) {
        return this.getUrlContest(contestId) + "/submissions";
    }

    private getUrlSubmission(submissionId: number) {
        return this.urlHost + "submission/" + submissionId;
    }

    private getUrlAddProblem(contestId: number) {
        return this.getUrlContest(contestId) + "/problem/add";
    }

    private getUrlStatements(contestId: number, problemIndex: number) {
        return this.getUrlContest(contestId) + "/problem/" + problemIndex;
    }
    
    private getUrlAdminProblems(contestId: number) {
        return this.getUrlContest(contestId) + "/problems/admin";
    }

    private getUrlGenerateAnswers(contestId: number, problemIndex: number) {
        return this.getUrlContest(contestId) + "/problem/" + problemIndex + "/answers/generate";
    }

    private getUrlSubmitParameters(contestId: number) {
        return this.getUrlContest(contestId) + "/submitParameters";
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
    
    getAdminProblems(contestId: number) : Observable<Problem[]> {
        return this.http.get(this.getUrlAdminProblems(contestId), this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    generateAnswers(contestId: number, problemIndex: number) {
        return this.http.post(this.getUrlGenerateAnswers(contestId, problemIndex), {}, this.optionsWithCredentials)
            .catch(this.handleError);
    }

    getStatements(contestId: number, problemIndex: number, language: string): Observable<StatementsResponse> {
        let params = new URLSearchParams();
        params.set('language', language);
        let options = this.optionsWithCredentials;
        options.params = params;
        return this.http.get(this.getUrlStatements(contestId, problemIndex), options)
            .map(res => res.json())
            .catch(this.handleError);
    }

    submitProblem(contestId: number, problemId: number, sourceCode: string, selectedLanguage: number) : Observable<any> {
        return this.http.post(this.urlSubmit, {contestId: contestId, problemId: problemId, sourceCode: sourceCode,
            languageId: selectedLanguage},
            this.optionsWithCredentials);
    }

    getContestSubmissions(contestId: number) {
        return this.http.get(this.getUrlContestSubmissions(contestId), this.optionsWithCredentials)
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

    // noinspection JSMethodCanBeStatic
    private handleError(error: any) {
        console.error('error', error);
        return Observable.throw(error.message || error);
    }

}
