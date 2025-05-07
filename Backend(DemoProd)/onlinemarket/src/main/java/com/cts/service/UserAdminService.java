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

    public User createUser(UserAdminDTO userAdminDTO, MultipartFile imageFile) throws IOException {
        User user = UserAdminMapper.toEntity(userAdminDTO);
        //added if check
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setPhoto(imageFile.getBytes());
        }
        if(userAdminDTO.isAdmin()==true) {
        	user.setPassword(util.hashPassword(user.getPassword()));
        }
        else {
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

    public ReviewsAndRatings updateReviewByUserEmail(String email, Long ratingId, Double rating, String review, Boolean reviewActiveStatus) {
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

    public List<User> getAllUsers()
    {
        return userRepository.findAll();
    }

    public List<User> getUsersByIsActive(boolean isActive)
    {
        return userRepository.findByIsActive(isActive);
    }
}
