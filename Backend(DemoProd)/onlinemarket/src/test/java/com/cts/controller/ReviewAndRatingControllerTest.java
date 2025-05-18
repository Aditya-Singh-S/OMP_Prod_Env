//package com.cts.controller;
// 
//import com.cts.dto.ReviewAndRatingDTO;
//import com.cts.entity.ReviewsAndRatings;
//import com.cts.exception.InvalidInputException;
//import com.cts.service.ReviewAndRatingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.http.ResponseEntity;
// 
//import java.sql.Timestamp;
//import java.util.Arrays;
//import java.util.List;
// 
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.mockito.MockitoAnnotations.openMocks;
// 
//public class ReviewAndRatingControllerTest {
// 
//    @InjectMocks
//    private ReviewAndRatingController controller;
// 
//    @Mock
//    private ReviewAndRatingService reviewService;
// 
//    private ReviewsAndRatings dummyReview;
// 
//    @BeforeEach
//    public void setUp() {
//        openMocks(this);
//        dummyReview = new ReviewsAndRatings();
//        dummyReview.setRatingId(1L);
//        dummyReview.setRating(4.5);
//        dummyReview.setReview("Great Product");
//        dummyReview.setReviewCreatedOn(new Timestamp(System.currentTimeMillis()));
//        dummyReview.setReviewActiveStatus(true);
//    }
// 
//    @Test
//    public void testCreateReviewSuccess() {
//        when(reviewService.createReview(1, 2, 4.5, "Great Product")).thenReturn(dummyReview);
// 
//        ResponseEntity<ReviewsAndRatings> response = controller.createReview(
//                "Basic dGVzdDp0ZXN0", 1, 2, 4.5, "Great Product");
// 
//        assertEquals(201, response.getStatusCodeValue());
//        assertEquals("Great Product", response.getBody().getReview());
//    }
// 
//    @Test
//    public void testCreateReviewMissingRating() {
//        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
//            controller.createReview("Basic dGVzdDp0ZXN0", 1, 2, null, "Good");
//        });
//        assertEquals("Required parameter is missing : rating", exception.getMessage());
//    }
// 
//    @Test
//    public void testGetAllUserReviews() {
//        ReviewAndRatingDTO dto = new ReviewAndRatingDTO();
//        dto.setUserId(2);
//        dto.setRating(4.0);
//        dto.setReview("Nice one");
// 
//        when(reviewService.getAllReviewsByUserId(2)).thenReturn(List.of(dto));
// 
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllUserReviews("Basic auth", 2);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(1, response.getBody().size());
//        assertEquals("Nice one", response.getBody().get(0).getReview());
//    }
// 
//    @Test
//    public void testGetAllReviewsNoContent() {
//        when(reviewService.getAllReviews()).thenReturn(List.of());
// 
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllReviews();
// 
//        assertEquals(204, response.getStatusCodeValue());
//        assertNull(response.getBody());
//    }
// 
//    @Test
//    public void testGetProductReviewsInvalidId() {
//        when(reviewService.getReviewsByProductId(0)).thenThrow(new InvalidInputException("Invalid product id"));
// 
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getProductReviews(0);
// 
//        assertEquals(400, response.getStatusCodeValue());
//    }
//}
// 

//package com.cts.controller;
//
//import com.cts.dto.ReviewAndRatingDTO;
//import com.cts.entity.ReviewsAndRatings;
//import com.cts.exception.InvalidInputException;
//import com.cts.service.ReviewAndRatingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.sql.Timestamp;
//import java.util.Base64;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//import static org.mockito.MockitoAnnotations.openMocks;
//
//public class ReviewAndRatingControllerTest {
//
//    @InjectMocks
//    private ReviewAndRatingController controller;
//
//    @Mock
//    private ReviewAndRatingService reviewService;
//
//    private ReviewsAndRatings dummyReview;
//    private String authHeaders;
//
//    @BeforeEach
//    public void setUp() {
//        openMocks(this);
//        // Mock the HttpServletRequest for RequestParam handling
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//        authHeaders = "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes());
//
//        dummyReview = new ReviewsAndRatings();
//        dummyReview.setRatingId(1L);
//        dummyReview.setRating(4.5);
//        dummyReview.setReview("Great Product");
//        dummyReview.setReviewCreatedOn(new Timestamp(System.currentTimeMillis()));
//        dummyReview.setReviewActiveStatus(true);
//    }
//
//    @Test
//    public void testCreateReviewSuccess() {
//        when(reviewService.createReview(1, 2, 4.5, "Great Product")).thenReturn(dummyReview);
//
//        ResponseEntity<ReviewsAndRatings> response = controller.createReview(
//                authHeaders, 1, 2, 4.5, "Great Product");
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Great Product", response.getBody().getReview());
//        verify(reviewService, times(1)).createReview(1, 2, 4.5, "Great Product");
//    }
//
//    @Test
//    public void testCreateReviewMissingRating() {
//        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
//            controller.createReview(authHeaders, 1, 2, null, "Good");
//        });
//        assertEquals("Required parameter is missing : rating", exception.getMessage());
//        verify(reviewService, never()).createReview(anyInt(), anyInt(), isNull(), anyString());
//    }
//
//    @Test
//    public void testCreateReviewMissingReview() {
//        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
//            controller.createReview(authHeaders, 1, 2, 3.0, null);
//        });
//        assertEquals("Required parameter is missing : review", exception.getMessage());
//        verify(reviewService, never()).createReview(anyInt(), anyInt(), anyDouble(), isNull());
//    }
//
//    @Test
//    public void testUpdateReviewSuccess() {
//        ReviewsAndRatings updatedReview = new ReviewsAndRatings();
//        updatedReview.setRatingId(1L);
//        updatedReview.setRating(4.0);
//        updatedReview.setReview("Updated Review");
//        updatedReview.setReviewActiveStatus(false);
//
//        when(reviewService.updateReview(1L, 2, 4.0, "Updated Review", false)).thenReturn(updatedReview);
//
//        ResponseEntity<ReviewsAndRatings> response = controller.updateReview(
//                authHeaders, 1L, 2, 4.0, "Updated Review", false);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Updated Review", response.getBody().getReview());
//        assertFalse(response.getBody().isReviewActiveStatus());
//        verify(reviewService, times(1)).updateReview(1L, 2, 4.0, "Updated Review", false);
//    }
//
//    @Test
//    public void testGetAllUserReviews() {
//        ReviewAndRatingDTO dto1 = new ReviewAndRatingDTO();
//        dto1.setUserId(2);
//        dto1.setRating(4.0);
//        dto1.setReview("Nice one");
//
//        ReviewAndRatingDTO dto2 = new ReviewAndRatingDTO();
//        dto2.setUserId(2);
//        dto2.setRating(5.0);
//        dto2.setReview("Excellent");
//
//        when(reviewService.getAllReviewsByUserId(2)).thenReturn(List.of(dto1, dto2));
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllUserReviews(authHeaders, 2);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//        assertEquals("Nice one", response.getBody().get(0).getReview());
//        assertEquals("Excellent", response.getBody().get(1).getReview());
//        verify(reviewService, times(1)).getAllReviewsByUserId(2);
//    }
//
//    @Test
//    public void testGetAllUserReviewsInvalidUserId() {
//        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
//            controller.getAllUserReviews(authHeaders, null);
//        });
//        assertEquals("Invalid User Id", exception.getMessage());
//        verify(reviewService, never()).getAllReviewsByUserId(anyInt());
//    }
//
//    @Test
//    public void testGetAllReviewsSuccess() {
//        ReviewAndRatingDTO dto1 = new ReviewAndRatingDTO();
//        dto1.setRating(3.5);
//        dto1.setReview("Okay");
//        ReviewAndRatingDTO dto2 = new ReviewAndRatingDTO();
//        dto2.setRating(4.8);
//        dto2.setReview("Superb");
//
//        when(reviewService.getAllReviews()).thenReturn(List.of(dto1, dto2));
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllReviews();
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(2, response.getBody().size());
//        assertEquals("Okay", response.getBody().get(0).getReview());
//        assertEquals("Superb", response.getBody().get(1).getReview());
//        verify(reviewService, times(1)).getAllReviews();
//    }
//
//    @Test
//    public void testGetAllReviewsNoContent() {
//        when(reviewService.getAllReviews()).thenReturn(List.of());
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getAllReviews();
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(reviewService, times(1)).getAllReviews();
//    }
//
//    @Test
//    public void testGetUserReviewsSuccess() {
//        ReviewAndRatingDTO dto1 = new ReviewAndRatingDTO();
//        dto1.setUserId(3);
//        dto1.setRating(4.2);
//        dto1.setReview("Good");
//
//        when(reviewService.getReviewsByUserId(3)).thenReturn(List.of(dto1));
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getUserReviews(authHeaders, 3);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, response.getBody().size());
//        assertEquals("Good", response.getBody().get(0).getReview());
//        verify(reviewService, times(1)).getReviewsByUserId(3);
//    }
//
//    @Test
//    public void testGetUserReviewsInvalidUserId() {
//        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
//            controller.getUserReviews(authHeaders, null);
//        });
//        assertEquals("Invalid User Id", exception.getMessage());
//        verify(reviewService, never()).getReviewsByUserId(anyInt());
//    }
//
//    @Test
//    public void testGetProductReviewsSuccess() {
//        ReviewAndRatingDTO dto1 = new ReviewAndRatingDTO();
//        dto1.setProductid(4);
//        dto1.setRating(3.8);
//        dto1.setReview("Decent");
//
//        when(reviewService.getReviewsByProductId(4)).thenReturn(List.of(dto1));
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getProductReviews(4);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(1, response.getBody().size());
//        assertEquals("Decent", response.getBody().get(0).getReview());
//        verify(reviewService, times(1)).getReviewsByProductId(4);
//    }
//
//    @Test
//    public void testGetProductReviewsInvalidId() {
//        when(reviewService.getReviewsByProductId(0)).thenThrow(new InvalidInputException("Please enter valid product Id"));
//
//        ResponseEntity<List<ReviewAndRatingDTO>> response = controller.getProductReviews(0);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(reviewService, times(1)).getReviewsByProductId(0);
//    }
//
//    @Test
//    public void testGetHighestRatedReviewSuccess() {
//        ReviewAndRatingDTO dto = new ReviewAndRatingDTO(dummyReview);
//        when(reviewService.getHighestRatedReview(5)).thenReturn(dummyReview);
//
//        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(5);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(4.5, response.getBody().getRating());
//        assertEquals("Great Product", response.getBody().getReview());
//        verify(reviewService, times(1)).getHighestRatedReview(5);
//    }
//
//    @Test
//    public void testGetHighestRatedReviewNotFound() {
//        when(reviewService.getHighestRatedReview(6)).thenReturn(null);
//
//        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(6);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(reviewService, times(1)).getHighestRatedReview(6);
//    }
//
//    @Test
//    public void testCheckAuthorizationHeadersValid() {
//        // This test primarily checks that no exception is thrown for a valid header.
//        // Output to System.out is not easily testable in a standard unit test.
//        assertDoesNotThrow(() -> controller.checkAuthorizationHeaders("Basic dXNlcjpwYXNzd29yZA=="));
//    }
//
//    @Test
//    public void testCheckAuthorizationHeadersInvalidFormat() {
//        assertDoesNotThrow(() -> controller.checkAuthorizationHeaders("Bearer token"));
//    }
//
//    @Test
//    public void testCheckAuthorizationHeadersNull() {
//        assertDoesNotThrow(() -> controller.checkAuthorizationHeaders(null));
//    }
//}



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
        when(reviewService.updateReview(1L, 2, 5.0, "Excellent", true))
                .thenReturn(mockUpdated);
 
        ResponseEntity<ReviewsAndRatings> response = controller.updateReview(authHeader, 2, 5.0, "Excellent", true);
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
 
    @Test
    public void testGetHighestRatedReview_Found() {
        ReviewsAndRatings review = new ReviewsAndRatings();
        review.setRating(5.0);
        when(reviewService.getHighestRatedReview(101)).thenReturn(review);
 
        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(101);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
    
//    @Test
//    public void testGetHighestRatedReview_Found() {
//        ReviewsAndRatings review = new ReviewsAndRatings();
//        review.setRating(5.0);
//        review.setReview("This is the highest rated review.");
//        review.setReviewActiveStatus(true);
//        review.setRatingId(1L);
//
//        // Create mock User and Product objects
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
//        assertEquals(review.getProducts().getProductid(), responseDTO.getProductid()); // Access product ID through the 'products' relationship
//        assertEquals(review.getUser().getUserID(), responseDTO.getUserId());       // Access user ID through the 'user' relationship
//        assertEquals(review.getRatingId(), responseDTO.getRatingId());
//        assertEquals(review.isReviewActiveStatus(), responseDTO.isReviewActiveStatus());
//    }
 
    @Test
    public void testGetHighestRatedReview_NotFound() {
        when(reviewService.getHighestRatedReview(101)).thenReturn(null);
 
        ResponseEntity<ReviewAndRatingDTO> response = controller.getHighestRatedReview(101);
        assertEquals(404, response.getStatusCodeValue());
    }
}