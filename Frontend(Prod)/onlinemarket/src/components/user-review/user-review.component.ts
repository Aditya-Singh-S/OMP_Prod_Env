import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';
import { UserService } from '../../services/user.service'; // Import UserService
import { catchError, filter, forkJoin, map, Observable, of, switchMap } from 'rxjs';

interface Review {
  userId: number;
  rating: number;
  review: string;
  userName?: string; // Add userName property
  productName: string;
  reviewCreatedOn: string;
  reviewActiveStatus : boolean;
}

@Component({
  selector: 'app-user-review',
  imports: [CommonModule],
  templateUrl: './user-review.component.html',
  styleUrls: ['./user-review.component.css'],
  providers: [UserService] // Provide UserService here
})
export class UserReviewComponent implements OnInit {

  private baseUrl = "https://n1sqae1lk8.execute-api.us-east-1.amazonaws.com/tempProd/OMP";

  reviews: Review[] = [];
  highestRatedReview: Review | null = null;
  showPopup = false;
  productId: number = 0;
  loadingReviews = true; // Add a loading flag
  loadingHighestRated = true; // Add a loading flag

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    public userService: UserService // Inject UserService (make it public to access in the template)
  ) {}

  ngOnInit() {
    this.productId = +this.route.snapshot.paramMap.get('id')!;
    // this.fetchReviews();
    // this.fetchHighestRatedReview();
    this.fetchReviewsWithUserNames(); // Call the new method
    this.fetchHighestRatedReviewWithUserName(); // Call the new method
  }

  // fetchReviews() {
  //   this.http
  //     .get<Review[]>(
  //       `${this.baseUrl}/reviews/getSpecificProductReviews?productId=${this.productId}`,
  //       { responseType: 'json' }
  //     )
  //     .subscribe(
  //       (data) => {
  //         this.reviews = data.filter(review => review.reviewActiveStatus); // Corrected property name
  //         this.reviews.forEach((review) => {
  //           this.fetchUserName(review.userId);
  //         });
  //       },
  //       (error: HttpErrorResponse) => {
  //         if (error.error instanceof ErrorEvent) {
  //           console.error('Client-side error:', error.error.message);
  //         } else {
  //           console.error(`Server-side error: ${error.status} - ${error.message}`);
  //         }
  //       }
  //     );
  // }
 

  // fetchHighestRatedReview() {
  //   this.http
  //     .get<Review>(
  //       `${this.baseUrl}/reviews/highestRatedReview?productId=${this.productId}`,
  //       { responseType: 'json' }
  //     )
  //     .subscribe(
  //       (review) => {
  //         if (review && review.reviewActiveStatus) { // Corrected property name
  //           this.highestRatedReview = review;
  //           this.fetchUserNameForHighestRated(review.userId);
  //         } else {
  //           this.highestRatedReview = null;
  //         }
  //       },
  //       (error: HttpErrorResponse) => {
  //         if (error.error instanceof ErrorEvent) {
  //           console.error('Client-side error:', error.error.message);
  //         } else {
  //           console.error(`Server-side error: ${error.status} - ${error.message}`);
  //         }
  //       }
  //     );
  // }

  fetchReviewsWithUserNames() {
    this.loadingReviews = true;
    this.http
      .get<Review[]>(
        `${this.baseUrl}/reviews/getSpecificProductReviews?productId=${this.productId}`,
        { responseType: 'json' }
      )
      .pipe(
        // Filter active reviews
        map(data => data.filter(review => review.reviewActiveStatus)),
        // Fetch user names for all reviews
        switchMap(activeReviews =>
          forkJoin(
            activeReviews.map(review =>
              this.fetchUserNamePromise(review.userId).pipe(
                map(userName => ({ ...review, userName }))
              )
            )
          )
        )
      )
      .subscribe(
        (reviewsWithNames) => {
          this.reviews = reviewsWithNames;
          this.loadingReviews = false;
          console.log("Fetched reviews with names:", this.reviews);
        },
        (error: HttpErrorResponse) => {
          console.error('Error fetching reviews:', error);
          this.loadingReviews = false;
        }
      );
  }

  fetchHighestRatedReviewWithUserName() {
    this.loadingHighestRated = true;
    this.http
      .get<Review>(
        `${this.baseUrl}/reviews/highestRatedReview?productId=${this.productId}`,
        { responseType: 'json' }
      )
      .pipe(
        filter(review => !!review && review.reviewActiveStatus), // Ensure review exists and is active
        switchMap(activeReview =>
          this.fetchUserNamePromise(activeReview.userId).pipe(
            map(userName => ({ ...activeReview, userName }))
          )
        ),
        catchError(() => of(null)) // Handle cases where no active review is found
      )
      .subscribe(
        (highestRatedReviewWithName) => {
          this.highestRatedReview = highestRatedReviewWithName || null;
          this.loadingHighestRated = false;
          console.log("Highest rated review with name:", this.highestRatedReview);
        },
        (error: HttpErrorResponse) => {
          console.error('Error fetching highest rated review:', error);
          this.loadingHighestRated = false;
        }
      );
  }

  fetchUserNamePromise(userId: number): Observable<string> {
    return this.http.get<{ firstName: string }>(`${this.baseUrl}/myName?userId=${userId}`).pipe(
      map(response => response?.firstName || 'Unknown User'),
      catchError(error => {
        console.error(`Error fetching user name for userId ${userId}:`, error);
        return of('Unknown User');
      })
    );
  }


  // fetchUserName(userId: number) {
  //   this.http
  //     .get<{ firstName: string }>(`${this.baseUrl}/myName?userId=${userId}`)
  //     .subscribe(
  //       (response) => {
  //         this.reviews.forEach((review) => {
  //           if (review.userId === userId) {
  //             review.userName = response?.firstName || 'Unknown User';
  //           }
  //         });
  //       },
  //       (error) => {
  //         console.error(`Error fetching user name for userId ${userId}:`, error);
  //         this.reviews.forEach((review) => {
  //           if (review.userId === userId) {
  //             review.userName = 'Unknown User';
  //           }
  //         });
  //       }
  //     );
  // }

  // fetchUserNameForHighestRated(userId: number) {
  //   this.http
  //     .get<{ firstName: string }>(`${this.baseUrl}/myName?userId=${userId}`)
  //     .subscribe(
  //       (response) => {
  //         if (this.highestRatedReview && this.highestRatedReview.userId === userId) {
  //           this.highestRatedReview.userName = response?.firstName || 'Unknown User';
  //         }
  //       },
  //       (error) => {
  //         console.error(`Error fetching user name for userId ${userId}:`, error);
  //         if (this.highestRatedReview && this.highestRatedReview.userId === userId) {
  //           this.highestRatedReview.userName = 'Unknown User';
  //         }
  //       }
  //     );
  // }

  openPopup() {
    this.showPopup = true;
  }

  closePopup() {
    this.showPopup = false;
  }

  exportToExcel() {
    if (this.reviews && this.reviews.length > 0) {
      const formattedReviews = this.reviews.map((review) => ({
        'User ID': review.userId,
        'User Name': review.userName || 'Unknown',
        'Product Name': review.productName,
        'Rating': review.rating,
        'Review': review.review,
        'Added Date': review.reviewCreatedOn,
      }));

      const worksheet = XLSX.utils.json_to_sheet(formattedReviews);
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Product Reviews');
      const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      this.saveAsExcelFile(excelBuffer, `product_${this.productId}_reviews`);
    } else {
      // alert('No reviews available for this product to export.');
    }
  }

  saveAsExcelFile(buffer: any, fileName: string): void {
    const data: Blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8',
    });
    saveAs(data, fileName + '.xlsx');
  }
}