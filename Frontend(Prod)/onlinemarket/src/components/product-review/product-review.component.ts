import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';

interface ReviewViewModel {
    isActive?: boolean;
    ratingId: number;
    productid: number;
    productName: String;
    userId: number;
    rating: number;
    review: String;
    reviewCreatedOn: Date;
    reviewUpdatedOn: Date;
    reviewDeletedOn: Date;
    reviewActiveStatus: boolean;
    name?: string;
    description?: string;
    avg_rating?: number;
    subscription_count?: number;
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

    constructor(private userService: UserService, private router: Router) {}

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
            this.userService.getProductRatingList(this.userId).subscribe({
                next: (data) => {
                    this.reviews = data.map(review => ({ ...review, isActive: true }));
                    console.log('User Reviews:', this.reviews);
                },
                error: (error) => {
                    console.error('Error loading reviews:', error);
                }
            });
        }
    }

    inactivateReview(review: ReviewViewModel): void {
        review.isActive = false;
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