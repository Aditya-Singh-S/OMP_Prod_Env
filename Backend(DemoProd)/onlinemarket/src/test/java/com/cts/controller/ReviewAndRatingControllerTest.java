
package com.cts.controller;

import com.cts.dto.ReviewAndRatingDTO;
import com.cts.entity.Products;
import com.cts.entity.ReviewsAndRatings;
import com.cts.entity.User;
import com.cts.exception.InvalidInputException;
import com.cts.service.ReviewAndRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewAndRatingControllerTest {

    @InjectMocks
    private ReviewAndRatingController controller;

    @Mock
    private ReviewAndRatingService reviewService;

    private String authHeader = "Basic dXNlcjpwYXNz"; // Base64 of user:pass

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReview_Success() {
        ReviewsAndRatings mockReview = new ReviewsAndRatings();
        when(reviewService.createReview(1, 2, 4.5, "Good product"))
                .thenReturn(mockReview);

        ResponseEntity<ReviewsAndRatings> response = controller.createReview(authHeader, 1, 2, 4.5, "Good product");
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(mockReview, response.getBody());
    }

    @Test
    public void testCreateReview_MissingRating() {
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> controller.createReview(authHeader, 1, 2, null, "Nice")
        );
        assertEquals("Required parameter is missing : rating", exception.getMessage());
    }

    @Test
    public void testUpdateReview_Success() {
        ReviewsAndRatings mockUpdated = new ReviewsAndRatings();
        when(reviewService.updateReview( 1L, 2, true))
                .thenReturn(mockUpdated);

        ResponseEntity<ReviewsAndRatings> response = controller.updateReview(authHeader, 1L, 2, true);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockUpdated, response.getBody());
    }

    @Test
    public void testGetAllUserReviews_ValidUserId() {
        List<ReviewAndRatingDTO> list = Arrays.asList(new ReviewAndRatingDTO());
        when(reviewService.getAllReviewsByUserId(2)).thenReturn(list);

        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllUserReviews(authHeader, 2);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetUserReviews_InvalidUserId() {
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> controller.getUserReviews(authHeader, null)
        );
        assertEquals("Invalid User Id", exception.getMessage());
    }

    @Test
    public void testGetAllReviews() {
        List<ReviewAndRatingDTO> list = Arrays.asList(new ReviewAndRatingDTO());
        when(reviewService.getAllReviews()).thenReturn(list);

        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllReviews();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllReviews_EmptyList() {
        when(reviewService.getAllReviews()).thenReturn(List.of());

        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllReviews();
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    public void testGetProductReviews_Success() {
        List<ReviewAndRatingDTO> list = Arrays.asList(new ReviewAndRatingDTO());
        when(reviewService.getReviewsByProductId(101)).thenReturn(list);

        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getProductReviews(101);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
    }

    
//@Test
//public void testGetHighestRatedReview_Found() {
//    ReviewsAndRatings review = new ReviewsAndRatings();
//    review.setRating(5.0);
//    when(reviewService.getHighestRatedReview(101)).thenReturn(review);
//
//    ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(101);
//    assertEquals(200, response.getStatusCodeValue());
//    assertNotNull(response.getBody());
//}
    
//    @Test
//    public void testGetHighestRatedReview_Found() {
//        ReviewsAndRatings review = new ReviewsAndRatings();
//        review.setRating(5.0);
//        review.setReview("This is the highest rated review.");
//        review.setReviewActiveStatus(true);
//        review.setRatingId(1L);
//
//        User user = new User();
//        user.setUserID(1);
//        review.setUser(user);
//
//        Products product = new Products();
//        product.setProductid(101);
//        review.setProducts(product);
//
//        when(reviewService.getHighestRatedReview(101)).thenReturn(review);
//
//        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(101);
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//
//        ReviewAndRatingDTO responseDTO = response.getBody();
//        assertEquals(review.getRating(), responseDTO.getRating());
//        assertEquals(review.getReview(), responseDTO.getReview());
//        //assertEquals(review.getProducts().getProductid(), responseDTO.getProductid());
//        assertEquals(review.getUser().getUserID(), responseDTO.getUserId());
//        assertEquals(review.getRatingId(), responseDTO.getRatingId());
//       // assertEquals(review.isReviewActiveStatus(), responseDTO.isReviewActiveStatus());
//    }

    @Test
    public void testGetHighestRatedReview_NotFound() {
        when(reviewService.getHighestRatedReview(101)).thenReturn(null);

        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(101);
        assertEquals(404, response.getStatusCodeValue());
    }
}