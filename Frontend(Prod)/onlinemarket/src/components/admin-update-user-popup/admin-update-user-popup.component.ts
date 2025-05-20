import { Component, Output, EventEmitter, Input, OnInit, OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../services/user.service';
import { ProductService } from '../../services/product.service';
import { IProductDTO } from '../../model/class/interface/Products';
import { IReview } from '../../model/class/interface/Products';
import { catchError, tap, switchMap, finalize } from 'rxjs/operators';
import { of, forkJoin, Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
 
interface IUserIdResponse {
  userId: number;
}
 
interface IUserDetails {
  userID: string | number;
  email: string;
  active: boolean;
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
  contactNumber?: string;
  postalCode?: string;
  addressLine1?: string;
  addressLine2?: string;
  profilePicture?: File | string;
  [key: string]: any;
  // Add other relevant user properties
}
 
interface ISubscription {
  id: number;
  productId: number;
  productName: string;
  imageUrl: string;
  subscribersCount: number;
  averageRating: number;
  isSelected: boolean;
}
 
interface SubscriptionViewModel extends IProductDTO {
  isSelectedToRemove: boolean;
}

@Pipe({
  name: 'filterActiveReviews'
})

export class FilterActiveReviewsPipe implements PipeTransform {
  transform(reviews: IReview[]): IReview[] {
    if (!reviews || reviews.length === 0) {
      return [];
    }
    return reviews.filter(review => review.reviewActiveStatus === true);
  }
}
 
@Component({
  selector: 'app-admin-update-user-popup',
  standalone: true,
  imports: [FormsModule, CommonModule, FilterActiveReviewsPipe],
  templateUrl: './admin-update-user-popup.component.html',
  styleUrls: ['./admin-update-user-popup.component.css']
})
export class AdminUpdateUserPopupComponent implements OnInit, OnDestroy {
  @Input() isUpdateUserPopupVisible: boolean = false;
  @Output() close = new EventEmitter<void>();
  @Output() submit = new EventEmitter<any>();
 
  searchEmail: string = '';
  foundUser: IUserDetails | null = null;
  userNotFoundMessage: string | null = null;
  showProfileSection: boolean = false;
  showSubscriptionsSection: boolean = false;

  showReviewUpdateSuccessPopup=false;
 
  areReviewsVisible: boolean = false;
 
  userSubscriptions: ISubscription[] = [];
  userReviews: IReview[] = [];
 
  public currentUserId: number | null = null;
  public currentUser: any = {};
 
  userId: number | null = null;
  subscriptions: SubscriptionViewModel[] = [];
  userIdSubscription: Subscription | undefined;
  showSubscriptionPopup: boolean = false;
  updateSuccessMessage: string | null = null;
 
  showAlertPopup: boolean = false;
   popupTitle: string = '';
   popupMessage: string = '';

  constructor(private userService: UserService,
    private productService: ProductService,
    private http: HttpClient,
    private route: ActivatedRoute) { }
 
  ngOnInit(): void {
   
  }
 
  ngOnDestroy(): void {
   
  }
 
  openPopup() {
    this.isUpdateUserPopupVisible = true;
    this.foundUser = null;
    this.userNotFoundMessage = null;
    this.searchEmail = '';
    this.showProfileSection = false;
    this.showSubscriptionsSection = false;
    this.showReviewsSection = false;
    this.userSubscriptions = [];
    this.userReviews = [];
    this.currentUserId = null;
  }
 
  closePopup() {
    this.isUpdateUserPopupVisible = false;
    this.close.emit();
  }
 
  searchUserByEmail() {
    this.userNotFoundMessage = null;
    this.foundUser = null;
    this.showProfileSection = false;
    this.showSubscriptionsSection = false;
    this.showReviewsSection = false;
    this.userSubscriptions = [];
    this.userReviews = [];
    this.currentUserId = null;
 
    if (this.searchEmail) {
      this.userService.getUserIdByEmail(this.searchEmail).subscribe({
        next: (userId: number) => {
          this.currentUserId = userId;
          this.loadUserDetails(this.currentUserId);
          this.loadUserSubscriptions(this.currentUserId);
          this.loadUserReviews(this.currentUserId);
        },
        error: (error) => {
          console.error('Error searching user ID:', error);
          this.userNotFoundMessage = 'User with this email not found.';
        }
      });
    } else {
      this.userNotFoundMessage = 'Please enter an email to search.';
    }
  }
 
  loadUserDetails(userId: number) {
    this.userService.getUserDetails(userId).subscribe({
      next: (userDetails: any) => {
        console.log('User details fetched in loadUserDetails:', userDetails); // <--- Debugging log
        this.foundUser = userDetails;
        console.log('foundUser after loading:', this.foundUser); // <--- Debugging log
        this.currentUser = { ...userDetails };
        console.log('User details fetched:', this.currentUser);
        // console.log(this.foundUser?.isActive);
 
      },
      error: (error) => {
        console.error('Error fetching user details:', error);
        this.userNotFoundMessage = 'Error fetching user details.';
        this.foundUser = null;
        this.currentUser = {};
      }
    });
  }
 
  updateUserActiveStatusNgModel(): void {
    if (this.currentUserId && this.foundUser) {
      this.userService.updateUserActiveStatus(this.currentUserId, this.foundUser.active).subscribe({
        next: (response) => {
          console.log('User active status updated in backend:', response);
          this.popupTitle = "Success";
          this.popupMessage = "User Active Status Updated";
          this.showAlertPopup = true;
          // The local foundUser.isActive is already updated by ngModel
          // Optionally, show a success message
        },
        error: (error) => {
          console.error('Error updating user active status in backend:', error);
          this.popupTitle = "Error";
          this.popupMessage = "Updating User Active Status Failed";
          this.showAlertPopup = true;
          // Optionally, revert the UI change or show an error message
          // If you want to revert the UI on error, you might need to store the previous state
        }
      });
    } else {
      console.warn('User ID not available, cannot update active status.');
    }
  }
 
  // loadUserSubscriptions(userId: number) {
  //   this.userService.getProductSubscriptionList(userId).subscribe({ // Assuming this service method exists
  //     next: (productDTOs: IProductDTO[]) => {
  //       this.userSubscriptions = productDTOs.map(dto => ({
  //         productId: dto.productid,
  //         productName: dto.name,
  //         imageUrl: dto.imageUrl,
  //         subscribersCount: dto.subscription_count,
  //         averageRating: dto.avg_rating,
  //         isSelected: false // Initialize isSelected
  //       } as ISubscription));
  //     },
  //     error: (error) => {
  //       console.error('Error fetching user subscriptions:', error);
  //       // Optionally display an error message
  //     }
  //   });
  // }
 
  showReviews(userId: number) {
    this.areReviewsVisible = true;
    this.loadUserReviews(userId); // Load all reviews when the section is shown
  }
 
  loadUserReviews(userId: number) {
    this.userService.getUserProductReviews(userId).subscribe({ // Assuming this service method exists
      next: (reviews: IReview[]) => {
        console.log('Reviews fetched:', reviews);
        this.userReviews = reviews;
      },
      error: (error) => {
        console.error('Error fetching user reviews:', error);
        // Optionally display an error message
      }
    });
  }
 
  submitMainForm() {
    // if (this.foundUser) {
    //   console.log('Submitting user data with ID:', this.foundUser.userID, this.foundUser);
    // //  this.submit.emit(this.foundUser); //  <---  Don't emit the IUserDetails directly here.
    // const formData = this.buildFormData(this.foundUser);  // Convert to FormData
    // this.submit.emit(formData);
    // } else {
    //   console.warn('No user found or user ID is missing on submit.');
    //   this.userNotFoundMessage = 'Please search for a user before submitting.';
    // }
    console.log('User data received for update:', this.foundUser);
    console.log('UserID :', this.foundUser?.userID);
    //const userId = userData.userID;
    if (this.foundUser && this.foundUser?.userID) {
      const userId = this.foundUser.userID;
      console.log("Inside if, User ID:", userId);
      const formData = new FormData();
      for (const key in this.foundUser) {
        if (this.foundUser.hasOwnProperty(key)) {
          formData.append(key, this.foundUser[key]);
        }
      }
 
      this.userService.updateUser(this.foundUser.userID, formData).subscribe({
        next: (response) => {
          console.log('User updated successfully:', response);
          this.isUpdateUserPopupVisible = false;
          // alert('User updated successfully!');
          this.popupTitle = "Success";
          this.popupMessage = "User Updated Successfully";
          this.showAlertPopup = true;
        },
        error: (error) => {
          console.error('Error updating user:', error);
          // alert('Error updating user.');
          this.popupTitle = "Error";
          this.popupMessage = "Error Updating User";
          this.showAlertPopup = true;
        }
      });
    } else {
      console.error('User ID is missing in the user data.');
      // alert('Error: Could not update user (ID missing).');
      this.popupTitle = "Error";
          this.popupMessage = "Could not update user (ID missing)";
          this.showAlertPopup = true;
    }
  }
 
  submitProfileChanges() {
    if (this.foundUser) {
      console.log('Saving profile changes for:', this.foundUser);
      const formData = this.buildFormData(this.foundUser); // Convert to FormData before sending
      this.userService.updateUser(this.foundUser.userID, formData).pipe( // Pass FormData
        tap(response => {
          console.log('Profile changes saved successfully:', response);
        }),
        catchError(error => {
          console.error('Error saving profile changes:', error);
          return of(null);
        })
      ).subscribe();
    } else {
      console.warn('No user found to update profile.');
      // Optionally inform the user that no user was found
    }
  }
 
 
  submitSubscriptionChanges() {
    const selectedSubscriptions = this.userSubscriptions
      .filter(sub => sub.isSelected)
      .map(sub => sub.productId);
 
    if (this.currentUserId) {
      console.log('Updating subscriptions for user:', this.currentUserId, selectedSubscriptions);
      this.userService.updateUserSubscriptions(this.currentUserId, selectedSubscriptions).pipe(
        tap(response => {
          console.log('Subscriptions updated successfully:', response);
          // Optionally provide feedback to the user
          this.popupTitle = "Success";
          this.popupMessage = "Subscriptions updated successfully";
          this.showAlertPopup = true;
        }),
        catchError(error => {
          console.error('Error updating subscriptions:', error);
          // Optionally display an error message
          this.popupTitle = "Error";
          this.popupMessage = "Updating Subscriptions failed";
          this.showAlertPopup = true;
          return of(null);
        })
      ).subscribe();
    } else {
      console.warn('No user ID available to update subscriptions.');
      // Optionally inform the user
    }
  }
 
 
  deleteReview(reviewId: number) {
    if (this.currentUserId) {
      console.log('Deleting review:', reviewId, 'for user:', this.currentUserId);
 
      this.productService.updateReviewStatus(reviewId, this.currentUserId, false).pipe(
        tap(response => {
          // Backend deletion successful, now update the frontend
          console.log('Review deleted successfully from backend:', response);
          this.userReviews = this.userReviews.filter(review => review.ratingId !== reviewId);
          // Optionally, you can emit an event or show a success message
        }),
        catchError(error => {
          // Handle backend deletion error
          console.error('Error deleting review from backend:', error);
          // Optionally, show an error message to the user
          return of(null); // Or throw the error again if you want the observable to error out
        })
      ).subscribe(); // Don't forget to subscribe to trigger the observable
    }
  }
 
  submitReviewChanges() {
    if (this.currentUserId && this.userReviews) {
      console.log('Saving review changes for user:', this.currentUserId, this.userReviews);
 
      forkJoin( // Use forkJoin to wait for all updates to complete
        this.userReviews.map(review => {
          const params = new HttpParams()
            .set('ratingId', review.ratingId ? review.ratingId.toString() : '') // Assuming ratingId exists
            .set('userId', this.currentUserId ? this.currentUserId.toString() : '')
            .set('rating', review.rating ? review.rating.toString() : '')
            .set('review', review.review ? review.review : '')
            .set('reviewActiveStatus', review.reviewActiveStatus ? review.reviewActiveStatus.toString() : '');
 
          return this.http.put(
            'https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/reviews/updateReview',
            null, // No body, parameters are in the URL
            { headers : this.userService.authHeaders }
          ).pipe(
            tap(response => console.log('Review updated:', response)),
            catchError(error => {
              console.error('Error updating review:', error);
              return of(error); // Or handle the error as needed
            })
          );
        })
      ).subscribe(results => {
        console.log('All review updates completed', results);
        // Optionally, show a success message or reload data
      });
    }
  }
 
  private _showReviewsSection = false;
 
  set showReviewsSection(value: boolean) {
    this._showReviewsSection = value;
    if (value && this.currentUserId) {
      this.areReviewsVisible = true; // Make the review grid visible
      this.loadUserReviews(this.currentUserId); // Load ALL reviews
    } else {
      this.areReviewsVisible = false;
      this.userReviews = [];
    }
  }
 
  get showReviewsSection(): boolean {
    return this._showReviewsSection;
  }
 
  toggleReviewStatus(review: IReview) {
    const newStatus = !review.reviewActiveStatus;
    this.productService.updateReviewStatus(review.ratingId, this.currentUserId, newStatus) // Assuming this service method exists
      .pipe(
        tap(response => {
          console.log('Review status updated:', response);
          // Update the local array to reflect the change immediately
          review.reviewActiveStatus = newStatus;
          this.popupTitle = "Success";
          this.popupMessage = "Review Status Updated Successfully"
          this.showAlertPopup = true;
        }),
        catchError(error => {
          console.error('Error updating review status:', error);
          // Optionally show an error message
          this.popupTitle = "Error";
          this.popupMessage = "Review Status Updation Failed"
          this.showAlertPopup = true;
          return of(null);
        })
      )
      .subscribe();
  }
 
// Method to close the success popup
closeReviewUpdateSuccessPopup() {
    this.showReviewUpdateSuccessPopup = false;
}
  getProductImage(productId: number): string {
    // Replace this with your actual logic to fetch the image URL
    console.log("Get Image method called");
    return `https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP/product/image${productId}`;

  }
 
  // updateUserActiveStatus(event: any): void {
  //   const isActive = event.target.checked;
  //   if (this.currentUserId && this.foundUser) {
  //     this.userService.updateUserActiveStatus(this.currentUserId, isActive).subscribe({
  //       next: (response) => {
  //         console.log('User active status updated in backend:', response);
  //         this.foundUser!.isActive = isActive; // Update the local foundUser object
  //         // Optionally, show a success message
  //       },
  //       error: (error) => {
  //         console.error('Error updating user active status in backend:', error);
  //         // Optionally, revert the UI change or show an error message
  //       }
  //     });
  //   }
  // }
 
  updateUserActiveStatus(event: any): void {
    const active = event.target.checked;
    // console.log('hi' + isActive);
    if (this.currentUserId && this.foundUser) {
      this.userService.updateUserActiveStatus(this.currentUserId, active).subscribe({
        next: (response) => {
          console.log('User active status updated in backend:', response);
          this.foundUser!.active = active; // Update the local foundUser object
          // Optionally, show a success message
        },
        error: (error) => {
          console.error('Error updating user active status in backend:', error);
          // Optionally, revert the UI change or show an error message
        }
      });
    } else {
      console.warn('User ID not available, cannot update active status.');
    }
  }
 
  onActiveStatusChange(event: any) {
    this.updateUserActiveStatus(event.target.checked);
  }
 
  deleteSubscription(productIdToDelete: number, index: number): void {
    if (this.currentUserId && productIdToDelete && confirm('Are you sure...?')) {
      this.userService.removeSubscription(this.currentUserId, productIdToDelete).subscribe({
        next: (response) => {
          console.log('Subscription removed successfully:', response);
          this.userSubscriptions.splice(index, 1);
          this.popupTitle = "Success";
          this.popupMessage = "Subscription Updated Successfully";
          this.showAlertPopup = true;
          // Optionally update UI or show success message
        },
        error: (error) => {
          console.error('Error removing subscription:', error);
          // Optionally show error message
        }
      });
    } else {
      console.warn('User ID or Product ID is missing.');
    }
  }
 
  //  Helper function to convert IUserDetails to FormData
  private buildFormData(user: IUserDetails): FormData {
    const formData = new FormData();
    Object.keys(user).forEach(key => {
      const value = user[key as keyof IUserDetails]; // Use keyof
      if (value !== undefined) {
        if (key === 'profilePicture' && typeof value === 'object') {
          formData.append(key, value);
        } else {
          formData.append(key, String(value));
        }
      }
    });
    return formData;
  }
 
  toggleSubscriptionStatus(productId: number, event: any) {
    const isEnabled = event.target.checked;
 
    this.userService.updateSubscriptionStatus(this.currentUserId, productId, isEnabled).subscribe({
      next: (response) => {
        console.log(`Subscription status for product ${productId} updated to ${isEnabled}`, response);
        // Update the local array to reflect the change immediately
        if (!isEnabled) {
          this.userSubscriptions = this.userSubscriptions.filter(sub => sub.productId !== productId);
        } else {
          // If it's enabled, you might want to reload the subscriptions
          // or find a way to add it back to the UI if it was previously removed.
          // For immediate addition back, you would typically need to fetch
          // the single subscription detail and add it to the array.
          // This example does not implement that immediate re-addition.
          console.warn('Note: Enabling a subscription back will not immediately show it unless the list is reloaded.');
          // If you want to implement immediate re-addition
        // You would need to call a service method to get the product details by productId
          // and then add a new ISubscription object to this.userSubscriptions.
          // Example (assuming you have a method like getProductDetails):
          // this.productService.getProductDetails(productId).subscribe(product => {
          //   this.userSubscriptions = [...this.userSubscriptions, {
          //     productId: product.productid,
          //     productName: product.name,
          //     imageUrl: product.imageUrl,
          //     subscribersCount: product.subscription_count,
          //     averageRating: product.avg_rating,
          //     isSelected: true
          //   }];
          // });
        }
      },
      error: (error) => {
        console.error(`Error updating subscription status for product ${productId}`, error);
        // Handle error (e.g., show a message to the user)
        // Optionally, revert the checkbox state in case of an error
        const subscription = this.userSubscriptions.find(sub => sub.productId === productId);
        if (subscription) {
          subscription.isSelected = !isEnabled;
          event.target.checked = !isEnabled;
        }
      }
    });
  }
 
  // ------------------------------------------------------------------
  // Product Subscription
  loadUserSubscriptions(userId: number): void {
    if (userId) {
      this.userService.getProductSubscriptionList(userId).subscribe({
        next: (data) => {
          this.subscriptions = data.map(sub => ({ ...sub, isSelectedToRemove: true })); // Initialize as selected for removal if unchecked
          console.log('User Subscriptions for User ID:', userId, this.subscriptions);
        },
        error: (error) => {
          console.error('Error loading subscriptions for User ID:', userId, error);
        }
      });
    } else {
      console.warn('User ID is null or undefined, cannot load subscriptions.');
      this.subscriptions = []; // Optionally clear any existing subscriptions
    }
  }
 
  removeSelectedSubscriptions(): void {
    if (this.currentUserId) {
      const productsToRemove = this.subscriptions // Use userSubscriptions here
        .filter(sub => !sub.isSelectedToRemove) // Find those that are *not* selected (i.e., unchecked)
        .map(sub => sub.productid);
 
      if (productsToRemove.length > 0) {
        let removalCount = 0;
        productsToRemove.forEach(productId => {
          this.userService.removeSubscription(this.currentUserId!, productId).subscribe({
            next: (response) => {
              console.log(`Subscription for product ${productId} removed successfully for user ${this.currentUserId}:`, response);
              this.popupTitle = "Success";
              this.popupMessage = "Product Subscriptions Updated Successfully";
              this.showAlertPopup = true;
              removalCount++;
              if (removalCount === productsToRemove.length) {
                this.loadUserSubscriptions(this.currentUserId!); // Reload the list after all removals, pass userId
                this.updateSuccessMessage = 'Profile Updated Successfully';
                setTimeout(() => this.updateSuccessMessage = null, 3000); // Clear message after 3 seconds
              }
            },
            error: (error) => {
              console.error(`Error removing subscription for product ${productId} for user ${this.currentUserId}:`, error);
              // Handle the error (e.g., display an error message)
            }
          });
        });
      } else {
        console.log('No products selected for unsubscription.');
        // Optionally display a message
      }
    } else {
      console.warn('User ID not available, cannot remove subscriptions.');
    }
  }
 
  // Assuming you have a button to trigger this, and you want to load subscriptions
  // for the currently searched user when the subscription section is shown.
  // You might not need a separate 'openSubscriptionPopup' if you are loading
  // subscriptions when 'showSubscriptionsSection' becomes true.
 
  // If you do have a specific button to "View Subscriptions":
  viewSubscriptions(): void {
    this.showSubscriptionsSection = true;
    this.updateSuccessMessage = null; // Clear any previous success message
    if (this.currentUserId) {
      this.loadUserSubscriptions(this.currentUserId); // Load for the current user
    } else {
      console.warn('No user selected to view subscriptions.');
      // Optionally display a message to the user
    }
  }
 
  // If you were using 'openSubscriptionPopup' to specifically trigger loading:
  /*
  openSubscriptionPopup(): void {
    this.showSubscriptionPopup = true;
    this.updateSuccessMessage = null; // Clear any previous success message
    if (this.currentUserId) {
      this.loadUserSubscriptions(this.currentUserId); // Load for the current user
    } else {
      console.warn('No user selected to view subscriptions.');
      // Optionally display a message to the user
    }
  }
  */
 
closeSubscriptionPopup(): void {
    this.showSubscriptionPopup = false;
    this.updateSuccessMessage = null; // Clear success message when closing
}

closeAlertPopup(){
  this.showAlertPopup = false;
}
}
 