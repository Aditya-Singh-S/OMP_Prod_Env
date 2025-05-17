package com.cts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.dto.ReviewAndRatingDTO;
import com.cts.entity.ReviewsAndRatings;
import com.cts.exception.InvalidInputException;
import com.cts.service.ReviewAndRatingService;

import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:3000")
@RequestMapping("/OMP/reviews")
public class ReviewAndRatingController {

	@Autowired
	private ReviewAndRatingService reviewService;

	// Create Review (All parameters as RequestParam)//reviewActiveStatus modify as
	// user should not assign that
	@PostMapping("/createReview")
	public ResponseEntity<ReviewsAndRatings> createReview(
			@RequestHeader("Authorization") String authHeaders,
			@RequestParam int productId, @RequestParam int userId,
			@RequestParam(required = false) Double rating, @RequestParam(required = false) String review) {
//        try {
//            ReviewsAndRatings savedReview = reviewService.createReview(productId, userId, rating, review,reviewActiveStatus);
//            return ResponseEntity.status(201).body(savedReview);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(null);
//        }

		if (rating == null)
			throw new InvalidInputException("Required parameter is missing : rating");
		else if (review == null)
			throw new InvalidInputException("Required parameter is missing : review");
		ReviewsAndRatings savedReview = reviewService.createReview(productId, userId, rating, review);
		
		this.checkAuthorizationHeaders(authHeaders);
		
		return ResponseEntity.status(201).body(savedReview);
	}

	// Update Review // Modify if user don't want to update rating but review only
	@PutMapping("/updateReview")
	public ResponseEntity<ReviewsAndRatings> updateReview(
			@RequestHeader("Authorization") String authHeaders,
			@RequestParam Long ratingId,
			@RequestParam(required=false) Integer userId,
			@RequestParam(required = false) Boolean reviewActiveStatus

	) {

		ReviewsAndRatings updatedReview = reviewService.updateReview(ratingId, userId,reviewActiveStatus);
		
		this.checkAuthorizationHeaders(authHeaders);
		
		return ResponseEntity.ok(updatedReview);

	}
	
	@GetMapping("/all/user/{userId}")
	public ResponseEntity<List<ReviewAndRatingDTO>> getAllUserReviews(
			@RequestHeader("Authorization") String authHeaders,
			@PathVariable (required=true) Integer userId) {
		if(userId == null )
			throw new InvalidInputException("Invalid User Id");
		
		List<ReviewAndRatingDTO> userReviews = reviewService.getAllReviewsByUserId(userId);
		
		this.checkAuthorizationHeaders(authHeaders);
		
		return ResponseEntity.ok(userReviews);
	}

	@GetMapping("/getReviews")
	public ResponseEntity<List<ReviewAndRatingDTO>> getAllReviews() {
		List<ReviewAndRatingDTO> reviews = reviewService.getAllReviews();
		if (reviews.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(reviews);
	}
	// Add reviews based on user ID

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ReviewAndRatingDTO>> getUserReviews(
			@RequestHeader("Authorization") String authHeaders,
			@PathVariable (required=true) Integer userId) {

		if(userId == null )
			throw new InvalidInputException("Invalid User Id");
		
		List<ReviewAndRatingDTO> userReviews = reviewService.getReviewsByUserId(userId);
		
		this.checkAuthorizationHeaders(authHeaders);
		
		return ResponseEntity.ok(userReviews);
		
		
	}

	// Get reviews for a particular product
	@GetMapping("/getSpecificProductReviews")
	public ResponseEntity<List<ReviewAndRatingDTO>> getProductReviews(@RequestParam int productId) {
		try {
			List<ReviewAndRatingDTO> productReviews = reviewService.getReviewsByProductId(productId);
			return ResponseEntity.ok(productReviews);
		} catch (InvalidInputException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/highestRatedReview")
	public ResponseEntity<ReviewAndRatingDTO> getHighestRatedReview(@RequestParam int productId) {

		ReviewsAndRatings review = reviewService.getHighestRatedReview(productId);
		if (review != null) {
			ReviewAndRatingDTO reviewDTO = new ReviewAndRatingDTO(review);
			return ResponseEntity.ok(reviewDTO);
		}
		return ResponseEntity.status(404).body(null);

	}

	public void checkAuthorizationHeaders(String authHeaders) {
    	if (authHeaders != null && authHeaders.startsWith("Basic ")) {
            String base64Credentials = authHeaders.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedString = new String(decodedBytes);
 
            // Split username and password
            String[] credentials = decodedString.split(":", 2);
            String username = credentials[0];
            String password = credentials[1];
            	
            System.out.println(username);
            System.out.println(password);
        } else {
        	System.out.println("Invalid Authorization headers");
        }
    }
	
}
