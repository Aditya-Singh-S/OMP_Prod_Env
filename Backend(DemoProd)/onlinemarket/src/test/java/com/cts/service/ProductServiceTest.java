//
//package com.cts.service;
// 
//import com.cts.dto.ProductViewDTO;
//import com.cts.entity.ProductSubscription;
//import com.cts.entity.Products;
//import com.cts.entity.User;
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
//import org.springframework.test.util.ReflectionTestUtils;
// 
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
//    private ProductService productService;
// 
//    @Mock // Mock the Products object
//    private Products testProduct;
// 
//    private User testUser;
// 
//    @Mock // Mock the ProductSubscription object
//    private ProductSubscription testProductSubscription;
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
//    }
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