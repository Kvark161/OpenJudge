import {Contest} from "../shared/contest";
import {Http} from "@angular/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";

import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import "rxjs/add/observable/throw";

@Injectable()
export class EskimoService {

    private apiUrl = 'http://localhost:8080/api/contests';

    constructor(private http: Http) {
    }

    getContests(): Observable<Contest[]> {
        return this.http.get(this.apiUrl)
            .map(res => res.json())
            .catch(this.handleError);
    }

    private handleError(error: any) {
        console.error('errror', error);
        return Observable.throw(error.message || error);
    }

}