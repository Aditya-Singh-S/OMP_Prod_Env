
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { CognitoUser, CognitoUserPool, AuthenticationDetails, CognitoUserAttribute, CognitoUserSession } from 'amazon-cognito-identity-js';
import { environment } from '../environment/environment';

const poolData = {
    UserPoolId: environment.UserPoolId,
    ClientId: environment.ClientId
};

const userPool = new CognitoUserPool(poolData);


@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private http: HttpClient) {}

    signUp(email: string, password: string): Promise<any> {
        
        const attributeList: CognitoUserAttribute[] = [];

        const dataEmail = {
            Name: 'email',
            Value: email
        };
        
        const attributeEmail = new CognitoUserAttribute(dataEmail);
        attributeList.push(attributeEmail);

        return new Promise((resolve,reject) => {
            userPool.signUp(email, password, [], null!, (err, result) => {
                if (err) reject(err);
                else resolve(result);
            });
        });
    }


    signIn(email: string, password: string): Promise<CognitoUserSession> {
        const authenticationDetails = new AuthenticationDetails({Username: email, Password: password});

        const userData = {
            Username: email,
            Pool: userPool
        };

        const cognitoUser = new CognitoUser(userData);

        return new Promise((resolve, reject) => {
            cognitoUser.authenticateUser(authenticationDetails, {onSuccess: session => resolve(session),
                onFailure: err => reject(err)
            });
        });
    }


    signOut(): void {
        const user = userPool.getCurrentUser();
        if(user) user.signOut();
    }

    //private loginUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/login';
    private generateResetLinkUrl = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/generate-reset-link';
    private reset = 'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/reset-password';
    


    private loggedInSource = new BehaviorSubject<boolean>(false);
    loggedIn$ = this.loggedInSource.asObservable();

    

    // login(email: string, password: string): Observable<any> {
    //     return this.http.post<any>(this.loginUrl, { email, password });
    // }

    forgotPassword(email: string): Observable<string> {
        const params = new HttpParams().set('email', email);
        return this.http.post(this.generateResetLinkUrl, {}, { params, responseType: 'text' });
    }

    resetPassword(email: string, newPassword: string, confirmPassword: string): Observable<string> { 
        const params = new HttpParams()
          .set('email', email)
          .set('newPassword', newPassword)
          .set('confirmPassword', confirmPassword);

        return this.http.post<string>(this.reset, null, { params, responseType: 'text' as 'json' });
    }

    resendVerificationCode(email: string): Promise<any> {
        // const poolData = {
        //   UserPoolId: environment.UserPoolId,
        //   ClientId: environment.ClientId
        // };
     
        const userPool = new CognitoUserPool(poolData);
     
        const userData = {
          Username: email,
          Pool: userPool
        };
     
        const cognitoUser = new CognitoUser(userData);
     
        return new Promise((resolve, reject) => {
          cognitoUser.resendConfirmationCode((err, result) => {
            if (err) {
              reject(err);
            } else {
              resolve(result);
            }
          });
        });
      }
}