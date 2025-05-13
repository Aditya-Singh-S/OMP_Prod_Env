
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RecaptchaModule } from 'ng-recaptcha-2';
import { CognitoUser, CognitoUserPool } from 'amazon-cognito-identity-js';

@Component({
    selector: 'app-reset',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RecaptchaModule, RouterModule, FormsModule],
    templateUrl: './reset.component.html',
    styleUrls: ['./reset.component.css'],
    providers : [AuthService]
})
export class ResetComponent implements OnInit {
    email: string | null = localStorage.getItem('forgotEmail');;
    code: string = '';


    onCaptchaResolved($event: string|null) {
        throw new Error('Method not implemented.');
    }

    newPassword = '';
    confirmPassword = '';
    //verificationCode = '';


    poolData = {
        UserPoolId: 'us-east-1_AxB7iszWL',
        ClientId: '417s4cdtb16h13q86gd9en55jb'
      };
      
    constructor(private route: ActivatedRoute, private router : Router) {}

    ngOnInit(): void {
        this.route.queryParams.subscribe((params) => {
            this.code = params['code'];
        })
    }
      

codeTouched: any;

    resetPassword(): void {
        if(!this.email){
            alert("Email not found");
            return;
        }

        if(this.newPassword !== this.confirmPassword){
            alert('Passwords do not match');
            return;
        }


        const userPool = new CognitoUserPool(this.poolData);

        const userData = {
            Username: this.email,
            Pool: userPool
        };

        const cognitoUser = new CognitoUser(userData);

        cognitoUser.confirmPassword(this.code, this.newPassword, {
            onSuccess: () => {alert('Password reset successful');
                localStorage.removeItem('forgotEmail');
            },
            onFailure: (err) => {
                console.error('Reset failed', err);
            }
        })
        this.router.navigate(['/signin']);
    }
}
