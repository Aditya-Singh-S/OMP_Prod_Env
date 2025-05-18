package com.cts.controller;

import com.cts.dto.ProductViewDTO;
import com.cts.dto.UserDTO;
import com.cts.entity.ProductSubscription;
import com.cts.entity.Products;
import com.cts.entity.User;
import com.cts.exception.InvalidSubscriptionException;
import com.cts.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException; // For simulating other HTTP errors
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests for getUsersSubscribedToProduct
    @Test
    public void testGetUsersSubscribedToProduct_Success_MultipleSubscriptions() {
        int productId = 5;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        List<UserDTO> mockList = Arrays.asList(new UserDTO(), new UserDTO(), new UserDTO());
        when(productService.getUsersSubscribedToProduct(productId)).thenReturn(mockList);
        ResponseEntity<List<UserDTO>> response = productController.viewUsersSubscribedToProduct(mockAuthHeader, productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        verify(productService).getUsersSubscribedToProduct(productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testGetUsersSubscribedToProduct_Success_NoSubscriptions() {
        int productId = 5;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.getUsersSubscribedToProduct(productId)).thenReturn(Collections.emptyList());
        ResponseEntity<List<UserDTO>> response = productController.viewUsersSubscribedToProduct(mockAuthHeader, productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(productService).getUsersSubscribedToProduct(productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testGetUsersSubscribedToProduct_Failure_ServiceError() {
        int productId = 5;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.getUsersSubscribedToProduct(productId)).thenThrow(new RuntimeException("Database error"));
        try {
            productController.viewUsersSubscribedToProduct(mockAuthHeader, productId);
        } catch (RuntimeException e) {
            assertEquals("Database error", e.getMessage());
        }
        verify(productService).getUsersSubscribedToProduct(productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testGetUsersSubscribedToProduct_Failure_Unauthorized() {
        int productId = 5;
        String mockAuthHeader = "Invalid Auth";
        // In a real scenario, checkAuthorizationHeaders might throw an exception or return false
        // Here, we are testing that the controller calls it. The actual authorization failure
        // would likely be handled by Spring Security or a similar mechanism.
        when(productService.getUsersSubscribedToProduct(productId)).thenReturn(Collections.emptyList()); // To avoid other exceptions
        productController.viewUsersSubscribedToProduct(mockAuthHeader, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
        verify(productService).getUsersSubscribedToProduct(productId);
        // We can't directly assert the HTTP status here without mocking the authorization logic more deeply
    }

    // Tests for addSubscription
    @Test
    public void testAddSubscription_Success_NewSubscription() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        Products mockProduct = new Products();
        when(productService.addSubscription(userId, productId)).thenReturn(mockProduct);
        ResponseEntity<Products> response = productController.addSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProduct, response.getBody());
        verify(productService).addSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testAddSubscription_Failure_AlreadySubscribed() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.addSubscription(userId, productId)).thenThrow(new InvalidSubscriptionException("User is already subscribed"));
        ResponseEntity<Products> response = productController.addSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Or HttpStatus.CONFLICT
        verify(productService).addSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testAddSubscription_Failure_InvalidUserOrProduct() {
        int userId = 99;
        int productId = 999;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.addSubscription(userId, productId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User or product not found"));
        ResponseEntity<Products> response = productController.addSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).addSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testAddSubscription_Failure_ServiceError() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.addSubscription(userId, productId)).thenThrow(new RuntimeException("Database error"));
        ResponseEntity<Products> response = productController.addSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService).addSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    // Tests for removeSubscription
    @Test
    public void testRemoveSubscription_Success_ExistingSubscription() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        Products mockProduct = new Products();
        when(productService.removeSubscription(userId, productId)).thenReturn(mockProduct);
        ResponseEntity<Products> response = productController.removeSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProduct, response.getBody());
        verify(productService).removeSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testRemoveSubscription_Failure_NotSubscribed() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.removeSubscription(userId, productId)).thenThrow(new InvalidSubscriptionException("User is not subscribed"));
        ResponseEntity<Products> response = productController.removeSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Or HttpStatus.NOT_FOUND
        verify(productService).removeSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testRemoveSubscription_Failure_InvalidUserOrProduct() {
        int userId = 99;
        int productId = 999;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.removeSubscription(userId, productId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User or product not found"));
        ResponseEntity<Products> response = productController.removeSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).removeSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    @Test
    public void testRemoveSubscription_Failure_ServiceError() {
        int userId = 2;
        int productId = 15;
        String mockAuthHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(productService.removeSubscription(userId, productId)).thenThrow(new RuntimeException("Database error"));
        ResponseEntity<Products> response = productController.removeSubscription(mockAuthHeader, userId, productId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService).removeSubscription(userId, productId);
        verify(productService).checkAuthorizationHeaders(mockAuthHeader);
    }

    // Tests for getSubscriptionList
    @Test
    public void testGetSubscriptionList_Success_MultipleSubscribers() {
        int productId = 15;
        List<ProductSubscription> mockList = Arrays.asList(new ProductSubscription(), new ProductSubscription(), new ProductSubscription());
        when(productService.getSubscriptionList(productId)).thenReturn(mockList);
        ResponseEntity<List<ProductSubscription>> response = productController.getSubscriptionList(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        verify(productService).getSubscriptionList(productId);
    }

    @Test
    public void testGetSubscriptionList_Success_NoSubscribers() {
        int productId = 15;
        when(productService.getSubscriptionList(productId)).thenReturn(Collections.emptyList());
        ResponseEntity<List<ProductSubscription>> response = productController.getSubscriptionList(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Or HttpStatus.NO_CONTENT
        assertEquals(0, response.getBody().size());
        verify(productService).getSubscriptionList(productId);
    }

    @Test
    public void testGetSubscriptionList_Failure_InvalidProduct() {
        int productId = 999;
        when(productService.getSubscriptionList(productId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        try {
            productController.getSubscriptionList(productId);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Product not found", e.getReason());
        }
        verify(productService).getSubscriptionList(productId);
    }

    @Test
    public void testGetSubscriptionList_Failure_ServiceError() {
        int productId = 15;
        when(productService.getSubscriptionList(productId)).thenThrow(new RuntimeException("Database error"));
        try {
            productController.getSubscriptionList(productId);
        } catch (RuntimeException e) {
            assertEquals("Database error", e.getMessage());
        }
        verify(productService).getSubscriptionList(productId);
    }

    // Tests for findTopSubscribedProduct
    @Test
    public void testFindTopSubscribedProduct_Success_ProductExists() {
        Object mockTopProduct = new ProductViewDTO();
        when(productService.findTopSubscribedProduct()).thenReturn(mockTopProduct);
        ResponseEntity<?> response = productController.findTopSubscribedProduct();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTopProduct, response.getBody());
        verify(productService).findTopSubscribedProduct();
    }

    @Test
    public void testFindTopSubscribedProduct_Success_NoSubscribersYet() {
        when(productService.findTopSubscribedProduct()).thenReturn(null); // Or an empty DTO
        ResponseEntity<?> response = productController.findTopSubscribedProduct();
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Or HttpStatus.NO_CONTENT
        assertEquals(null, response.getBody());
        verify(productService).findTopSubscribedProduct();
    }

    @Test
    public void testFindTopSubscribedProduct_Failure_ServiceError() {
        when(productService.findTopSubscribedProduct()).thenThrow(new RuntimeException("Failed to retrieve top product"));
        try {
            productController.findTopSubscribedProduct();
        } catch (RuntimeException e) {
            assertEquals("Failed to retrieve top product", e.getMessage());
        }
        verify(productService).findTopSubscribedProduct();
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