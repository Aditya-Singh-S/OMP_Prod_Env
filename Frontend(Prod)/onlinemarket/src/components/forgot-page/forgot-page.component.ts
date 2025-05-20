// import { Component, OnInit } from '@angular/core';
// import { AuthService } from '../../services/auth.service';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { RecaptchaModule } from 'ng-recaptcha-2';
// import { Router, RouterModule } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { CognitoUser, CognitoUserPool } from 'amazon-cognito-identity-js';
// import { environment } from '../../environment/environment';
 
// @Component({
//   selector: 'app-forgot-page',
//   imports: [ReactiveFormsModule, RecaptchaModule, RouterModule,CommonModule],
//   standalone: true,
//   templateUrl: './forgot-page.component.html',
//   styleUrls: ['./forgot-page.component.css'],
//   providers: [AuthService]
// })
// export class ForgotPageComponent implements OnInit {
//   forgotForm!: FormGroup;
//   captchaResponse: string | null = null;
//   message: string = '';


//     email: string = '';
  
//     userPool = new CognitoUserPool({
//       UserPoolId: environment.UserPoolId,
//     ClientId: environment.ClientId
//     });

//   constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) { }

//   ngOnInit(): void {
//     this.forgotForm = this.fb.group({
//       email: ['', [Validators.required, Validators.email]],
//       captchaResponse: ['', Validators.required] 
//     });
//   }

//   onCaptchaResolved(captchaResponse: string | null) {
//     this.forgotForm.patchValue({ captchaResponse });
//     console.log('Captcha Response:', captchaResponse);
//   }

//   onSubmit() {
//     if (this.forgotForm.invalid) {
//       alert("Please fill in all required fields and verify the captcha.");
//       return;
//     }

//     const userData = {
//           Username: this.email,
//           Pool: this.userPool
//         };
    
//     const cognitoUser = new CognitoUser(userData);
//     console.log(this.email);
//     cognitoUser.forgotPassword({
//       onSuccess: function(data) {
//         console.log("Code sent successfully", data);
//         alert("Code has been sent.");
//       },
//       onFailure: function(err) {
//         console.error("Error sending code", err);
//       }
//     })
//     localStorage.setItem('forgotEmail',this.email);
//     this.router.navigate(['/verify-email']);
//   }
// }

import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RecaptchaModule } from 'ng-recaptcha-2';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CognitoUser, CognitoUserPool } from 'amazon-cognito-identity-js';
import { environment } from '../../environment/environment';

@Component({
  selector: 'app-forgot-page',
  imports: [ReactiveFormsModule, RecaptchaModule, RouterModule,CommonModule],
  standalone: true,
  templateUrl: './forgot-page.component.html',
  styleUrls: ['./forgot-page.component.css'],
  providers: [AuthService]
})
export class ForgotPageComponent implements OnInit {
  forgotForm!: FormGroup;
  captchaResponse: string | null = null;
  message: string = '';

  // New properties for the popup
  showPopup: boolean = false;
  popupTitle: string = '';
  popupMessage: string = '';


  email: string = '';
  
    userPool = new CognitoUserPool({
      UserPoolId: environment.UserPoolId,
    ClientId: environment.ClientId
    });

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      captchaResponse: ['', Validators.required] 
    });
  }

  onCaptchaResolved(captchaResponse: string | null) {
    this.forgotForm.patchValue({ captchaResponse });
    console.log('Captcha Response:', captchaResponse);
  }

  onSubmit() {
    if (this.forgotForm.invalid) {
      //alert("Please fill in all required fields and verify the captcha.");
      this.popupTitle = "Error";
      this.popupMessage = "Please fill in all required fields and verify the captcha.";
      this.showPopup = true;
      return;
    }


    const userData = {
          Username: this.email,
          Pool: this.userPool
        };
    
    const cognitoUser = new CognitoUser(userData);
    console.log(this.email);

        this.authService.forgotPassword(this.email).subscribe({
       next: (response) => {
        console.log("successful");

        
        cognitoUser.forgotPassword({
          onSuccess: function(data: any) {
            console.log("Code sent successfully", data);
            //alert("Code has been sent.");
            
          },
          onFailure: function(err: any) {
            console.error("Error sending code", err);
          }
        })

        this.router.navigate(['/verify-reset-page']);
         localStorage.setItem('forgotEmail', this.email);
         this.forgotForm.get('captchaResponse')?.setValue(null); 

      },
      error: (error) => {
        console.error("Error initiating password reset:", error);

        if (error.status === 404) {
          //alert("User not found. Please check your email address.");
          this.popupTitle = "Error";
          this.popupMessage = "User not found. Please check your email address.";
          this.showPopup = true;
          
          //window.location.reload();
        } else {
          //alert("Something went wrong. Please try again later.");
          this.popupTitle = "Error";
          this.popupMessage = "Something went wrong. Please try again later.";
          this.showPopup = true;

        }
        this.forgotForm.get('captchaResponse')?.setValue(null); 
      }})


    // cognitoUser.forgotPassword({
    //   onSuccess: function(data: any) {
    //     console.log("Code sent successfully", data);
    //     alert("Code has been sent.");

    //   },
    //   onFailure: function(err: any) {
    //     console.error("Error sending code", err);
    //   }
    // })
    // localStorage.setItem('forgotEmail',this.email);
    // this.router.navigate(['/verify-email']);

    
      //  (error: { status: any; }) => {
      //   console.error("Error:", error);
      //   if (error.status === 404) {
      //     alert("User not found");
      //   } else {
      //     alert("Something went wrong");
      //   }
      //   this.message = "";
      //   this.forgotForm.get('captchaResponse')?.setValue(null); 
      // }
    
  }

  closePopup() {
    this.showPopup = false;
  }
}