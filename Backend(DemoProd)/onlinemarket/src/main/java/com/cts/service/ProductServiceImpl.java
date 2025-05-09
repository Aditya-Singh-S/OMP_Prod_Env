package com.cts.service;

import com.cts.dto.ProductUploadDTO;
import com.cts.dto.ProductViewDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.entity.ProductSubscription;
import com.cts.entity.Products;
import com.cts.entity.User;
import com.cts.repository.ProductRepository;
import com.cts.repository.ProductViewRepository;
import com.cts.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import com.cts.exception.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.time.Duration;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductViewRepository productViewRepo;
    @Autowired
    UserRepository userRepository;
    @Value("${aws.s3.bucketName}")
    private String bucketName;
    @Value("${aws.s3.keyPrefix}")
    private String s3KeyPrefix;
 
//   
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public ProductServiceImpl(@Value("${aws.accessKeyId}") String accessKey,@Value("${aws.secretKeyId}") String secretKey) {
    	
 
    	
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
    
   
    public List<ProductViewDTO> viewAllProducts() {
        List<Products> allProducts = productRepository.findAll(); // Fetch all Products entities

        if (allProducts == null || allProducts.isEmpty()) {
            throw new InvalidProductException("No products available to display.");
        } else {
            return allProducts.stream()
                    .filter(Products::getIsActive) 
                    .map(product -> {
                        ProductViewDTO dto = new ProductViewDTO();
                        dto.setProductid(product.getProductid());
                        dto.setName(product.getName());
                        dto.setDescription(product.getDescription());
                        dto.setImageUrl(product.getImageUrl());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ProductViewDTO viewProductById(int id) {
        Optional<ProductViewDTO> productOptional = productViewRepo.findById(id);
        return productOptional.orElseThrow(() -> new InvalidProductException("Product not found with ID: " + id));
    }

    @Override
    public Products addProduct(String name, String description, MultipartFile imageFile, Boolean isActive) throws IOException {
        String s3Key = uploadFileToS3(imageFile);
        String imageUrl = String.format("https://%s.s3.us-east-1.amazonaws.com/%s%s", bucketName, s3KeyPrefix, s3Key);
        Products product = new Products();
        product.setName(name);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setIsActive(isActive);
        return productRepository.save(product);
    }

    @Override
    public Products updateProduct(String name, String upName, String description, MultipartFile imageFile, Boolean isActive)
            throws InvalidInputException, IOException {
        Products product = productRepository.findByName(name)
                .orElseThrow(() -> new InvalidInputException("Product not found with Name: " + name));
        if (upName != null)
            product.setName(upName);
        if (description != null)
            product.setDescription(description);
        if (imageFile != null && imageFile != null) {
            String s3Key = uploadFileToS3(imageFile);
            String imageUrl = String.format("https://%s.s3.us-east-1.amazonaws.com/%s%s", bucketName, s3KeyPrefix, s3Key);
            product.setImageUrl(imageUrl);
        }
        if (isActive != null)
            product.setIsActive(isActive);
        return productRepository.save(product);
    }

    private String uploadFileToS3(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String key = s3KeyPrefix + originalFilename; // Use originalFilename as the key.
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();
        try {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return originalFilename; // Return the original filename.
        } catch (S3Exception e) {
            throw new IOException("Could not upload image to S3: " + e.getMessage());
        }
    }

    @Override
    public byte[] getProductImage(int id) {
        Products product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        String s3Key = product.getImageUrl().substring(product.getImageUrl().lastIndexOf('/') + 1); // Extract the key
        return downloadImageFromS3(s3Key);
    }

    @Override
    public byte[] getProductImageByName(String name) {
        Products product = productRepository.findByName(name).orElseThrow(() -> new RuntimeException("Product not found"));
        String s3Key = product.getImageUrl().substring(product.getImageUrl().lastIndexOf('/') + 1);
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


    @Override
    public Products addSubscription(Integer userId, Integer productId) {
        Products product = productRepository.findById(productId).orElseThrow(() -> new InvalidProductException("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!product.getIsActive()) {
            throw new InvalidProductException("Product is not active");
        }
        Optional<ProductSubscription> existingSubscription = product.getProductSubscriptionList().stream()
                .filter(subscription -> subscription.getUser().getUserID() == userId)
                .findFirst();
        if (existingSubscription.isPresent()) {
            ProductSubscription subscription = existingSubscription.get();
            if (!subscription.isOptIn()) {
                subscription.setOptIn(true);
                subscription.setUpdatedOn(LocalDateTime.now());
                productRepository.save(product);
                return product;
            } else {
                throw new InvalidSubscriptionException("Already subscribed to this product");
            }
        }
        ProductSubscription subscription = new ProductSubscription();
        subscription.setUser(user);
        subscription.setProducts(product);
        product.getProductSubscriptionList().add(subscription);
        return productRepository.save(product);
    }

    @Override
    public Products removeSubscription(Integer userId, Integer productId) {
        Products product = productRepository.findById(productId).orElseThrow(() -> new InvalidProductException("Product not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!product.getIsActive()) {
            throw new InvalidProductException("Product is not active");
        }
        Optional<ProductSubscription> existingSubscription = product.getProductSubscriptionList().stream()
                .filter(subscription -> subscription.getUser().getUserID() == userId)
                .findFirst();
        if (!existingSubscription.isPresent() || !existingSubscription.get().isOptIn()) {
            throw new InvalidSubscriptionException("User has not subscribed to this product");
        }
        existingSubscription.get().setOptIn(false);
        existingSubscription.get().setUpdatedOn(LocalDateTime.now());
        productRepository.save(product);
        return product;
    }

    @Override
    public List<ProductSubscription> getSubscriptionList(Integer productId) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidProductException("Product not found"));
        return product.getProductSubscriptionList().stream()
                .filter(ProductSubscription::isOptIn)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductViewDTO> getProductSubscriptionList(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return productViewRepo.getSubscribedListByUser(userId);
    }

    @Override
    public List<ProductViewDTO> findTopSubscribedProduct() {
        return productViewRepo.findTopSubscribedProduct();
    }

    @Override
    public List<ProductViewDTO> findTopRatedProducts() {
        return productViewRepo.findTopRatedProducts();
    }

    @Override
    public List<User> getUsersSubscribedToProduct(int productId) {
        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidProductException("Product not found"));
        return product.getProductSubscriptionList().stream()
                .filter(ProductSubscription::isOptIn)
                .map(ProductSubscription::getUser)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<ProductViewDTO> readProductsFromXlsx(MultipartFile file) throws IOException {
//        List<ProductViewDTO> productDTOs = new ArrayList<>();
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0)
//                    continue;
//                ProductViewDTO productDTO = new ProductViewDTO();
//                productDTO.setName(row.getCell(0).getStringCellValue());
//                productDTO.setDescription(row.getCell(1).getStringCellValue());
//                // Assuming the local image path is in the third column (index 2)
//                String localImagePath = row.getCell(2).getStringCellValue();
//                productDTO.setLocalImagePath(localImagePath);
//                productDTOs.add(productDTO);
//            }
//        }
//        return productDTOs;
//    }

//    @Override
//    public List<ProductUploadDTO> readProductsFromXlsx(MultipartFile file) throws IOException {
//        List<ProductUploadDTO> uploadDTOs = new ArrayList<>();
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0)
//                    continue;
//                ProductUploadDTO uploadDTO = new ProductUploadDTO();
//                uploadDTO.setName(row.getCell(0).getStringCellValue());
//                uploadDTO.setDescription(row.getCell(1).getStringCellValue());
//                try {
//                    uploadDTO.setLocalImagePath(row.getCell(2).getStringCellValue());
//                } catch (Exception e) {
//                    System.err.println("Error reading local image path from Excel: " + e.getMessage());
//                    uploadDTO.setLocalImagePath(null);
//                }
//                uploadDTOs.add(uploadDTO);
//            }
//        }
//        return uploadDTOs; // Directly return List<ProductUploadDTO>
//    }

//    @Override
//    public List<Products> saveProducts(List<ProductUploadDTO> uploadDTOs) { // Expect List<ProductUploadDTO>
//        List<Products> allProduct = new ArrayList<>();
//        for (ProductUploadDTO uploadDTO : uploadDTOs) {
//            Products product = new Products();
//            product.setName(uploadDTO.getName());
//            product.setDescription(uploadDTO.getDescription());
//
//            String localImagePath = uploadDTO.getLocalImagePath();
//            String s3ImageUrl = null;
//
//            if (localImagePath != null && !localImagePath.trim().isEmpty()) {
//                Path localFile = Paths.get(localImagePath);
//                if (Files.exists(localFile)) {
//                    try {
//                        String fileName = localFile.getFileName().toString();
//                        String s3Key = uploadFileToS3(localFile, fileName);
//                        s3ImageUrl = String.format("https://%s.s3.amazonaws.com/%s%s", bucketName, s3KeyPrefix, fileName);
//                    } catch (IOException e) {
//                        System.err.println("Error uploading image from " + localImagePath + " to S3: " + e.getMessage());
//                        // Handle the error appropriately
//                    }
//                } else {
//                    System.err.println("Local image file not found at: " + localImagePath);
//                    // Handle the case where the local file doesn't exist
//                }
//            }
//            product.setImageUrl(s3ImageUrl); // Set the S3 URL (or null if upload failed)
//            allProduct.add(product);
//        }
//        productRepository.saveAll(allProduct);
//        return allProduct;
//    }
}
