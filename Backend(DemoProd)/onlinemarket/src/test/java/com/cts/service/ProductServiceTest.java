//
//package com.cts.service;
// 
//import com.cts.dto.ProductViewDTO;
//import com.cts.entity.ProductSubscription;
//import com.cts.entity.Products;
//import com.cts.entity.User;
//import com.cts.exception.InvalidInputException;
//import com.cts.exception.InvalidProductException;
//import com.cts.exception.InvalidSubscriptionException;
//import com.cts.exception.UserNotFoundException;
//import com.cts.repository.ProductRepository;
//import com.cts.repository.ProductViewRepository;
//import com.cts.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import software.amazon.awssdk.core.ResponseBytes;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.GetObjectResponse;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.S3Exception;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
// 
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
// 
//@ExtendWith(MockitoExtension.class)
//public class ProductServiceTest {
// 
//    @Mock
//    private ProductRepository productRepository;
// 
//    @Mock
//    private UserRepository userRepository;
// 
//    @Mock
//    private ProductViewRepository productViewRepo; // Assuming this is your DTO repository
// 
//    @Mock
//    private SNSService snsService;
// 
//    // Inject the mocks into the ProductService instance
//    @InjectMocks
//    private ProductServiceImpl productService;
// 
//    @Mock // Mock the Products object
//    private Products testProduct;
// 
//    private User testUser;
// 
//    @Mock // Mock the ProductSubscription object
//    private ProductSubscription testProductSubscription;
//    
//    private String bucketName = "test-bucket";
//    private String s3KeyPrefix = "product-images/";
//    
//    private final S3Client s3Client = null;
// 
//    @BeforeEach
//    void setUp() {
//        // Configure the mocked testProduct
//        when(testProduct.getProductid()).thenReturn(1);
//        when(testProduct.getName()).thenReturn("Test Product");
//        when(testProduct.getIsActive()).thenReturn(true);
//        // Crucial: Return a real, mutable list if your service modifies it
//        when(testProduct.getProductSubscriptionList()).thenReturn(new ArrayList<>());
// 
// 
//        testUser = new User();
//        testUser.setUserID(101);
//        testUser.setEmail("test@example.com");
//        testUser.setNickName("TestUser");
// 
//        // Configure the mocked testProductSubscription
//        when(testProductSubscription.getSubscriptionId()).thenReturn(1);
//        when(testProductSubscription.getUser()).thenReturn(testUser);
//        when(testProductSubscription.getProducts()).thenReturn(testProduct);
//        when(testProductSubscription.isOptIn()).thenReturn(true);
//        when(testProductSubscription.getAddedOn()).thenReturn(LocalDateTime.now());
//        when(testProductSubscription.getUpdatedOn()).thenReturn(LocalDateTime.now());
// 
//        // Adapting AWS S3/Credentials mocking logic for SNSService
//        // Since SNSService is mocked, we don't need actual AWS credentials for it.
//        // If SNSService had an internal S3Client, we'd mock that too within SNSService's setup.
//        // For now, we'll simply mock the snsService itself.
//        
//     // Set private fields using ReflectionTestUtils for S3 integration
//        ReflectionTestUtils.setField(productService, "bucketName", bucketName);
//        ReflectionTestUtils.setField(productService, "s3KeyPrefix", s3KeyPrefix);
//    }
// 
//    
//    @Test
//    void testViewAllProducts_Success() {
//    	Products product1 = new Products(); // Use the no-argument constructor
//    	product1.setProductid(1);
//    	product1.setName("Product A");
//    	product1.setDescription("Desc A");
//    	product1.setImageUrl("urlA");
//    	product1.setIsActive(true);
//    	product1.setProductSubscriptionList(new ArrayList<>()); // Set the list explicitly
//
//    	Products product2 = new Products();
//    	product2.setProductid(2);
//    	product2.setName("Product B");
//    	product2.setDescription("Desc B");
//    	product2.setImageUrl("urlB");
//    	product2.setIsActive(true);
//    	product2.setProductSubscriptionList(new ArrayList<>());
//
//    	Products product3 = new Products();
//    	product3.setProductid(3);
//    	product3.setName("Inactive Product");
//    	product3.setDescription("Desc C");
//    	product3.setImageUrl("urlC");
//    	product3.setIsActive(false);
//    	product3.setProductSubscriptionList(new ArrayList<>());
//
//        List<Products> allProducts = Arrays.asList(product1, product2, product3);
//
//        ProductViewDTO dto1 = new ProductViewDTO(1, "Product A", "Desc A", "urlA", true, 4.5, 5);
//        ProductViewDTO dto2 = new ProductViewDTO(2, "Product B", "Desc B", "urlB", true, 3.0, 10);
//        ProductViewDTO dto3_view = new ProductViewDTO(3, "Inactive Product", "Desc C", "urlC", false, 2.0, 2); // For getReferenceById
//
//        when(productRepository.findAll()).thenReturn(allProducts);
//        // Mock getSubscriptionList for each active product
//        when(testProduct.getProductid()).thenReturn(1); // Set mock behavior for testProduct used in getSubscriptionList
//        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
//        when(productRepository.findById(2)).thenReturn(Optional.of(product2));
//
//        // Mock productViewRepo.getReferenceById for each product that will be mapped
//        when(productViewRepo.getReferenceById(1)).thenReturn(dto1);
//        when(productViewRepo.getReferenceById(2)).thenReturn(dto2);
//
//        List<ProductSubscription> subs1 = new ArrayList<>();
//        subs1.add(new ProductSubscription()); // Simulate 1 subscription
//        when(product1.getProductSubscriptionList()).thenReturn(subs1);
//
//        List<ProductSubscription> subs2 = new ArrayList<>();
//        subs2.add(new ProductSubscription()); // Simulate 2 subscriptions
//        subs2.add(new ProductSubscription());
//        when(product2.getProductSubscriptionList()).thenReturn(subs2);
//
//        List<ProductViewDTO> result = productService.viewAllProducts();
//
//        assertNotNull(result);
//        assertEquals(2, result.size()); // Only active products
//        assertEquals("Product A", result.get(0).getName());
//        assertEquals(1, result.get(0).getSubscription_count()); // Based on mocked subs1 size
//        assertEquals("Product B", result.get(1).getName());
//        assertEquals(2, result.get(1).getSubscription_count()); // Based on mocked subs2 size
//        verify(productRepository, times(1)).findAll();
//        verify(productViewRepo, times(1)).getReferenceById(1);
//        verify(productViewRepo, times(1)).getReferenceById(2);
//    }
//
//    @Test
//    void testViewAllProducts_NoProductsAvailable() {
//        when(productRepository.findAll()).thenReturn(new ArrayList<>()); // Empty list
//
//        assertThrows(InvalidProductException.class, () -> productService.viewAllProducts());
//        verify(productRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testViewProductById_Success() {
//        ProductViewDTO mockDto = new ProductViewDTO(1, "Test Product", "Description", "image.jpg", true, 4.5, 10);
//        when(productViewRepo.findById(1)).thenReturn(Optional.of(mockDto));
//
//        ProductViewDTO result = productService.viewProductById(1);
//
//        assertNotNull(result);
//        assertEquals(mockDto.getProductid(), result.getProductid());
//        verify(productViewRepo, times(1)).findById(1);
//    }
//
//    @Test
//    void testViewProductById_NotFound() {
//        when(productViewRepo.findById(99)).thenReturn(Optional.empty());
//
//        assertThrows(InvalidProductException.class, () -> productService.viewProductById(99));
//        verify(productViewRepo, times(1)).findById(99);
//    }
//
//    @Test
//    void testAddProduct_Success() throws IOException {
//        String name = "New Product";
//        String description = "New Description";
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "image", "test.jpg", "image/jpeg", "some image content".getBytes());
//        Boolean isActive = true;
//
//        Products newProduct = new Products();
//        newProduct.setName(name);
//        newProduct.setDescription(description);
//        newProduct.setIsActive(isActive);
//
//        when(productRepository.save(any(Products.class))).thenReturn(newProduct);
//        doNothing().when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        doNothing().when(snsService).notifyOnAddProduct();
//
//        Products result = productService.addProduct(name, description, imageFile, isActive);
//
//        assertNotNull(result);
//        assertEquals(name, result.getName());
//        assertEquals(description, result.getDescription());
//        assertTrue(result.getIsActive());
//        assertTrue(result.getImageUrl().contains("https://test-bucket.s3.us-east-1.amazonaws.com/product-images/New_Product.jpg"));
//        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(productRepository, times(1)).save(any(Products.class));
//        verify(snsService, times(1)).notifyOnAddProduct();
//    }
//
//    @Test
//    void testAddProduct_InvalidName_Null() {
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
//        assertThrows(InvalidInputException.class, () -> productService.addProduct(null, "desc", imageFile, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidName_Empty() {
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
//        assertThrows(InvalidInputException.class, () -> productService.addProduct("", "desc", imageFile, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidName_TooLong() {
//        String longName = "a".repeat(256);
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
//        assertThrows(InvalidInputException.class, () -> productService.addProduct(longName, "desc", imageFile, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidIsActive_Null() {
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "content".getBytes());
//        assertThrows(InvalidInputException.class, () -> productService.addProduct("Name", "desc", imageFile, null));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidImageFile_Null() {
//        assertThrows(InvalidInputException.class, () -> productService.addProduct("Name", "desc", null, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidImageFile_Empty() {
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[0]);
//        assertThrows(InvalidInputException.class, () -> productService.addProduct("Name", "desc", imageFile, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testAddProduct_InvalidImageFile_WrongContentType() {
//        MockMultipartFile imageFile = new MockMultipartFile("image", "test.txt", "text/plain", "content".getBytes());
//        assertThrows(InvalidInputException.class, () -> productService.addProduct("Name", "desc", imageFile, true));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//    }
//
//    @Test
//    void testUpdateProduct_Success_AllFields() throws IOException {
//        String oldName = "Old Product";
//        String updatedName = "Updated Product";
//        String updatedDescription = "Updated Description";
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "image", "updated.png", "image/png", "updated image content".getBytes());
//        Boolean updatedIsActive = false;
//
//     // Corrected way to create existingProduct
//        Products existingProduct = new Products();
//        existingProduct.setProductid(1);
//        existingProduct.setName(oldName);
//        existingProduct.setDescription("Old Description");
//        existingProduct.setImageUrl("old_image.jpg");
//        existingProduct.setIsActive(true);
//        existingProduct.setProductSubscriptionList(new ArrayList<>()); // Initialize the list
//        
//        ProductSubscription userSub = new ProductSubscription();
//        userSub.setUser(testUser); // testUser has role USER
//        userSub.setProducts(existingProduct);
//        userSub.setOptIn(true);
//        existingProduct.getProductSubscriptionList().add(userSub);
//
//        when(productRepository.findByName(oldName)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(any(Products.class))).thenReturn(existingProduct);
//        doNothing().when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        doNothing().when(snsService).notifyAdminOnUpdateProduct();
//        doNothing().when(snsService).notifyUserOnUpdateProduct(anyList());
//
//        Products result = productService.updateProduct(oldName, updatedName, updatedDescription, imageFile, updatedIsActive);
//
//        assertNotNull(result);
//        assertEquals(updatedName, result.getName());
//        assertEquals(updatedDescription, result.getDescription());
//        assertEquals(updatedIsActive, result.getIsActive());
//        assertTrue(result.getImageUrl().contains("https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Updated_Product.png"));
//        verify(productRepository, times(1)).findByName(oldName);
//        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(productRepository, times(1)).save(existingProduct);
//        verify(snsService, times(1)).notifyAdminOnUpdateProduct();
//        verify(snsService, times(1)).notifyUserOnUpdateProduct(Mockito.argThat(list -> list.contains(testUser.getEmail())));
//    }
//
//    @Test
//    void testUpdateProduct_Success_PartialUpdate_NameOnly() throws IOException {
//        String oldName = "Old Product";
//        String updatedName = "Updated Product Name Only";
//
//     // Corrected way to create existingProduct
//        Products existingProduct = new Products();
//        existingProduct.setProductid(1);
//        existingProduct.setName(oldName);
//        existingProduct.setDescription("Old Description");
//        existingProduct.setImageUrl("old_image.jpg");
//        existingProduct.setIsActive(true);
//        existingProduct.setProductSubscriptionList(new ArrayList<>()); // Initialize the list
//
//        when(productRepository.findByName(oldName)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(any(Products.class))).thenReturn(existingProduct);
//        // No S3Client interaction expected as imageFile is null
//        doNothing().when(snsService).notifyAdminOnUpdateProduct();
//        doNothing().when(snsService).notifyUserOnUpdateProduct(anyList());
//
//        Products result = productService.updateProduct(oldName, updatedName, null, null, null);
//
//        assertNotNull(result);
//        assertEquals(updatedName, result.getName());
//        assertEquals("Old Description", result.getDescription()); // Should remain unchanged
//        assertTrue(result.getIsActive()); // Should remain unchanged
//        assertEquals("old_image.jpg", result.getImageUrl()); // Should remain unchanged
//        verify(productRepository, times(1)).findByName(oldName);
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(productRepository, times(1)).save(existingProduct);
//        verify(snsService, times(1)).notifyAdminOnUpdateProduct();
//    }
//
//    @Test
//    void testUpdateProduct_Success_PartialUpdate_ImageOnly() throws IOException {
//        String oldName = "Old Product";
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "image", "new_image.jpeg", "image/jpeg", "new image content".getBytes());
//
//     // Corrected way to create existingProduct
//        Products existingProduct = new Products();
//        existingProduct.setProductid(1);
//        existingProduct.setName(oldName);
//        existingProduct.setDescription("Old Description");
//        existingProduct.setImageUrl("old_image.jpg");
//        existingProduct.setIsActive(true);
//        existingProduct.setProductSubscriptionList(new ArrayList<>()); // Initialize the list
//
//        when(productRepository.findByName(oldName)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(any(Products.class))).thenReturn(existingProduct);
//        doNothing().when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        doNothing().when(snsService).notifyAdminOnUpdateProduct();
//        doNothing().when(snsService).notifyUserOnUpdateProduct(anyList());
//
//        Products result = productService.updateProduct(oldName, null, null, imageFile, null);
//
//        assertNotNull(result);
//        assertEquals(oldName, result.getName()); // Should remain unchanged
//        assertTrue(result.getImageUrl().contains("https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Old_Product.jpeg"));
//        verify(productRepository, times(1)).findByName(oldName);
//        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(productRepository, times(1)).save(existingProduct);
//        verify(snsService, times(1)).notifyAdminOnUpdateProduct();
//    }
//
//    @Test
//    void testUpdateProduct_ProductNotFound() {
//        String name = "NonExistent Product";
//        when(productRepository.findByName(name)).thenReturn(Optional.empty());
//
//        assertThrows(InvalidInputException.class, () -> productService.updateProduct(name, "New Name", null, null, null));
//        verify(productRepository, never()).save(any(Products.class));
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        verify(snsService, never()).notifyAdminOnUpdateProduct();
//        verify(snsService, never()).notifyUserOnUpdateProduct(anyList());
//    }
//
//    @Test
//    void testUpdateProduct_NoChanges() throws IOException {
//        String name = "Existing Product";
//        
//     // Corrected way to create existingProduct
//        Products existingProduct = new Products();
//        existingProduct.setProductid(1);
//        existingProduct.setName(oldName);
//        existingProduct.setDescription("Old Description");
//        existingProduct.setImageUrl("old_image.jpg");
//        existingProduct.setIsActive(true);
//        existingProduct.setProductSubscriptionList(new ArrayList<>()); // Initialize the list
//
//        when(productRepository.findByName(name)).thenReturn(Optional.of(existingProduct));
//        when(productRepository.save(any(Products.class))).thenReturn(existingProduct);
//        doNothing().when(snsService).notifyAdminOnUpdateProduct();
//        doNothing().when(snsService).notifyUserOnUpdateProduct(anyList());
//
//        Products result = productService.updateProduct(name, null, null, null, null); // No changes passed
//
//        assertNotNull(result);
//        assertEquals(name, result.getName());
//        assertEquals("Description", result.getDescription());
//        assertEquals("image.jpg", result.getImageUrl());
//        assertTrue(result.getIsActive());
//
//        verify(productRepository, times(1)).findByName(name);
//        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
//        // Save should still be called if no specific fields are updated in the parameters,
//        // as the service calls save regardless to persist potential internal changes
//        verify(productRepository, times(1)).save(existingProduct);
//        verify(snsService, times(1)).notifyAdminOnUpdateProduct();
//        // notifyUserOnUpdateProduct may not be called if no changes trigger it, or it's empty
//        verify(snsService, times(1)).notifyUserOnUpdateProduct(anyList()); // Called with an empty list
//    }
//
//
//    @Test
//    void testGetProductImage_Success() throws IOException {
//        String imageUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Test_Product.jpg";
//        byte[] imageData = "mockImageData".getBytes();
//
//        Products product = new Products();
//        product.setProductid(1);
//        product.setImageUrl(imageUrl);
//
//        // Mock the response for getObjectAsBytes
//        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
//        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
//        when(responseBytes.asByteArray()).thenReturn(imageData);
//        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);
//
//        when(productRepository.findById(1)).thenReturn(Optional.of(product));
//
//        byte[] result = productService.getProductImage(1);
//
//        assertNotNull(result);
//        assertArrayEquals(imageData, result);
//        verify(productRepository, times(1)).findById(1);
//        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//
//    @Test
//    void testGetProductImage_ProductNotFound() {
//        when(productRepository.findById(99)).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () -> productService.getProductImage(99), "Product not found");
//        verify(productRepository, times(1)).findById(99);
//        verify(s3Client, never()).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//
//    @Test
//    void testGetProductImage_S3Exception() throws IOException {
//        String imageUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Test_Product.jpg";
//        Products product = new Products();
//        product.setProductid(1);
//        product.setImageUrl(imageUrl);
//
//        when(productRepository.findById(1)).thenReturn(Optional.of(product));
//        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(S3Exception.builder().message("S3 error").build());
//
//        byte[] result = productService.getProductImage(1);
//
//        assertNotNull(result);
//        assertEquals(0, result.length); // Should return empty array on S3 error
//        verify(productRepository, times(1)).findById(1);
//        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//
//
//    @Test
//    void testGetProductImageByName_Success() throws IOException {
//        String productName = "Test Product";
//        String imageUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Test_Product.jpg";
//        byte[] imageData = "mockImageDataByName".getBytes();
//
//        Products product = new Products();
//        product.setName(productName);
//        product.setImageUrl(imageUrl);
//
//        // Mock the response for getObjectAsBytes
//        GetObjectResponse getObjectResponse = GetObjectResponse.builder().build();
//        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
//        when(responseBytes.asByteArray()).thenReturn(imageData);
//        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);
//
//        when(productRepository.findByName(productName)).thenReturn(Optional.of(product));
//
//        byte[] result = productService.getProductImageByName(productName);
//
//        assertNotNull(result);
//        assertArrayEquals(imageData, result);
//        verify(productRepository, times(1)).findByName(productName);
//        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//
//    @Test
//    void testGetProductImageByName_ProductNotFound() {
//        String productName = "NonExistent Product";
//        when(productRepository.findByName(productName)).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () -> productService.getProductImageByName(productName), "Product not found");
//        verify(productRepository, times(1)).findByName(productName);
//        verify(s3Client, never()).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//
//    @Test
//    void testGetProductImageByName_S3Exception() throws IOException {
//        String productName = "Test Product";
//        String imageUrl = "https://test-bucket.s3.us-east-1.amazonaws.com/product-images/Test_Product.jpg";
//        Products product = new Products();
//        product.setName(productName);
//        product.setImageUrl(imageUrl);
//
//        when(productRepository.findByName(productName)).thenReturn(Optional.of(product));
//        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(S3Exception.builder().message("S3 error").build());
//
//        byte[] result = productService.getProductImageByName(productName);
//
//        assertNotNull(result);
//        assertEquals(0, result.length); // Should return empty array on S3 error
//        verify(productRepository, times(1)).findByName(productName);
//        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
//    }
//    
//    
//    @Test
//    void testAddSubscription_Success_NewSubscription() {
//        // Create a local, real list for the mocked product to hold subscriptions
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions);
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
//        when(productRepository.save(any(Products.class))).thenReturn(testProduct);
// 
//        Products result = productService.addSubscription(testUser.getUserID(), testProduct.getProductid());
// 
//        assertNotNull(result);
//        assertTrue(result.getProductSubscriptionList().stream()
//                .anyMatch(s -> s.getUser().getUserID() == testUser.getUserID() && s.isOptIn()));
//        verify(snsService, times(1)).notifyOnSubscribing(testUser.getEmail(), testUser.getNickName(), testProduct.getName());
//        verify(productRepository, times(1)).save(testProduct);
//    }
// 
//    @Test
//    void testAddSubscription_Success_ReOptIn() {
//        // Simulate existing subscription that was opt-out in a real list
//        ProductSubscription existingSub = new ProductSubscription();
//        existingSub.setUser(testUser);
//        existingSub.setProducts(testProduct);
//        existingSub.setOptIn(false);
//        
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        productSubscriptions.add(existingSub);
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions);
// 
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
//        when(productRepository.save(any(Products.class))).thenReturn(testProduct);
// 
//        Products result = productService.addSubscription(testUser.getUserID(), testProduct.getProductid());
// 
//        assertNotNull(result);
//        assertTrue(result.getProductSubscriptionList().stream()
//                .filter(s -> s.getUser().getUserID() == testUser.getUserID())
//                .findFirst().get().isOptIn());
//        verify(snsService, times(1)).notifyOnSubscribing(testUser.getEmail(), testUser.getNickName(), testProduct.getName());
//        verify(productRepository, times(1)).save(testProduct);
//    }
// 
//    @Test
//    void testAddSubscription_ProductNotFound() {
//        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(InvalidProductException.class, () -> productService.addSubscription(testUser.getUserID(), 99));
//        verify(snsService, never()).notifyOnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testAddSubscription_UserNotFound() {
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(UserNotFoundException.class, () -> productService.addSubscription(999, testProduct.getProductid()));
//        verify(snsService, never()).notifyOnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testAddSubscription_ProductNotActive() {
//        when(testProduct.getIsActive()).thenReturn(false); // Set product to inactive on the mock
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
// 
//        assertThrows(InvalidProductException.class, () -> productService.addSubscription(testUser.getUserID(), testProduct.getProductid()));
//        verify(snsService, never()).notifyOnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testAddSubscription_AlreadySubscribed() {
//        // Simulate an existing and active subscription in a real list
//        ProductSubscription existingSub = new ProductSubscription();
//        existingSub.setUser(testUser);
//        existingSub.setProducts(testProduct);
//        existingSub.setOptIn(true);
// 
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        productSubscriptions.add(existingSub);
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions);
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
// 
//        assertThrows(InvalidSubscriptionException.class, () -> productService.addSubscription(testUser.getUserID(), testProduct.getProductid()));
//        verify(snsService, never()).notifyOnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class)); // save should not be called
//    }
// 
//    @Test
//    void testRemoveSubscription_Success() {
//        // Add an active subscription to the product's real list for the mock
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        productSubscriptions.add(testProductSubscription);
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions);
// 
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
//        when(productRepository.save(any(Products.class))).thenReturn(testProduct);
// 
//        Products result = productService.removeSubscription(testUser.getUserID(), testProduct.getProductid());
// 
//        assertNotNull(result);
//        assertFalse(result.getProductSubscriptionList().stream()
//                .filter(s -> s.getUser().getUserID() == testUser.getUserID())
//                .findFirst().get().isOptIn());
//        verify(snsService, times(1)).notifyOnUnSubscribing(testUser.getEmail(), testUser.getNickName(), testProduct.getName());
//        verify(productRepository, times(1)).save(testProduct);
//    }
// 
//    @Test
//    void testRemoveSubscription_ProductNotFound() {
//        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(InvalidProductException.class, () -> productService.removeSubscription(testUser.getUserID(), 99));
//        verify(snsService, never()).notifyOnUnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testRemoveSubscription_UserNotFound() {
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(UserNotFoundException.class, () -> productService.removeSubscription(999, testProduct.getProductid()));
//        verify(snsService, never()).notifyOnUnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testRemoveSubscription_ProductNotActive() {
//        when(testProduct.getIsActive()).thenReturn(false); // Set product to inactive on the mock
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
// 
//        assertThrows(InvalidProductException.class, () -> productService.removeSubscription(testUser.getUserID(), testProduct.getProductid()));
//        verify(snsService, never()).notifyOnUnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testRemoveSubscription_UserNotSubscribed() {
//        // Product has no subscription for this user (empty list)
//        when(testProduct.getProductSubscriptionList()).thenReturn(new ArrayList<>()); // Ensure it's empty
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
// 
//        assertThrows(InvalidSubscriptionException.class, () -> productService.removeSubscription(testUser.getUserID(), testProduct.getProductid()));
//        verify(snsService, never()).notifyOnUnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testRemoveSubscription_UserOptedOut() {
//        // Simulate existing subscription that was already opt-out in a real list
//        ProductSubscription existingSub = new ProductSubscription();
//        existingSub.setUser(testUser);
//        existingSub.setProducts(testProduct);
//        existingSub.setOptIn(false);
// 
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        productSubscriptions.add(existingSub);
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions);
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
// 
//        assertThrows(InvalidSubscriptionException.class, () -> productService.removeSubscription(testUser.getUserID(), testProduct.getProductid()));
//        verify(snsService, never()).notifyOnUnSubscribing(anyString(), anyString(), anyString());
//        verify(productRepository, never()).save(any(Products.class));
//    }
// 
//    @Test
//    void testGetSubscriptionList_Success() {
//        // Add an active subscription to the product's real list for the mock
//        List<ProductSubscription> productSubscriptions = new ArrayList<>();
//        productSubscriptions.add(testProductSubscription);
// 
//        // Add an inactive subscription to ensure filtering works
//        ProductSubscription inactiveSub = new ProductSubscription();
//        User inactiveUser = new User();
//        inactiveUser.setUserID(102);
//        inactiveSub.setUser(inactiveUser);
//        inactiveSub.setProducts(testProduct);
//        inactiveSub.setOptIn(false);
//        productSubscriptions.add(inactiveSub);
// 
//        when(testProduct.getProductSubscriptionList()).thenReturn(productSubscriptions); // Return the populated list
// 
//        when(productRepository.findById(anyInt())).thenReturn(Optional.of(testProduct));
// 
//        List<ProductSubscription> result = productService.getSubscriptionList(testProduct.getProductid());
// 
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertTrue(result.get(0).isOptIn()); // Only opt-in subscriptions should be returned
//        assertEquals(testUser.getUserID(), result.get(0).getUser().getUserID());
//        verify(productRepository, times(1)).findById(testProduct.getProductid());
//    }
// 
//    @Test
//    void testGetSubscriptionList_ProductNotFound() {
//        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(InvalidProductException.class, () -> productService.getSubscriptionList(99));
//        verify(productRepository, times(1)).findById(99);
//    }
// 
//    @Test
//    void testGetProductSubscriptionList_Success() {
//        ProductViewDTO dto1 = new ProductViewDTO(1, "Product A", "Description A","image_url", true, 2.9, 10);
//        ProductViewDTO dto2 = new ProductViewDTO(2, "Product B", "Description B", "image_url",true, 3.6, 5);
//        ProductViewDTO dto3 = new ProductViewDTO(3, "Inactive Product", "Description C", "image_url",false, 4.6, 7);
// 
//        List<ProductViewDTO> mockProductViewList = Arrays.asList(dto1, dto2, dto3);
// 
//        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
//        when(productViewRepo.getSubscribedListByUser(anyInt())).thenReturn(mockProductViewList);
// 
//        List<ProductViewDTO> result = productService.getProductSubscriptionList(testUser.getUserID());
// 
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(2, result.size()); // Expecting 2 active products
//        assertTrue(result.stream().noneMatch(p -> !p.isIsactive())); // Ensure only active products are returned
//        verify(userRepository, times(1)).findById(testUser.getUserID());
//        verify(productViewRepo, times(1)).getSubscribedListByUser(testUser.getUserID());
//    }
// 
//    @Test
//    void testGetProductSubscriptionList_UserNotFound() {
//        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
// 
//        assertThrows(UserNotFoundException.class, () -> productService.getProductSubscriptionList(999));
//        verify(productViewRepo, never()).getSubscribedListByUser(anyInt());
//    }
// 
//   
//}
// 