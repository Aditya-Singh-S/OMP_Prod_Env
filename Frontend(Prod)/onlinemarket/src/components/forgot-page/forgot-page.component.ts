import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RecaptchaModule } from 'ng-recaptcha-2';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
 
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
 
  constructor(private fb: FormBuilder, private authService: AuthService) { }
 
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
      alert("Please fill in all required fields and verify the captcha.");
      return;
    }
 
    const email = this.forgotForm.value.email;
 
    console.log("Requesting reset link for:", email);
 
    this.authService.forgotPassword(email).subscribe({
      next: (resetLink: string) => {
        console.log("Reset Link Generated:", resetLink);
        alert("Reset link has been generated in the console.");
        this.message = "";
        this.forgotForm.reset();
      },
      error: (error) => {
        console.error("Error:", error);
        if (error.status === 404) {
          alert("User not found");
        } else {
          alert("Something went wrong");
        }
        this.message = "";
        this.forgotForm.get('captchaResponse')?.setValue(null);
      }
    });
  }
}
 