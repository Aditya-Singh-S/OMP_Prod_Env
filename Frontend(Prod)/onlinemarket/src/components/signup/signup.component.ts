import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
  providers: [UserService]
})
export class SignupComponent {
  signUpForm: FormGroup;
  photoError: string = '';
  emailError: string = '';
  isFileSelected: boolean = false;
  potentiallyDuplicateEmails: string[] = [];
  destroy$ = new Subject<void>();
  showPopup: boolean = false;
  popupTitle: string = '';
  popupMessage: string = '';
  popupType: 'success' | 'error' = 'success';

  constructor(private fb: FormBuilder, private userService: UserService, private authService: AuthService, private router: Router) {
    this.signUpForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      lastName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      nickName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z.0-9]+@[a-zA-Z0-9]+\.(com|net|org)$/)]],
      contactNo: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      password: ['', [Validators.required, Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/)]],
      confirmPassword: ['', [Validators.required]],
      addressLine1: ['', [Validators.required, Validators.minLength(10)]],
      addressLine2: ['', [Validators.required, Validators.minLength(10)]],
      postalCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      dob: ['', [Validators.required, this.minimumAgeValidator(18)]],
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(formGroup: FormGroup): void {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;

    if (!confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ required: true });
    } else if (password !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ mismatch: true });
    } else {
      formGroup.get('confirmPassword')?.setErrors(null);
    }
  }

  minimumAgeValidator(minAge: number) {
    return (control: any) => {
      if (!control.value) return null;
      const dob = new Date(control.value);
      const today = new Date();
      const age = today.getFullYear() - dob.getFullYear();
      const hasHadBirthday = today.getMonth() > dob.getMonth() ||
        (today.getMonth() === dob.getMonth() && today.getDate() >= dob.getDate());
      return age > minAge || (age === minAge && hasHadBirthday) ? null : { minAge: true };
    };
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    this.isFileSelected = !!file;

    if (file) {
      this.photoError = file.size < 10240 || file.size > 20480 ? 'Photo must be between 10KB and 20KB.' : '';
    } else {
      this.photoError = 'Photo is required.';
    }
  }

  removePhoto() {
    const photoInput = document.getElementById('photo') as HTMLInputElement;
    if (photoInput) {
      photoInput.value = '';
      this.photoError = '';
    }
  }

  validateBeforeSubmit(): boolean {
    this.signUpForm.markAllAsTouched();
    this.passwordMatchValidator(this.signUpForm);

    const confirmPassword = this.signUpForm.get('confirmPassword')?.value;
    if (!confirmPassword) {
      this.signUpForm.get('confirmPassword')?.setErrors({ required: true });
    }

    const photoInput = document.getElementById('photo') as HTMLInputElement;
    const isPhotoValid = !!photoInput?.files?.length && !(photoInput.files[0].size < 10240 || photoInput.files[0].size > 20480);

    if (!isPhotoValid) {
      this.photoError = 'Photo is required and must be between 10KB and 20KB.';
    }

    return !this.signUpForm.invalid && isPhotoValid;
  }

  onSubmit(): void {
    if (!this.validateBeforeSubmit()) return;

    const formData = new FormData();
    Object.keys(this.signUpForm.controls).forEach(key => {
      formData.append(key, this.signUpForm.get(key)?.value || '');
    });

    const photoInput = document.getElementById('photo') as HTMLInputElement;
    if (photoInput?.files?.length) {
      formData.append('imageFile', photoInput.files[0]);
    }

    console.log(Array.from(formData.entries()));

    const email = this.signUpForm.get('email')?.value;
    const password = this.signUpForm.get('password')?.value;
    localStorage.setItem('userEmail', email);

    this.authService.signUp(email, password).then(result => {
      console.log('User registered:', result);
      alert("Registration Successful! Check for email verification");

      this.userService.register(formData).subscribe({
        next: (response) => {
          console.log('User stored in database:', response);
          alert('User stored in database successfully!');
        },
        error: (err) => console.error('User registration failed:', err)
      });

      this.router.navigate(['/verify-email']);
    }).catch(err => {
      console.error("Registration failed:", err);
      alert('Error: ' + err.message);
    });
  }

  closePopup() {
    this.showPopup = false;
  }
}
 