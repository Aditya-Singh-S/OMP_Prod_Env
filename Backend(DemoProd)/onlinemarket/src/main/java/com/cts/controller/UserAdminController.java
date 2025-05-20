package com.cts.controller;
 
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
@CrossOrigin(origins = "http://127.0.0.1:3000")
@RequestMapping("/OMP")
public class UserAdminController {
 
    @Autowired
    private UserAdminService userAdminService;
    
    @Autowired
    private ReviewAndRatingService reviewService;
    
	@Autowired
	ProductService productService;
    
    
    @GetMapping("/admin")
    public String getUserByEmailId(
    		@RequestHeader("Authorization") String authHeaders,
    		@RequestParam String email){
    	User user = userAdminService.searchUserByEmailId(email);
    	this.checkAuthorizationHeaders(authHeaders);
    	return user.getFirstName();
    }
    
 
    @PostMapping("/admin/register")
    public ResponseEntity<User> createUser(
    		@RequestHeader("Authorization") String authHeaders,
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
        
        this.checkAuthorizationHeaders(authHeaders);
 
        return ResponseEntity.ok(savedUser);
    }
 
    
    @GetMapping("/admin/viewProfile")
    public ResponseEntity<User> searchUserByEmailId(
    		@RequestHeader("Authorization") String authHeaders,
    		@RequestParam String email){
    	User user = userAdminService.searchUserByEmailId(email);
    	
    	this.checkAuthorizationHeaders(authHeaders);
    	
    	return ResponseEntity.ok(user);
    }
    
    
    @PutMapping("/admin/updateProfile")
    public ResponseEntity<User> updateUser(
    		@RequestHeader("Authorization") String authHeaders,
            @RequestParam String email,
            @RequestParam(required = false) Boolean isActive
    ) throws IOException, ParseException {
        User updatedEntity = userAdminService.updateUserByEmail(isActive, email);
        
        this.checkAuthorizationHeaders(authHeaders);
        
        return ResponseEntity.ok(updatedEntity);
    }
    
	@GetMapping("/admin/viewProductSubscription")
	public ResponseEntity<?> getSubscriptionsByEmail(
			@RequestHeader("Authorization") String authHeaders,
			@RequestParam String email) {
	    try {
	        List<ProductSubscription> subscriptions = userAdminService.getSubscriptionsByEmail(email);
	        
	        this.checkAuthorizationHeaders(authHeaders);
	        
	        return ResponseEntity.ok(subscriptions);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}
	
	@GetMapping("/admin/viewProductReviews")
	public ResponseEntity<?> getReviewsByEmail(
			@RequestHeader("Authorization") String authHeaders,
			@RequestParam String email) {
	    try {
	        List<ReviewsAndRatings> reviews = userAdminService.getReviewsByEmail(email);
	        
	        this.checkAuthorizationHeaders(authHeaders);
	        
	        return ResponseEntity.ok(reviews);
	    } catch (UserNotFoundException e) {
	    	
	    	this.checkAuthorizationHeaders(authHeaders);
	    	
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    }
	}
	
	@PutMapping("/admin/updateSubscriptionByEmail")
	public ResponseEntity<ProductSubscription> updateSubscription(
			@RequestHeader("Authorization") String authHeaders,
	    @RequestParam String email,
	    @RequestParam int subscriptionId,
	    @RequestParam boolean optInStatus
	) {
	    try {
	        ProductSubscription updatedSubscription = userAdminService.updateSubscriptionByEmail(email, subscriptionId, optInStatus);
	        
	        this.checkAuthorizationHeaders(authHeaders);
	        
	        return ResponseEntity.ok(updatedSubscription);
	    } catch (RuntimeException e) {
	    	
	    	 this.checkAuthorizationHeaders(authHeaders);
	    	 
	        return ResponseEntity.badRequest().body(null);
	    }
	}
	
    @PutMapping("/admin/updateReviewByEmail")
    public ResponseEntity<ReviewsAndRatings> updateReviewByEmail(
    		@RequestHeader("Authorization") String authHeaders,
        @RequestParam String email,
        @RequestParam Long ratingId,
        @RequestParam(required = false) Double rating,
        @RequestParam(required = false) String review,
        @RequestParam(required = false) Boolean reviewActiveStatus  
    ) {
        try {
            ReviewsAndRatings updatedReview = userAdminService.updateReviewByUserEmail(email, ratingId, rating, review, reviewActiveStatus);
            
            this.checkAuthorizationHeaders(authHeaders);
            
            return ResponseEntity.ok(updatedReview);
        } catch (RuntimeException e) {
        	
        	this.checkAuthorizationHeaders(authHeaders);
        	
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/admin/exportUsers")
    public ResponseEntity<byte[]> exportUsersToExcel(
    		@RequestHeader("Authorization") String authHeaders
    		) throws IOException {
    	
        byte[] excelFile = userAdminService.generateExcelFileWithAllUsers();
    	
    	  this.checkAuthorizationHeaders(authHeaders);
    	  
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=users.xlsx")
            .body(excelFile);
    }
    
    //geting al user
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDetailDTO>> getAllUsersForAdmin(
    		@RequestHeader("Authorization") String authHeaders
    		) {
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
                        user.getUserRole().name(),
                        user.isEmailVerification()
                ))
                .collect(Collectors.toList());
        
        this.checkAuthorizationHeaders(authHeaders);
 
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    
 
    
  //getting all user with filter
    @GetMapping("/admin/users/filter")
    public ResponseEntity<List<UserDetailDTO>> getUsersByFilter(
    		@RequestHeader("Authorization") String authHeaders,
    		@RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "emailVerification", required = false) Boolean isEmailVerified) {
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
                        user.getUserRole().name(),
                        user.isEmailVerification()
                ))
                .collect(Collectors.toList());
        
        this.checkAuthorizationHeaders(authHeaders);
 
        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/admin/users/active")
    public ResponseEntity<List<UserDetailDTO>> getUsersByActiveStatus(
            @RequestHeader("Authorization") String authHeaders,
            @RequestParam("isActive") boolean isActive) {
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
                        user.getUserRole().name(),
                        user.isEmailVerification() // Assuming your User entity has this field
                ))
                .collect(Collectors.toList());

        this.checkAuthorizationHeaders(authHeaders);

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/admin/users/verified")
    public ResponseEntity<List<UserDetailDTO>> getUsersByActiveStatusbyemail(
            @RequestHeader("Authorization") String authHeaders,
            @RequestParam("emailVerification") boolean emailVerification) {
        List<User> users = userAdminService.getUsersByIsActivebyemail(emailVerification);

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
                        user.getUserRole().name(),
                        user.isEmailVerification() // Assuming your User entity has this field
                ))
                .collect(Collectors.toList());

        this.checkAuthorizationHeaders(authHeaders);

        return new ResponseEntity<>(userDTOs, HttpStatus.OK);
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