import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { forkJoin, Subscription, tap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { IProductDTO, IRatingDTO } from '../../model/class/interface/Products';

interface ReviewViewModel extends IRatingDTO{
    isActive?: boolean;
    ratingId: number;
    productid: number;
    productName: string;
    userId: number;
    rating: number;
    review: string;
    reviewCreatedOn: Date;
    reviewUpdatedOn: Date;
    reviewDeletedOn: Date;
    reviewActiveStatus: boolean;
    imageUrl?: string;
    description?: string;
    subscribersCount?: number; 
    markedForDeletion?: boolean;

}

@Component({
    selector: 'app-product-review',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './product-review.component.html',
    styleUrls: ['./product-review.component.css']
})
export class ProductReviewComponent implements OnInit, OnDestroy {
    userId: number | null = null;
    reviews: ReviewViewModel[] = [];
    userIdReview: Subscription | undefined;
    showReviewPopup: boolean = false;
    updateSuccessMessage: string | null = null;
    errorMessage: string | null = null;
    @Output() reviewDeleted = new EventEmitter<number>();

    constructor(private userService: UserService, private router: Router) { }

    ngOnInit(): void {
        this.userIdReview = this.userService.watchUserId().subscribe(id => {
            this.userId = id;
            if (this.userId) {
                this.loadUserReviews();
            } else {
                console.warn('User ID not available.');
            }
        });
    }

    ngOnDestroy(): void {
        if (this.userIdReview) {
            this.userIdReview.unsubscribe();
        }
    }

    loadUserReviews(): void {
        if (this.userId) {
            console.log('loadUserReviews() called with userId:', this.userId);
            this.userService.getProductRatingList(this.userId).subscribe({
                next: (data) => {
                    console.log('Raw review data:', data);
                    this.reviews = data.map(review => ({
                        isActive: review.reviewActiveStatus,
                        ratingId: review.ratingId,
                        productid: review.productid,
                        productName: review.productName,
                        userId: review.userId,
                        rating: review.rating,
                        review: review.review,
                        reviewCreatedOn: review.reviewCreatedOn,
                        reviewUpdatedOn: review.reviewUpdatedOn,
                        reviewDeletedOn: review.reviewDeletedOn,
                        reviewActiveStatus: review.reviewActiveStatus,
                        imageUrl: review.imageUrl,
                        description: review.description,
                        subscribersCount: review.subscribersCount,
 
                    } as ReviewViewModel));
                    this.reviews = this.reviews.filter(review => review.reviewActiveStatus); // Filter out inactive reviews
                    console.log(this.reviews)
                },
                error: (error) => {
                    console.error('Error loading reviews:', error);
                    this.errorMessage = 'Error Loading reviews';
                    setTimeout(() => this.errorMessage = null, 3000);
                }
 
 
 
            });
        }
    }
  

    inactivateReview(review: ReviewViewModel): void {
        review.isActive = false;
    }
    
    markForDeletion(reviewToDelete: ReviewViewModel): void {
        this.reviews = this.reviews.map(review => {
          if (review.ratingId === reviewToDelete.ratingId) {
            return { ...review, markedForDeletion: true }; 
          }
          return review;
        });
        this.updateSuccessMessage = `Review for ${reviewToDelete.productName} will be removed on submit.`;
        setTimeout(() => this.updateSuccessMessage = null, 3000);
      }

      deleteReview(reviewToDelete: ReviewViewModel): void {
        this.markForDeletion(reviewToDelete);
      }

      submitReviews(): void {
        const reviewsToDelete = this.reviews.filter(review => review.markedForDeletion);
        console.log('Reviews to delete on submit:', reviewsToDelete);
    
        if (reviewsToDelete.length > 0) {
          forkJoin(
            reviewsToDelete.map(review =>
              this.userService.updateReview(review.ratingId, null, false)
            )
          ).subscribe({
            next: (responses) => {
              console.log('All reviews marked for removal:', responses);
              this.updateSuccessMessage = `${reviewsToDelete.length} Reviews Removed Successfully`;
              this.loadUserReviews(); // Refresh the list after successful deletion
              reviewsToDelete.forEach(review => this.reviewDeleted.emit(review.ratingId));
              setTimeout(() => this.updateSuccessMessage = null, 3000);
            },
            error: (error) => {
              console.error('Error deleting reviews:', error);
              this.errorMessage = `Error removing reviews.`;
              setTimeout(() => this.errorMessage = null, 3000);
            }
          });
        } else {
          this.updateSuccessMessage = 'No reviews to delete.';
          setTimeout(() => this.updateSuccessMessage = null, 3000);
        }
      }

    openReviewPopup(): void {
        this.showReviewPopup = true;
        this.updateSuccessMessage = null;
        if (this.userId && (!this.reviews || this.reviews.length === 0)) {
            this.loadUserReviews();
        }
    }

    closeReviewPopup(): void {
        this.showReviewPopup = false;
        this.updateSuccessMessage = null;
    }

    
}