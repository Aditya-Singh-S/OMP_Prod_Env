package com.cts.service;

import com.cts.dto.UserAdminDTO;
import com.cts.entity.ProductSubscription;
import com.cts.entity.Products;
import com.cts.entity.ReviewsAndRatings;
import com.cts.entity.User;
import com.cts.exception.UserNotFoundException;
import com.cts.mapper.UserAdminMapper;
import com.cts.repository.ProductRepository;
import com.cts.repository.ReviewAndRatingRepository;
import com.cts.repository.UserRepository;
import com.cts.util.PasswordUtil;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserAdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserValidationService userValidationService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PasswordUtil util;

	@Autowired
	private ReviewAndRatingRepository reviewRepository;

	@Value("${aws.s3.bucketName}")
	private String bucketName;
	@Value("${aws.s3.keyPrefix}")
	private String s3KeyPrefix;

//   
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;

	public UserAdminService(@Value("${aws.accessKeyId}") String accessKey,
			@Value("${aws.secretKeyId}") String secretKey) {

		AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		this.s3Client = S3Client.builder().region(Region.US_EAST_1) // Replace with your AWS Region if different
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
		this.s3Presigner = S3Presigner.builder().region(Region.US_EAST_1)
				.credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}

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

	public User createUser(UserAdminDTO userAdminDTO, MultipartFile imageFile) throws IOException {
		String s3Key = uploadFileToS3(imageFile);
		String imageUrl = String.format("https://%s.s3.amazonaws.com/%s%s", bucketName, s3KeyPrefix, s3Key);
		User user = UserAdminMapper.toEntity(userAdminDTO);
		// added if check
		if (imageFile != null && !imageFile.isEmpty()) {
			user.setPhoto(imageUrl);
		}
		if (userAdminDTO.isAdmin() == true) {
			user.setPassword(util.hashPassword(user.getPassword()));
		} else {
			user.setPassword(null);
		}
		userValidationService.validateAdminAddUser(user);

		return userRepository.save(user);
	}

	// No changes are required in the other methods; they can remain as is.
	public User searchUserByEmailId(String email) {
		return userRepository.findByEmail(email);
	}

	public User updateUserByEmail(Boolean isActive, String email) throws IOException, ParseException {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			if (isActive != null) {
				user.setActive(isActive);
			}
			return userRepository.save(user);
		} else {
			throw new RuntimeException("User with email " + email + " not found.");
		}
	}

	public List<ProductSubscription> getSubscriptionsByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found with email: " + email);
		}
		return user.getProductSubscriptionList();
	}

	public List<ReviewsAndRatings> getReviewsByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("User not found with email: " + email);
		}
		return user.getReviewAndRating();
	}

	public ReviewsAndRatings updateReviewByUserEmail(String email, Long ratingId, Double rating, String review,
			Boolean reviewActiveStatus) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("User with email " + email + " not found.");
		}
		ReviewsAndRatings existingReview = reviewRepository.findById(ratingId)
				.orElseThrow(() -> new RuntimeException("Rating not found with ID: " + ratingId));

		if (!existingReview.getUser().equals(user)) {
			throw new RuntimeException("The review does not belong to the user with email " + email);
		}
		if (rating != null) {
			existingReview.setRating(rating);
		}
		if (review != null) {
			existingReview.setReview(review);
		}
		if (reviewActiveStatus != null) {
			existingReview.setReviewActiveStatus(reviewActiveStatus);
		}
		existingReview.setReviewUpdateOn(Timestamp.from(Instant.now()));
		return reviewRepository.save(existingReview);
	}

	public ProductSubscription updateSubscriptionByEmail(String email, int subscriptionId, boolean optInStatus) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new RuntimeException("User with email " + email + " not found.");
		}

		ProductSubscription subscription = productRepository.findSubscriptionById(subscriptionId)
				.orElseThrow(() -> new RuntimeException("Subscription with ID " + subscriptionId + " not found."));

		if (!subscription.getUser().equals(user)) {
			throw new RuntimeException("The subscription does not belong to the user with email " + email);
		}

		subscription.setOptIn(optInStatus);
		subscription.setUpdatedOn(LocalDateTime.now());
		productRepository.save(subscription.getProducts());
		return subscription;
	}

	public byte[] generateExcelFileWithAllUsers() throws IOException {
		List<User> users = userRepository.findAll();
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Users");
			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("ID");
			header.createCell(1).setCellValue("First Name");
			header.createCell(2).setCellValue("Last Name");
			header.createCell(3).setCellValue("Email");
			header.createCell(4).setCellValue("Date of Birth");
			header.createCell(5).setCellValue("Address");

			int rowIdx = 1;
			for (User user : users) {
				Row row = sheet.createRow(rowIdx++);
				row.createCell(0).setCellValue(user.getUserID());
				row.createCell(1).setCellValue(user.getFirstName());
				row.createCell(2).setCellValue(user.getLastName());
				row.createCell(3).setCellValue(user.getEmail());
				row.createCell(4).setCellValue(user.getDateOfBirth().toString());
				row.createCell(5).setCellValue(user.getAddress());
			}
			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				workbook.write(out);
				return out.toByteArray();
			}
		}
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<User> getUsersByIsActive(boolean isActive) {
		return userRepository.findByIsActive(isActive);
	}
}
