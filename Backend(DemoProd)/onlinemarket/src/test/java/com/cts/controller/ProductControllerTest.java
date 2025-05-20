//package com.cts.controller;
//
//import com.cts.dto.ProductViewDTO;
//import com.cts.entity.ProductSubscription;
//import com.cts.entity.Products;
//import com.cts.exception.InvalidProductException;
//import com.cts.exception.InvalidSubscriptionException;
//import com.cts.service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ProductControllerTest {
//
//    @Mock
//    private ProductService productService;
//
//    @Spy // Use @Spy for the controller so we can mock specific methods while keeping others real
//    @InjectMocks
//    private ProductController productController;
//
//    private final String AUTH_HEADER_BASIC;
//
//    @Mock // Mock the Products object for consistent test data
//    private Products testProduct;
//
//    @Mock // Mock the ProductSubscription object for consistent test data
//    private ProductSubscription testProductSubscription;
//
//    private ProductViewDTO testProductViewDTO; // This is a concrete DTO, not a mock
//
//    @Mock
//    private MultipartFile imageFile;
//
//    @Mock
//    private MultipartFile file;
//
//    public ProductControllerTest() {
//        // Generate a valid Basic Auth header once for the test class
//        String username = "testuser";
//        String password = "testpassword";
//        String credentials = username + ":" + password;
//        AUTH_HEADER_BASIC = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
//    }
//
//    @BeforeEach
//    void setUp() {
//        // Use lenient() for doNothing() to prevent UnnecessaryStubbingException
//        // This ensures checkAuthorizationHeaders is bypassed without strict checking if it's called
//        Mockito.lenient().doNothing().when(productController).checkAuthorizationHeaders(anyString());
//
//        // Configure the mocked testProduct with lenient() for common properties
//        // These are mocks, so their getter behavior needs to be explicitly defined.
//        Mockito.lenient().when(testProduct.getProductid()).thenReturn(1);
//        Mockito.lenient().when(testProduct.getName()).thenReturn("Test Product");
//        Mockito.lenient().when(testProduct.getIsActive()).thenReturn(true);
//        // Ensure getProductSubscriptionList returns a mutable list if modified by service
//        Mockito.lenient().when(testProduct.getProductSubscriptionList()).thenReturn(new ArrayList<>());
//
//        // Configure the mocked testProductSubscription with lenient()
//        Mockito.lenient().when(testProductSubscription.getSubscriptionId()).thenReturn(1);
//        Mockito.lenient().when(testProductSubscription.isOptIn()).thenReturn(true);
//        Mockito.lenient().when(testProductSubscription.getAddedOn()).thenReturn(LocalDateTime.now());
//        Mockito.lenient().when(testProductSubscription.getUpdatedOn()).thenReturn(LocalDateTime.now());
//        Mockito.lenient().when(testProductSubscription.getProducts()).thenReturn(testProduct); // Link to the mocked product
//        // Assuming your ProductSubscription has a getUserId() that returns an Integer directly
//        Mockito.lenient().when(testProductSubscription.getUserId()).thenReturn(1);
//
//        // This is a concrete DTO, not a mock. Initialize its fields directly.
//        testProductViewDTO = new ProductViewDTO();
//        testProductViewDTO.setProductid(1);
//        testProductViewDTO.setName("Test Product View");
//        testProductViewDTO.setIsactive(true);
//        testProductViewDTO.setDescription("Sample Description for DTO"); // Add if your DTO has this field
//        testProductViewDTO.setSubscription_count(10); // Add if your DTO has this field
//    }
//
//    @Test
//    void createNewProduct_validRequest_shouldReturnCreatedResponse() throws IOException {
//        String name = "Sample Product";
//        String description = "This is a description";
//        Boolean isActive = true;
//
//        // Mock product entity (Ensure it matches your entity fields)
//        Products mockProduct = new Products();
//        mockProduct.setProductid(1);
//        mockProduct.setName(name);
//        mockProduct.setDescription(description);
//        mockProduct.setImageUrl("image.png"); // Assuming controller extracts file URL
//        mockProduct.setIsActive(isActive);
//        mockProduct.setAddedOn(LocalDateTime.now());
//        mockProduct.setUpdatedOn(LocalDateTime.now());
//        mockProduct.setProductSubscriptionList(null);
//
//        when(productService.addProduct(name, description, imageFile, isActive)).thenReturn(mockProduct);
//
//        ResponseEntity<Products> responseEntity = productController.createNewProduct(AUTH_HEADER_BASIC, name, description, imageFile, isActive);
//
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        assertEquals(mockProduct, responseEntity.getBody());
//    }
//
//
//    @Test
//    void updateProduct_validRequest_shouldReturnUpdatedProduct() throws Exception {
//        String name = "Existing Product";
//        String upName = "Updated Product";
//        String description = "Updated description";
//        Boolean isActive = false;
//
//        // Mock updated product entity
//        Products updatedProduct = new Products();
//        updatedProduct.setProductid(1);
//        updatedProduct.setName(upName);
//        updatedProduct.setDescription(description);
//        updatedProduct.setImageUrl("updated_image.png"); // Mock file handling result
//        updatedProduct.setIsActive(isActive);
//        updatedProduct.setAddedOn(LocalDateTime.now());
//        updatedProduct.setUpdatedOn(LocalDateTime.now());
//        updatedProduct.setProductSubscriptionList(null);
//
//        when(productService.updateProduct(name, upName, description, imageFile, isActive)).thenReturn(updatedProduct);
//
//        ResponseEntity<Products> responseEntity = productController.updateProduct(AUTH_HEADER_BASIC, name, upName, description, imageFile, isActive);
//
//        // Verifications
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertEquals(updatedProduct, responseEntity.getBody());
//    }
//
//    @Test
//    void uploadMultipleProducts_validRequest_shouldReturnCreatedResponse() throws IOException {
//        boolean bulkProductisactive = true;
//
//        // Mock uploaded product list
//        Products product1 = new Products();
//        product1.setName("Product1");
//        product1.setDescription("Description1");
//        product1.setImageUrl("https://image1.png");
//        product1.setIsActive(true);
//
//        Products product2 = new Products();
//        product2.setName("Product2");
//        product2.setDescription("Description2");
//        product2.setImageUrl("https://image2.png");
//        product2.setIsActive(true);
//
//        List<Products> uploadedProducts = List.of(product1, product2);
//
//        when(productService.addMultipleProducts(file, bulkProductisactive)).thenReturn(uploadedProducts);
//
//        ResponseEntity<List<Products>> responseEntity = productController.uploadMultipleProducts(AUTH_HEADER_BASIC, file, bulkProductisactive);
//
//        // Verifications
//        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        assertEquals(uploadedProducts, responseEntity.getBody());
//    }
//
//    @Test
//    void uploadMultipleProducts_emptyFile_shouldReturnBadRequest() {
//        boolean bulkProductisactive = true;
//
//        when(file.isEmpty()).thenReturn(true);
//
//        ResponseEntity<List<Products>> responseEntity = productController.uploadMultipleProducts(AUTH_HEADER_BASIC, file, bulkProductisactive);
//
//        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
//    }
//
//    @Test
//    void uploadMultipleProducts_fileProcessingError_shouldReturnInternalServerError() throws IOException {
//        boolean bulkProductisactive = true;
//
//        when(productService.addMultipleProducts(file, bulkProductisactive)).thenThrow(new IOException("File processing error"));
//
//        ResponseEntity<List<Products>> responseEntity = productController.uploadMultipleProducts(AUTH_HEADER_BASIC, file, bulkProductisactive);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//    }
//
//
//    @Test
//    void testAddSubscription_Success() {
//        when(productService.addSubscription(anyInt(), anyInt())).thenReturn(testProduct);
//
//        ResponseEntity<Products> response = productController.addSubscription(AUTH_HEADER_BASIC, 1, 1);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(testProduct.getProductid(), response.getBody().getProductid());
//        verify(productService, times(1)).addSubscription(1, 1);
//        verify(productController, times(1)).checkAuthorizationHeaders(AUTH_HEADER_BASIC);
//    }
//
//    @Test
//    void testAddSubscription_InvalidProductException() {
//        // Stub the service to throw an exception immediately
//        when(productService.addSubscription(anyInt(), anyInt())).thenThrow(new InvalidProductException("Product not found"));
//
//        try {
//            // This call will throw the exception before checkAuthorizationHeaders is invoked in the controller
//            productController.addSubscription(AUTH_HEADER_BASIC, 1, 99);
//            fail("Should have thrown InvalidProductException");
//        } catch (InvalidProductException e) {
//            assertEquals("Product not found", e.getMessage());
//        }
//        verify(productService, times(1)).addSubscription(1, 99);
//        // checkAuthorizationHeaders is NOT called in this execution path, so do not verify it here.
//    }
//
//    @Test
//    void testRemoveSubscription_Success() {
//        when(productService.removeSubscription(anyInt(), anyInt())).thenReturn(testProduct);
//
//        ResponseEntity<Products> response = productController.removeSubscription(AUTH_HEADER_BASIC, 1, 1);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(testProduct.getProductid(), response.getBody().getProductid());
//        verify(productService, times(1)).removeSubscription(1, 1);
//        verify(productController, times(1)).checkAuthorizationHeaders(AUTH_HEADER_BASIC);
//    }
//
//    @Test
//    void testRemoveSubscription_InvalidSubscriptionException() {
//        when(productService.removeSubscription(anyInt(), anyInt())).thenThrow(new InvalidSubscriptionException("User has not subscribed to this product"));
//
//        try {
//            // This call will throw the exception before checkAuthorizationHeaders is invoked in the controller
//            productController.removeSubscription(AUTH_HEADER_BASIC, 1, 99);
//            fail("Should have thrown InvalidSubscriptionException");
//        } catch (InvalidSubscriptionException e) {
//            assertEquals("User has not subscribed to this product", e.getMessage());
//        }
//        verify(productService, times(1)).removeSubscription(1, 99);
//        // checkAuthorizationHeaders is NOT called in this execution path, so do not verify it here.
//    }
//
//    @Test
//    void testGetSubscriptionList_Success() {
//        List<ProductSubscription> expectedList = Arrays.asList(testProductSubscription);
//        when(productService.getSubscriptionList(anyInt())).thenReturn(expectedList);
//
//        // This controller method returns ResponseEntity<List<ProductSubscription>>
//        ResponseEntity<List<ProductSubscription>> response = productController.getSubscriptionList(1);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertFalse(response.getBody().isEmpty());
//        assertEquals(1, response.getBody().size());
//        assertEquals(testProductSubscription.getSubscriptionId(), response.getBody().get(0).getSubscriptionId());
//        verify(productService, times(1)).getSubscriptionList(1);
//        // No checkAuthorizationHeaders call in this specific controller method, so no verification for it.
//    }
//
//    @Test
//    void testGetSubscriptionList_ProductNotFound() {
//        when(productService.getSubscriptionList(anyInt())).thenThrow(new InvalidProductException("Product not found"));
//
//        try {
//            productController.getSubscriptionList(99);
//            fail("Should have thrown InvalidProductException");
//        } catch (InvalidProductException e) {
//            assertEquals("Product not found", e.getMessage());
//        }
//        verify(productService, times(1)).getSubscriptionList(99);
//    }
//
//    @Test
//    void testFindTopSubscribedProduct_Success() {
//        // Corrected: Mock the return type of findTopSubscribedProduct to be a List<ProductViewDTO>
//        // Adjust this if your actual service method returns a different type (e.g., a single DTO or custom object)
//        ProductViewDTO topProductDTO = new ProductViewDTO();
//        topProductDTO.setProductid(10);
//        topProductDTO.setName("Top Sold Product");
//        topProductDTO.setIsactive(true);
//        topProductDTO.setDescription("Description of top product");
//        topProductDTO.setSubscription_count(500);
//
//        List<ProductViewDTO> expectedTopProductList = Arrays.asList(topProductDTO);
//
//        // Stub the service method to return the prepared list
//        when(productService.findTopSubscribedProduct()).thenReturn(expectedTopProductList);
//
//        ResponseEntity<?> response = productController.findTopSubscribedProduct();
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(expectedTopProductList, response.getBody()); // Assert that the response body matches the list
//        verify(productService, times(1)).findTopSubscribedProduct();
//        // No checkAuthorizationHeaders call in this specific controller method, so no verification for it.
//    }
//
//    @Test
//    void testFindTopSubscribedProduct_NoProductFound() {
//        // Stub the service to return null (or an empty list if that's its behavior for no results)
//        when(productService.findTopSubscribedProduct()).thenReturn(null);
//
//        ResponseEntity<?> response = productController.findTopSubscribedProduct();
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(response.getBody()); // Assert that the body is null if no product is found
//        verify(productService, times(1)).findTopSubscribedProduct();
//    }
//}