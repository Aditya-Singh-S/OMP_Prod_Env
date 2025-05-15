import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CognitoUserPool, CognitoUser } from 'amazon-cognito-identity-js';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { environment } from '../../environment/environment';

@Component({
  selector: 'app-verify-email',
  imports: [FormsModule, CommonModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css'
})
export class VerifyEmailComponent implements OnInit {

  code: string | null = null;
  email: string | null = null;
  isVerified: boolean = false;
  errorMessage: string = '';
  infoMessage: string = '';

  ngOnInit(): void {
      const params = new URLSearchParams(window.location.search);
      this.code = params.get('code');
      this.email = localStorage.getItem('userEmail');

      if(this.code && this.email){
        this.verifyEmail(this.email, this.code);
      } else {
        this.infoMessage = 'Please check your email for verification';
      }
      console.log('Code', this.code);
      console.log('Email' , this.email);
  }


  constructor(private router: Router, private userService: UserService) {}

  verifyEmail(email:string, code: string) : void {
    const poolData = {
      UserPoolId: environment.UserPoolId,
      ClientId: environment.ClientId
    };

    const userPool = new CognitoUserPool(poolData);

    const userData = {
      Username: email,
      Pool: userPool
    };

    const cognitoUser = new CognitoUser(userData);

    cognitoUser.confirmRegistration(code, true, (err, result) => {
      if(err) {
        alert('Verification Failed: ' + err.message);
        this.errorMessage = 'Verification failed ' + err.message;
        return;
      } 
      alert('Verification successful');
      this.isVerified = true;
      this.userService.verifyEmail(email).subscribe({
        next: (response) => {
          console.log('Changed value in database:', response);
        },
        error: (err) => console.error('Changes in database failed:', err)
      });
    });
  }

  goToSignIn(): void {
    this.router.navigate(['/signin'])
  }
  
}
