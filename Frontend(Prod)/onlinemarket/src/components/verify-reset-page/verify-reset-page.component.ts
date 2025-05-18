import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verify-reset-page',
  imports: [CommonModule],
  templateUrl: './verify-reset-page.component.html',
  styleUrl: './verify-reset-page.component.css'
})
export class VerifyResetPageComponent {
  infoMessage: string = 'Please check your email for the verification code to reset your password.';
}