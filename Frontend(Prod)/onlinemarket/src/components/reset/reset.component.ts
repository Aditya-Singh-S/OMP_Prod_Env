
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RecaptchaModule } from 'ng-recaptcha-2';

@Component({
    selector: 'app-reset',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RecaptchaModule, RouterModule],
    templateUrl: './reset.component.html',
    styleUrls: ['./reset.component.css'],
    providers : [AuthService]
})
export class ResetComponent implements OnInit {
    resetPasswordForm!: FormGroup;
    emailFromQuery: string | null = null;

    constructor(private fb: FormBuilder, private authService: AuthService, private route: ActivatedRoute, private router: Router) {}

    ngOnInit() {
        this.resetPasswordForm = this.fb.group({
            //email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            confirmPassword: ['', [Validators.required]],
            captchaResponse: ['']
        });

        this.route.queryParams.subscribe(params => {
            if (params['email']) {
                this.emailFromQuery = params['email'];
                this.resetPasswordForm.controls['email'].setValue(this.emailFromQuery);
            } else {
                alert('Invalid reset link.');
                this.resetPasswordForm.disable();
            }
        });
    }

    onCaptchaResolved(captchaResponse: string | null) {
        this.resetPasswordForm.patchValue({ captchaResponse });
        console.log('Captcha Response:', captchaResponse);
    }

    resetPassword() {
        if (!this.resetPasswordForm.value.captchaResponse) {
            alert("Please verify that you are not a robot.");
            return;
        }

        if (this.resetPasswordForm.invalid) {
            alert("Please fill in all required fields.");
            return;
        }

        if (this.resetPasswordForm.value.password !== this.resetPasswordForm.value.confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        if (!this.emailFromQuery) {
            alert('No email provided for password reset.');
            return;
        }

        const payload = {
            email: this.emailFromQuery,
            newPassword: this.resetPasswordForm.value.password,
            confirmPassword: this.resetPasswordForm.value.confirmPassword,
            captchaResponse: this.resetPasswordForm.value.captchaResponse
        };

        this.authService.resetPassword(payload).subscribe({
            next: (response: string) => {
                alert(response); 
                setTimeout(() => {
                    this.router.navigate(['/signin']);
                }, 1000);
            },
            error: (error) => {
                alert('Error resetting password: ' + error.error.text); 
                console.error('Error:', error);
            }
        });
    }
}