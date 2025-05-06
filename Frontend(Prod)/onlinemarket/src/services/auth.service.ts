
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private loginUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/login';
    private generateResetLinkUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/generate-reset-link';
    private apiUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/reset-password';

    private loggedInSource = new BehaviorSubject<boolean>(false);
    loggedIn$ = this.loggedInSource.asObservable();

    constructor(private http: HttpClient) {}

    login(email: string, password: string): Observable<any> {
        return this.http.post<any>(this.loginUrl, { email, password });
    }

    forgotPassword(email: string): Observable<string> {
        const params = new HttpParams().set('email', email);
        return this.http.post(this.generateResetLinkUrl, {}, { params, responseType: 'text' });
    }

    resetPassword(payload: any): Observable<string> { 
        return this.http.post(this.apiUrl, payload, { responseType: 'text' }); 
    }
}