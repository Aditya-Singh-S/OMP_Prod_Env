import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { Subscription } from 'rxjs';
import { ProductSubscriptionsComponent } from '../product-subscriptions/product-subscriptions.component';
import { ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { ProductReviewComponent } from "../product-review/product-review.component";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    RouterModule,
    ProductSubscriptionsComponent,
    ProductReviewComponent
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DatePipe]
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm: FormGroup;
  photoError: string = '';
  currentPhotoUrl: string | null = null;
  userId: number | null = null;
  userIdSubscription: Subscription | undefined;
  private hasPhotoBeenRemoved: boolean = false; // Track if the photo has been removed
  @ViewChild(ProductSubscriptionsComponent) productSubscriptionsPopup!: ProductSubscriptionsComponent;
  @ViewChild(ProductReviewComponent) productReviewsPopup!: ProductReviewComponent;

  // New properties for the popup
  showPopup: boolean = false;
  popupTitle: string = '';
  popupMessage: string = '';
  popupType: 'success' | 'error' = 'success'; // You can extend this type

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      lastName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      nickName: ['', [Validators.required, Validators.pattern(/^(?=.*[a-zA-Z])[a-zA-Z0-9._]{3,15}$/)]],
      email: ['', [Validators.required, Validators.pattern(/^[a-zA-Z0-9]+@[a-zA-Z0-9]+\.(com|net|org)$/)]],
      contactNumber: ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      postalCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(10)]],
      addressLine2: ['', [Validators.required, Validators.minLength(10)]],
      dateOfBirth: ['', [Validators.required, this.minimumAgeValidator(18)]],
      photo: ['']
    });
    this.profileForm.controls['email'].disable();
  }

  ngOnInit(): void {
    this.userIdSubscription = this.userService.watchUserId().subscribe(id => {
      this.userId = id;
      if (this.userId) {
        this.loadUserProfile();
      } else {
        console.warn('User ID not available.');
      }
    });
  }

  ngOnDestroy(): void {
    if (this.userIdSubscription) {
      this.userIdSubscription.unsubscribe();
    }
  }

  loadUserProfile(): void {
    if (this.userId) {
      this.userService.getUserDetails(this.userId).subscribe({
        next: (profileData: any) => {
          console.log('Profile Data:', profileData);
          this.profileForm.patchValue({
            firstName: profileData.firstName,
            lastName: profileData.lastName,
            nickName: profileData.nickName,
            email: profileData.email,
            contactNumber: profileData.contactNumber,
            postalCode: profileData.postalCode,
            addressLine1: profileData.addressLine1,
            addressLine2: profileData.addressLine2,
            dateOfBirth: this.datePipe.transform(profileData.dateOfBirth, 'yyyy-MM-dd')
          });
          this.currentPhotoUrl = profileData.photo;
          console.log('Current Photo URL:', this.currentPhotoUrl);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error loading profile:', error);
          this.popupTitle = 'Error';
          this.popupMessage = 'Failed to load profile data.';
          this.popupType = 'error';
          this.showPopup = true;
          this.cdr.detectChanges();
        }
      });
    } else {
      console.warn('User ID is not available, cannot load profile.');
    }
  }

  minimumAgeValidator(minAge: number) {
    return (control: any) => {
      const dateOfBirth = new Date(control.value);
      const today = new Date();
      const age = today.getFullYear() - dateOfBirth.getFullYear();
      const hasHadBirthday = today.getMonth() > dateOfBirth.getMonth() ||
        (today.getMonth() === dateOfBirth.getMonth() && today.getDate() >= dateOfBirth.getDate());
      return age > minAge || (age === minAge && hasHadBirthday) ? null : { minAge: true };
    };
  }

  onFileChange(event: any) {
    const file = event?.target?.files?.[0];
    if (file) {
      if (file.size < 10240 || file.size > 20480) {
        this.photoError = 'Photo must be between 10KB and 20KB.';
      } else {
        this.photoError = '';
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.currentPhotoUrl = e.target.result;
          this.hasPhotoBeenRemoved=false;
          this.cdr.detectChanges(); // Manually trigger change detection for OnPush
        };
        reader.readAsDataURL(file);
      }
    }
  }

  removePhoto() {
    const photoInput = document.getElementById('photo') as HTMLInputElement;
    if (photoInput) {
      photoInput.value = '';
      this.photoError = '';
      this.currentPhotoUrl = null;
      this.hasPhotoBeenRemoved=true;
      this.cdr.detectChanges(); 
    }
  }

  onSubmit(): void {

    console.log('onSubmit() called'); // Diagnostic log
    // Check if a photo is required and if it has been removed without a new one being selected
    if (!this.currentPhotoUrl && this.hasPhotoBeenRemoved) {
      this.photoError = 'Please select a photo.';
      return; // Stop the submission
    } else {
      this.photoError = ''; // Clear any previous photo error if a photo exists or wasn't removed
    }
    
    if (this.profileForm.valid && !this.photoError && this.userId) {
      const formData = new FormData();
      formData.append('firstName', this.profileForm.get('firstName')?.value || '');
      formData.append('lastName', this.profileForm.get('lastName')?.value || '');
      formData.append('nickName', this.profileForm.get('nickName')?.value || '');
      formData.append('email', this.profileForm.get('email')?.value || '');
      formData.append('contactNumber', this.profileForm.get('contactNumber')?.value || '');
      formData.append('addressLine1', this.profileForm.get('addressLine1')?.value || '');
      formData.append('addressLine2', this.profileForm.get('addressLine2')?.value || '');
      formData.append('postalCode', this.profileForm.get('postalCode')?.value || '');
      formData.append('dateOfBirth', this.profileForm.get('dateOfBirth')?.value || '');
      const address = `${this.profileForm.get('addressLine1')?.value || ''}, ${this.profileForm.get('addressLine2')?.value || ''} - ${this.profileForm.get('postalCode')?.value || ''}`;
      formData.append('address', address);
      const photoInput = document.getElementById('photo') as HTMLInputElement;
      if (photoInput?.files?.length) {
        formData.append('photo', photoInput.files[0]);
      }
      console.log('Form Data:', Array.from(formData.entries()));
      this.userService.updateUser(this.userId, formData).subscribe({
        next: (response) => {
          console.log('Profile updated successfully:', response);
          this.popupTitle = 'Success';
          this.popupMessage = 'Profile updated successfully!';
          this.popupType = 'success';
          this.showPopup = true;
          this.cdr.detectChanges();
          this.loadUserProfile();
        },
        error: (err) => {
          console.error('Profile update failed:', err);
          this.popupTitle = 'Error';
          this.popupMessage = `Profile update failed: ${err.message}`;
          this.popupType = 'error';
          this.showPopup = true;
          this.cdr.detectChanges();
        }
      });
    }
  }

  closePopup() {
    this.showPopup = false;
    this.cdr.detectChanges();
  }

  openSubscriptions(): void {
    console.log('openSubscriptions() called');
    this.productSubscriptionsPopup.openSubscriptionPopup();
  }

  openReviewPopup(): void {
    this.productReviewsPopup.openReviewPopup();
  }

  
}

