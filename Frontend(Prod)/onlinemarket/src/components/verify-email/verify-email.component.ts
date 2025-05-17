// import { Component, OnInit } from '@angular/core';
// import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { CognitoUserPool, CognitoUser } from 'amazon-cognito-identity-js';
// import { Router } from '@angular/router';
// import { UserService } from '../../services/user.service';
// import { CommonModule } from '@angular/common';
// import { environment } from '../../environment/environment';
// import { AuthService } from '../../services/auth.service';


// @Component({
//   selector: 'app-verify-email',
//   imports: [FormsModule, CommonModule, ReactiveFormsModule],
//   templateUrl: './verify-email.component.html',
//   styleUrl: './verify-email.component.css'
// })
// export class VerifyEmailComponent implements OnInit {

//   code: string | null = null;
//   // email: string='';
//   eMail:string='';
//   isVerified: boolean = false;
//   errorMessage: string = '';
//   infoMessage: string = '';
//   displayEmail : string = '';
//   verifyForm!: FormGroup;

//   ngOnInit(): void {
//       const params = new URLSearchParams(window.location.search);
//       this.code = params.get('code');
//       //this.email = localStorage.getItem('userEmail');

//       if(this.eMail){
//         this.displayEmail= this.eMail;
        
//       } else {
//         this.displayEmail = "please enter email";
//       }

//       if(this.code && this.eMail){
//         this.verifyEmail(this.eMail, this.code);
//       } else {
//         this.infoMessage = 'Please check your email for verification';
//       }
//       console.log('Code', this.code);
//       console.log('Email' , this.eMail);
//   }


//   constructor(private router: Router, private userService: UserService, private authService: AuthService){}

//   verifyEmail(email:string, code: string) : void {
//     const poolData = {
//       UserPoolId: environment.UserPoolId,
//       ClientId: environment.ClientId
//     };

//     const userPool = new CognitoUserPool(poolData);

//     const userData = {
//       Username: email,
//       Pool: userPool
//     };

//     const cognitoUser = new CognitoUser(userData);

//     cognitoUser.confirmRegistration(code, true, (err, result) => {

//       if(err) {
//         alert('Verification Failed: ' + err.message);
//         this.errorMessage = 'Verification failed ' + err.message;
//         return;
//       } 
//       alert('Verification successful');
//       this.isVerified = true;
//       this.userService.verifyEmail(email).subscribe({
//         next: (response) => {
//           console.log('Changed value in database:', response);
//         },
//         error: (err) => console.error('Changes in database failed:', err)
//       });
//     });
//   }

//   goToSignIn(): void {
//     this.router.navigate(['/signin'])
//   }

//   resendVerificationEmail(): void {

//     if (!this.eMail) {
//       this.errorMessage = 'Email not found. Please sign up again.';
//       return;
//     }

//     this.authService.resendVerificationCode(this.eMail)
//     .then(() => {
//       this.infoMessage = 'Verification email has been resent successfully. After verification, Please SignIn';
//       this.errorMessage = '';
//     })
//     .catch((err) => {
//       this.errorMessage = 'Failed to resend verification email. Please try again.';
//       console.error('Resend error:', err);
//     });
//   }

//   onSubmit(): void {

//     if (this.verifyForm.valid) {

//       const formData = new FormData();
  
//       formData.append('eMail', this.verifyForm.get('eMail')?.value || '');
//       console.log(formData);
//     }
//     // this.displayEmail = this.verifyForm.value;
//     // console.log(this.displayEmail);
//     // console.log(this.eMail);
//   }

// }

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms'; // Import FormBuilder and Validators
import { CognitoUserPool, CognitoUser } from 'amazon-cognito-identity-js';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { environment } from '../../environment/environment';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  imports: [CommonModule, ReactiveFormsModule], // Remove FormsModule
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css'
})
export class VerifyEmailComponent implements OnInit {

  code: string ='';
  eMail: string = '';
  isVerified: boolean = false;
  errorMessage: string = '';
  infoMessage: string = '';
  displayEmail: string = '';
  verifyForm!: FormGroup; // Declare as non-nullable

  constructor(
    private router: Router,
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder, // Inject FormBuilder
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.code = params['code'];
    });
    console.log('Code', this.code);

    this.verifyForm = this.fb.group({ // Initialize the form here
      email: ['', [Validators.required, Validators.email]] // Add validation if needed
    });

    // Initialize eMail from the form control or localStorage if needed
    this.eMail = localStorage.getItem('userEmail') || '';
    this.verifyForm.patchValue({ email: this.eMail }); // Set initial value in the form
    

    if (this.eMail) {
      this.displayEmail = this.eMail;
    } else {
      this.displayEmail = "please enter email";
 
    }
      this.infoMessage = 'Please check your email for verification';
    
    
    console.log('Email', this.eMail);
  }

  verifyEmail(email: string, code: string): void {
    const poolData = {
      UserPoolId: environment.UserPoolId,
      ClientId: environment.ClientId
    };

    const userPool = new CognitoUserPool(poolData);

    const userData = {
      Username: this.eMail,
      Pool: userPool
    };

    const cognitoUser = new CognitoUser(userData);

    cognitoUser.confirmRegistration(code, true, (err, result) => {
      if (err) {
        //alert('Verification Failed: ' + err.message);
        //this.errorMessage = 'Verification failed ' + err.message;
        this.errorMessage = 'Verification failed ';
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
    this.router.navigate(['/signin']);
  }

  resendVerificationEmail(): void {
    if (!this.verifyForm.get('email')?.value) { // Access email from the form
      this.errorMessage = 'Email not found. Please sign up again.';
      return;
    }

    this.authService.resendVerificationCode(this.verifyForm.get('email')?.value) 
      .then(() => {
        this.infoMessage = 'Verification email has been resent successfully. After verification, Please SignIn';
        this.errorMessage = '';
      })
      .catch((err) => {
        this.errorMessage = 'Failed to resend verification email. Please try again.';
        console.error('Resend error:', err);
      });
  }

  onSubmit(): void {
    this.eMail = this.verifyForm.get('email')?.value;
    console.log(this.code);
    console.log(this.eMail);
    console.log(this.verifyForm.get('email')?.value);
    this.verifyEmail(this.eMail, this.code);
    // if (this.verifyForm.valid) {
    //   const emailValue = this.verifyForm.get('email')?.value;
    //   console.log('Form Value (Email):', emailValue);
    //   if (this.code && this.eMail) {
        //this.verifyEmail(this.eMail, this.code);
    //   } 
    //   // You can now use the emailValue for further processing if needed
    // } else {
    //   this.errorMessage = 'Please enter a valid email address.';
    // }
  }
}
