package com.cts.service;
 
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.RequestDTO;
import com.cts.dto.ResetPasswordDTO;
import com.cts.dto.ResponseDTO;
import com.cts.dto.SignInRequest;
import com.cts.dto.SignInResponse;
import com.cts.entity.Products;
import com.cts.entity.User;
import com.cts.exception.InvalidCredentialsException;
import com.cts.exception.InvalidInputException;
import com.cts.exception.EmailNotVerifiedException;
import com.cts.exception.UserNotFoundException;
import com.cts.mapper.UserAdminMapper;
import com.cts.mapper.UserMapper;
import com.cts.repository.UserRepository;
import com.cts.util.PasswordUtil;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
 
@Service
public class UserService {
 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil util;
    @Autowired
    UserValidationService userValidationService;
    @Autowired
    private UserMapper userMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.keyPrefix}")
    private String s3KeyPrefix;
 
//   
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public UserService(@Value("${aws.accessKeyId}") String accessKey,@Value("${aws.secretKeyId}") String secretKey) {
    	
 
    	
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // Replace with your AWS Region if different
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        this.s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
    
    
   //Retrieve user details by ID.
   public ResponseDTO getUserDetailsById(int userId) {
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
       return userMapper.toDTO(user);
   }
 
   
   //Retrieve user ID by email.
   public Integer getUserIdByEmail(String email) {
       User user = userRepository.findByEmail(email);
       if (user == null) {
           throw new UserNotFoundException("User not found with email: " + email);
       }
       return user.getUserID();
   }
   
   
   public String getUserEmailById(int id) {
	   Optional<User> optionalUser = userRepository.findById(id);
	   User user = optionalUser.orElseThrow(() -> new RuntimeException("User with email " + id + " not found."));
       return user.getEmail();
   }
 
   
   //Retrieve user image by ID.
   public byte[] getUserImage(int userId) {
       User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Product not found"));
       String s3Key = user.getPhoto().substring(user.getPhoto().lastIndexOf('/') + 1); // Extract the key
       return downloadImageFromS3(s3Key);
   }
   
   private byte[] downloadImageFromS3(String key) {
       GetObjectRequest getObjectRequest = GetObjectRequest.builder()
               .bucket(bucketName)
               .key(s3KeyPrefix + key)
               .build();
       try {
           ResponseBytes<software.amazon.awssdk.services.s3.model.GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
           return responseBytes.asByteArray();
       } catch (S3Exception e) {
           System.err.println("Error downloading image from S3: " + e.getMessage());
           return new byte[0];
       }
   }
//   
//   public byte[] getUserImage(int userId) {
//       User user = userRepository.findById(userId)
//               .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
//       String s3Key = user.getImageUrl().substring(user.getImageUrl().lastIndexOf('/')+1);
//       return downloadImageFromS3(s3Key);
//   }

   private String uploadFileToS3(MultipartFile file) throws IOException {
		String originalFilename = file.getOriginalFilename();
		String key = s3KeyPrefix + originalFilename; // Use originalFilename as the key.
		PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucketName).key(key)
				.contentType(file.getContentType()).build();
		try {
			s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
			return originalFilename; // Return the original filename.
		} catch (S3Exception e) {
			throw new IOException("Could not upload image to S3: " + e.getMessage());
		}
	}
    //Update existing user details.
    public User updateUserDetails(
            int userId,
            String firstName,
            String lastName,
            String email,
            MultipartFile imageFile,
            String nickName,
            String addressLine1,
            String addressLine2,
            String postalCodeStr, // Changed to String
            String contactNumber,
            String dateOfBirthStr, // Changed to String
            Boolean isActive,
            String password
    ) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        String s3Key = uploadFileToS3(imageFile);
		String imageUrl = String.format("https://%s.s3.us-east-1.amazonaws.com/%s%s", bucketName, s3KeyPrefix, s3Key);
 
        if (password != null) {
            throw new InvalidInputException("You cannot change the password directly through the update profile. Please use the 'reset password' functionality.");
        }
 
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setPhoto(imageUrl);
        } else if (imageFile != null && imageFile.isEmpty()) {
            user.setPhoto(null);
        }
        if (nickName != null) user.setNickName(nickName);
        if (addressLine1 != null) user.setAddressLine1(addressLine1);
        if (addressLine2 != null) user.setAddressLine2(addressLine2);
        if (postalCodeStr != null) {
            user.setPostalCode(postalCodeStr);
        }
       
        if (contactNumber != null) user.setContactNumber(contactNumber);
        if (dateOfBirthStr != null) {
             user.setDateOfBirth(dateOfBirthStr);
        }
        if (isActive != null) user.setActive(isActive);
        
        String combinedAddress = (user.getAddressLine1() != null ? user.getAddressLine1() : "") + " "
                + (user.getAddressLine2() != null ? user.getAddressLine2() : "")
                + (user.getPostalCode() != null ? "-" + user.getPostalCode() : "");
        user.setAddress(combinedAddress.trim());
        
        userValidationService.validateOnUpdate(user);
        return userRepository.save(user);
    }
   
   //Create a new user.
    public ResponseDTO createUser(RequestDTO requestDTO, MultipartFile imageFile) throws IOException {
        User user = userMapper.toEntity(requestDTO);
        String s3Key = uploadFileToS3(imageFile);
		String imageUrl = String.format("https://%s.s3.us-east-1.amazonaws.com/%s%s", bucketName, s3KeyPrefix, s3Key);
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setPhoto(imageUrl);
        }
        userValidationService.validate(user);
        user.setPassword(util.hashPassword(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }
 
   //Retrieve all users.
   public List<ResponseDTO> getAllUsers() {
       return userRepository.findAll().stream()
               .map(userMapper::toDTO)
               .collect(Collectors.toList());
   }
    
   
   //sign in response
    public SignInResponse validateUser(SignInRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null)
        {
            throw new UserNotFoundException("User not found");
        }
        if ( !util.checkPassword(request.getPassword(), user.getPassword()))
        {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        if (!user.isActive())
        {
            throw new EmailNotVerifiedException("Account is not active. Please verify your email.");
        }
        return new SignInResponse("Login Successfull",true,user.getEmail());
    }
    
    // Method to reset password
    public String resetPassword(ResetPasswordDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {

        	throw new UserNotFoundException("User not found");
        }
 
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return "Passwords do not match!";
        }
 
        user.setPassword(PasswordUtil.hashPassword(dto.getNewPassword()));
        userRepository.save(user);
 
        return "Password updated successfully!";
    }
 
    // Verify Email and Activate User
    public String verifyEmail(String email)
    {
        User user = userRepository.findByEmail(email);
        if (user == null)
        {
            return "User not found!";
        }
 
        user.setEmailVerification(true);
        user.setActive(true);
        userRepository.save(user);
 
        return "Email verified successfully! Account is now active.";
    }
    
    public String generateResetLink(String email)
    {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "User not found";
        }
        return "http://127.0.0.1:3000/reset-page?email=" + email;
    }
}
 