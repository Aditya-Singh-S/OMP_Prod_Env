import { CommonModule } from '@angular/common';

import { HttpClient } from '@angular/common/http';

import { Component } from '@angular/core';

import { FormBuilder, FormGroup, Validators,ReactiveFormsModule } from '@angular/forms';

import { Router } from '@angular/router';

import { UserService } from '../../services/user.service';

import { Subject, takeUntil } from 'rxjs';
import { AuthService } from '../../services/auth.service';
 
@Component({

  selector: 'app-signup',

  imports: [ReactiveFormsModule,CommonModule],

  templateUrl: './signup.component.html',

  styleUrl: './signup.component.css',

  providers : [UserService]

})

export class SignupComponent {

  signUpForm: FormGroup;

  photoError: string = '';

  emailError: string = '';

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

      password: ['',[Validators.required,Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$/)]],

      confirmPassword: ['', [Validators.required]],

      addressLine1: ['', [Validators.required, Validators.minLength(10)]],

      addressLine2: ['', [Validators.required, Validators.minLength(10)]],

      postalCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],

      dob: ['', [Validators.required, this.minimumAgeValidator(18)]],

    },

    { validator: this.passwordMatchValidator });
 
    this.signUpForm.get('email')?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(email => {

      if (this.potentiallyDuplicateEmails.includes(email)) {

        this.signUpForm.get('email')?.setErrors({ 'potentialDuplicate': true });

      } else if (this.signUpForm.get('email')?.errors?.['potentialDuplicate']) {

        const currentErrors = { ...this.signUpForm.get('email')?.errors };

        delete currentErrors['potentialDuplicate'];

        this.signUpForm.get('email')?.setErrors(Object.keys(currentErrors).length > 0 ? currentErrors : null);

      }

    });

  }
 
  passwordMatchValidator(formGroup: FormGroup): void {

    const password = formGroup.get('password')?.value;

    const confirmPassword = formGroup.get('confirmPassword')?.value;
 
    if (password && confirmPassword && password !== confirmPassword) {

      formGroup.get('confirmPassword')?.setErrors({ mismatch: true });

    } else {

      formGroup.get('confirmPassword')?.setErrors(null);

    }

  }
 
  minimumAgeValidator(minAge: number) {

    return (control: any) => {

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

    if (file) {

      if (file.size < 10240 || file.size > 20480) {

        this.photoError = 'Photo must be between 10KB and 20KB.';

      } else {

        this.photoError = '';

      }

    }

  }
 
  removePhoto() {

    const photoInput = document.getElementById('photo') as HTMLInputElement;

    if (photoInput) {

        photoInput.value = '';

        this.photoError = '';

    }

}
 
onSubmit(): void {

  if (this.signUpForm.valid && !this.photoError) {

    const formData = new FormData();

    formData.append('firstName', this.signUpForm.get('firstName')?.value || '');

    formData.append('lastName', this.signUpForm.get('lastName')?.value || '');

    formData.append('email', this.signUpForm.get('email')?.value || '');

    formData.append('password', this.signUpForm.get('password')?.value || '');

    formData.append('nickName', this.signUpForm.get('nickName')?.value || '');

    formData.append('addressLine1', this.signUpForm.get('addressLine1')?.value || '');

    formData.append('addressLine2', this.signUpForm.get('addressLine2')?.value || '');

    formData.append('postalCode', this.signUpForm.get('postalCode')?.value || '');

    formData.append('contactNumber', this.signUpForm.get('contactNo')?.value || ''); 

    formData.append('dateOfBirth', this.signUpForm.get('dob')?.value || '');

    const photoInput = (document.getElementById('photo') as HTMLInputElement);

    if (photoInput?.files?.length) {

      formData.append('imageFile', photoInput.files[0]);

    }else{
      this.photoError = 'Photo is required.';
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
          console.log('User registered in database:', response)
          alert('User stored in database successful! Please check your db.');
        },
        error: (err) => console.error('User registration in database failed:', err)
      });
      this.router.navigate(['/verify-email']);
    }).catch(err => {
      console.error("Registration failed:",err);
      alert('Error:'+ err.message);
    });
    
  }
}
closePopup() {
  this.showPopup = false;
}
 
}
 