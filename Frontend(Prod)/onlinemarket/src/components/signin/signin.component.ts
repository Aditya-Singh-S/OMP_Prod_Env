import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { RecaptchaModule } from 'ng-recaptcha-2';
import { RouterModule, Router } from '@angular/router';
import { ISigninResponse, IUserIdResponse } from '../../model/class/interface/Products';
import { CookieServiceService } from '../../services/cookie-service.service';
import { UserService } from '../../services/user.service';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RecaptchaModule, RouterModule],
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css'],
  providers : [CookieServiceService,UserService]
})
export class SigninComponent implements OnInit {
  signInForm!: FormGroup;
  signInResponse: ISigninResponse[] = [];
  userEmailId: string = '';
  @Output() loginSuccess = new EventEmitter<void>();

  constructor(private fb: FormBuilder, private userService: UserService, private authService: AuthService, private cookieService: CookieServiceService, private router: Router) {}

  ngOnInit() {
    this.signInForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      captchaResponse: ['']
    });
  }

  onCaptchaResolved(captchaResponse: string | null) {
    this.signInForm.patchValue({ captchaResponse });
    console.log('Captcha Response:', captchaResponse);
  }

  signIn() {
    if (this.signInForm.invalid) {
      alert("Please fill in all required fields.");
      return;
    }

    const { email, password } = this.signInForm.value;

    if (!this.signInForm.value.captchaResponse) {
      alert("Please verify that you are not a robot.");
      return;
    }

    console.log("Submitting login request...");
    console.log("Email:", email);
    console.log("Password:", password);


    this.authService.signIn(email, password).then(result => {
      console.log("User signed in:", result);
      alert("Login successful!");

      this.userEmailId = email;
      this.loginSuccess.emit(); // Emit event on successful login
      this.userService.handleLoginSuccess(this.userEmailId);

      this.router.navigate(['/home'])
    }).catch(err => {
      console.error('Login failed: ', err);
      alert("Error: "+ err.message);
    });
  }
}
