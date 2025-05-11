package com.cts.dto;
 
import java.sql.Timestamp;
 
import com.cts.entity.Products;
import com.cts.entity.ReviewsAndRatings;
import com.cts.entity.User;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewAndRatingDTO {
	private long ratingId;
    private int productid;
    private String productName;
    private String imageUrl;
    private int userId;
    private double rating;
    private String review;
    private Timestamp reviewCreatedOn;
    private Timestamp reviewUpdateOn;
    private Timestamp reviewDeletedOn;
    private boolean reviewActiveStatus;
 
    public ReviewAndRatingDTO(ReviewsAndRatings review) {
        this.ratingId = review.getRatingId();
        this.productid = review.getProducts().getProductid();
        this.userId = review.getUser().getUserID();
        this.productName = review.getProducts().getName();
        this.imageUrl = review.getProducts().getImageUrl();
        this.rating = review.getRating();
        this.review = review.getReview();
        this.reviewCreatedOn = review.getReviewCreatedOn();
        this.reviewUpdateOn = review.getReviewUpdateOn();
        //this.reviewDeletedOn = review.getReviewDeletedOn();
        this.reviewActiveStatus = review.isReviewActiveStatus();
    }
 
    
}