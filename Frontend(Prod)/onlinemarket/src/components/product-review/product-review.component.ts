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
    markForDeletion(review: ReviewViewModel): void {
        review.isActive = false; 
    }

    deleteReview(reviewToDelete: ReviewViewModel): void {
        if (confirm(`Are you sure you want to remove your review for ${reviewToDelete.productName}?`)) {
            this.userService.updateReview(
                reviewToDelete.ratingId,
                null,
                reviewToDelete.rating,
                null,
                false
            ).subscribe({
                next: (response) => {
                    console.log('Review marked for removal successfully:', response);
                    this.updateSuccessMessage = `Review for ${reviewToDelete.productName} removed.`;
                    this.loadUserReviews();
                    this.reviewDeleted.emit(reviewToDelete.ratingId); 
                    setTimeout(() => this.updateSuccessMessage = null, 3000);
                },
                error: (error) => {
                    console.error('Error marking review for removal:', error);
                    this.errorMessage = `Error removing review for ${reviewToDelete.productName}.`;
                    setTimeout(() => this.errorMessage = null, 3000);
                }
            });
        }
    }

    submitReviews(): void {
        const activeReviews = this.reviews.filter(review => review.isActive);
        console.log('Active reviews to submit:', activeReviews);
        this.updateSuccessMessage = 'Reviews Updated Successfully';
        setTimeout(() => this.updateSuccessMessage = null, 3000);
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