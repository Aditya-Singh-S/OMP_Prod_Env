package com.cts.controller;
 
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import com.cts.dto.UserAdminDTO;
import com.cts.dto.UserDetailDTO;
import com.cts.entity.ProductSubscription;
import com.cts.entity.Products;
import com.cts.entity.ReviewsAndRatings;
import com.cts.entity.User;
import com.cts.exception.UserNotFoundException;
import com.cts.service.ProductService;
import com.cts.service.ReviewAndRatingService;
import com.cts.service.UserAdminService;
 
@RestController
@RequestMapping("/OMP")
public class UserAdminController {
 
    @Autowired
    private UserAdminService userAdminService;
    
    @Autowired
    private ReviewAndRatingService reviewService;
    
	@Autowired
	ProductService productService;
    
    
    @GetMapping("/admin")
    public String getUserByEmailId(@RequestParam String email){
    	User user = userAdminService.searchUserByEmailId(email);
    	return user.getFirstName();
    }
    
 
    @PostMapping("/admin/register")
    public ResponseEntity<User> createUser(
        @RequestParam String firstName,
        @RequestParam String lastName,
        @RequestParam String email,
        @RequestParam String nickName,
        @RequestParam String addressLine1,
        @RequestParam String addressLine2,
        @RequestParam String postalCode, // Changed to String
        @RequestParam String contactNumber,
        @RequestParam String dateOfBirth,
        @RequestParam boolean isAdmin,
        @RequestParam MultipartFile imageFile
    ) throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = dateFormat.parse(dateOfBirth);
 
        UserAdminDTO createUser = new UserAdminDTO();
        createUser.setFirstName(firstName);
        createUser.setLastName(lastName);
        createUser.setEmail(email);
        createUser.setNickName(nickName);
        createUser.setAddressLine1(addressLine1);
        createUser.setAddressLine2(addressLine2);
        createUser.setPostalCode(postalCode); // Changed to String
        createUser.setContactNumber(contactNumber);
        createUser.setDateOfBirth(dateOfBirth);
        createUser.setAdmin(isAdmin);
        createUser.setUserRoleBasedOnAdminFlag();
 
        User savedUser = userAdminService.createUser(createUser, imageFile);
 
        return ResponseEntity.ok(savedUser);
    }
 
    
    @GetMapping("/admin/viewProfile")
    public ResponseEntity<User> searchUserByEmailId(@RequestParam String email){
    	User user = userAdminService.searchUserByEmailId(email);
    	return ResponseEntity.ok(user);
    }
    
    
    @PutMapping("/admin/updateProfile")
    public ResponseEntity<User> updateUser(
            @RequestParam String email,
            @RequestParam(required = false) Boolean isActive
    ) throws IOException, ParseException {
        User updatedEntity = userAdminService.updateUserByEmail(isActive, email);
        return ResponseEntity.ok(updatedEntity);
    }
    
	@GetMapping("/admin/viewProductSubscription")
	public ResponseEntity<?> getSubscriptionsByEmail(@RequestParam String email) {
	    try {
	        List<ProductSubscription> subscriptions = userAdminService.getSubscriptionsByEmail(email);
	        return ResponseEntity.ok(subscriptions);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}
	
	@GetMapping("/admin/viewProductReviews")
	public ResponseEntity<?> getReviewsByEmail(@RequestParam String email) {
	    try {
	        List<ReviewsAndRatings> reviews = userAdminService.getReviewsByEmail(email);
	        return ResponseEntity.ok(reviews);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}
	
	@PutMapping("/admin/updateSubscriptionByEmail")
	public ResponseEntity<ProductSubscription> updateSubscription(
	    @RequestParam String email,
	    @RequestParam int subscriptionId,
	    @RequestParam boolean optInStatus
	) {
	    try {
	        ProductSubscription updatedSubscription = userAdminService.updateSubscriptionByEmail(email, subscriptionId, optInStatus);
	        return ResponseEntity.ok(updatedSubscription);
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(null);
	    }
	}
 
	
    @PutMapping("/admin/updateReviewByEmail")
    public ResponseEntity<ReviewsAndRatings> updateReviewByEmail(
        @RequestParam String email,
        @RequestParam Long ratingId,
        @RequestParam(required = false) Double rating,
        @RequestParam(required = false) String review,
        @RequestParam(required = false) Boolean reviewActiveStatus  
    ) {
        try {
            ReviewsAndRatings updatedReview = userAdminService.updateReviewByUserEmail(email, ratingId, rating, review, reviewActiveStatus);
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/admin/exportUsers")
    public ResponseEntity<byte[]> exportUsersToExcel() throws IOException {
        byte[] excelFile = userAdminService.generateExcelFileWithAllUsers();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=users.xlsx")
            .body(excelFile);
    }
    
    //geting al user
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDetailDTO>> getAllUsersForAdmin() {
        List<User> users = userAdminService.getAllUsers();
        List<UserDetailDTO> userDTOs = users.stream()
                .map(user -> new UserDetailDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getDateOfBirth(),
                        user.getContactNumber(),
                        Date.from(user.getAddedOn().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                        Date.from(user.getUpdatedOn().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                        user.getAddressLine1(),
                        user.getAddressLine2(),
                        user.getPostalCode(),
                        user.isActive(),
                        user.getUserRole().name()
                ))
                .collect(Collectors.toList());
 
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
 
    
    //geting  al user with filter
    @GetMapping("/admin/users/filter")
    public ResponseEntity<List<UserDetailDTO>> getUsersByActiveStatus(@RequestParam("isActive") boolean isActive) {
        List<User> users = userAdminService.getUsersByIsActive(isActive);
        List<UserDetailDTO> userDTOs = users.stream()
                .map(user -> new UserDetailDTO(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getDateOfBirth(),
                        user.getContactNumber(),
                        Date.from(user.getAddedOn().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                        Date.from(user.getUpdatedOn().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                        user.getAddressLine1(),
                        user.getAddressLine2(),
                        user.getPostalCode(),
                        user.isActive(),
                        user.getUserRole().name()
                ))
                .collect(Collectors.toList());
 
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    
    
    
	
 
}