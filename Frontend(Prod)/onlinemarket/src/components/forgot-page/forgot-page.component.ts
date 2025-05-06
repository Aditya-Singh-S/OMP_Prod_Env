import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RecaptchaModule } from 'ng-recaptcha-2';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-forgot-page',
    imports: [FormsModule, ReactiveFormsModule, RecaptchaModule, RouterModule],
    standalone: true,
    templateUrl: './forgot-page.component.html',
    styleUrls: ['./forgot-page.component.css'],
    providers : [AuthService]
})
export class ForgotPageComponent {
    email: string = '';
    captchaResponse: string | null = null;
    message: string = '';

    constructor(private authService: AuthService) {}

    onCaptchaResolved(captchaResponse: string | null) {
        this.captchaResponse = captchaResponse;
        console.log('Captcha Response:', this.captchaResponse);
    }

    onSubmit(event: Event) {
        event.preventDefault();

        if (!this.captchaResponse) {
            alert("Please verify that you are not a robot ");
            return;
        }

        console.log("Requesting reset link for:", this.email);

        this.authService.forgotPassword(this.email).subscribe({
            next: (resetLink: string) => {
                console.log("Reset Link Generated:", resetLink);
                alert("Reset link has been generated in the console.");
                this.message = "";
                this.email = '';
            },
            error: (error) => {
                console.error("Error:", error);
                if (error.status === 404) {
                    alert("User not found");
                } else {
                    alert("Something went wrong");
                }
                this.message = "";
                
            }
        });
    }
}