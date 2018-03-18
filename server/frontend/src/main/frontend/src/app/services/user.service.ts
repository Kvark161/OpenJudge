import {Injectable} from "@angular/core";
import {Http, RequestOptions} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {CurrentUserInfo} from "../shared/current-user-info";

@Injectable()
export class UserService implements CanActivate {

    private urlHost = 'http://localhost:8080/api/';
    private urlCurrentUser = this.urlHost + "current-user";
    private urlLogIn = this.urlHost + "log-in";
    private urlLogOut = this.urlHost + "log-out";

    public currentUserInfo = new CurrentUserInfo();

    private optionsWithCredentials = new RequestOptions({withCredentials: true});

    constructor(private http: Http, private router: Router) {
    }

    /**
     * Determine whether some page should be opened by current user.
     * If user has no rights on this page - redirect to home.
     */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise((resolve, reject) => {
            this.getCurrentUserInfo().subscribe(userInfo => {
                this.currentUserInfo = userInfo;
                let role = this.currentUserInfo.role;
                if ((role == 'ADMIN' || role == 'USER') && state.url.indexOf('login') > -1) {
                    this.router.navigate(['']);
                    resolve(false);
                } else if (role == 'USER' && state.url.indexOf('/a/') > -1) {
                    this.router.navigate(['']);
                    resolve(false);
                } else if (role == 'ANONYMOUS'
                    && (state.url.indexOf('/a/') > -1 || state.url.indexOf('/u/') > -1)) {
                    this.router.navigate(['']);
                    resolve(false);
                } else {
                    resolve(true);
                }
            })
        });
    }

    getCurrentUserInfo() {
        return this.http.get(this.urlCurrentUser, this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError)
    }

    logIn(username: string, password: string): Observable<boolean> {
        return this.http.post(this.urlLogIn, {username: username, password: password}, this.optionsWithCredentials)
            .map(res => res.json())
            .catch(this.handleError);
    }

    logOut(): Observable<void> {
        this.currentUserInfo = new CurrentUserInfo();
        return this.http.get(this.urlLogOut, this.optionsWithCredentials)
            .catch(this.handleError);
    }

    private handleError(error: any) {
        console.error('error', error);
        return Observable.throw(error.message || error);
    }
}
