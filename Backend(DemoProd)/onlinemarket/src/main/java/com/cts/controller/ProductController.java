//package com.cts.controller;
//
//import java.io.IOException;
//
//import java.util.List;
//
//import com.cts.service.ProductServiceImpl;
//
//import org.apache.catalina.connector.Response;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.http.HttpStatus;
//
//import org.springframework.http.MediaType;
//
//import org.springframework.http.ResponseEntity;
//
//import org.springframework.web.bind.annotation.CrossOrigin;
//
//import org.springframework.web.bind.annotation.DeleteMapping;
//
//import org.springframework.web.bind.annotation.GetMapping;
//
//import org.springframework.web.bind.annotation.PathVariable;
//
//import org.springframework.web.bind.annotation.PostMapping;
//
//import org.springframework.web.bind.annotation.PutMapping;
//
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import org.springframework.web.bind.annotation.RequestBody;
//
//import org.springframework.web.bind.annotation.RequestParam;
//
//import org.springframework.web.bind.annotation.RestController;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import com.cts.dto.ProductViewDTO;
//
//import com.cts.entity.Products;
//
//import com.cts.entity.ReviewsAndRatings;
//import com.cts.entity.User;
//import com.cts.exception.InvalidInputException;
//import com.cts.exception.InvalidProductException;
//import com.cts.exception.InvalidSubscriptionException;
//import com.cts.exception.UserNotFoundException;
//
//import com.cts.entity.ProductSubscription;
//
//import com.cts.service.ProductService;
//
//@RestController
//
//@CrossOrigin(origins = "http://127.0.0.1:3000")
//
//@RequestMapping("/OMP")
//
//public class ProductController {
//
//	private final ProductServiceImpl productServiceImpl;
//
//	@Autowired
//
//	ProductService productService;
//
//	ProductController(ProductServiceImpl productServiceImpl) {
//
//		this.productServiceImpl = productServiceImpl;
//	}
//
//	// API call for adding new Product
//
//	@PostMapping("/admin/addProduct")
//
//	public ResponseEntity<Products> createNewProduct(
//
//			@RequestParam String name,
//
//			@RequestParam String description,
//
//			@RequestParam MultipartFile imageFile, @RequestParam Boolean isActive) throws IOException
//
//	{
//
//		return ResponseEntity.ok(productService.addProduct(name, description, imageFile, isActive));
//
//	}
//
//	@GetMapping("/viewAllProducts")
//	public ResponseEntity<List<ProductViewDTO>> viewAllProducts(){
//
//		List<ProductViewDTO> products = productService.viewAllProducts();
//
////		if(products == null || products.isEmpty()) {
////
////			return ResponseEntity.status(HttpStatus.NOT_FOUND)
////
////	                .body("No products available to display.");
////
////		}
//
//		return ResponseEntity.ok(products);
//	}
//
//	// API call for viewing specific product using specific Product id
//
//	@GetMapping("/viewProductDetails/{productId}")
//	public ResponseEntity<ProductViewDTO> viewProductDetails(@PathVariable int productId){
//		// If viewProductById throws InvalidProductException, it will propagate up.
//        ProductViewDTO product = productService.viewProductById(productId);
//        // If successful, return 200 OK with the product DTO
//        return ResponseEntity.ok(product);
//        
//	}
//
//	// API call for fetching image of specific product using id
//
//	@GetMapping("product/image/{id}")
//
//	public ResponseEntity<byte[]> getImage(@PathVariable int id) {
//
//		byte[] image = productService.getProductImage(id);
//
//        // If successful, return 200 OK with image bytes and content type
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG) 
//                .body(image);
//
//	}
//
//	@GetMapping("product/imageByName/{name}")
//
//	public ResponseEntity<byte[]> getImageByName(@PathVariable String name) {
//
//		byte[] image = productService.getProductImageByName(name);
//
//        // If successful, return 200 OK with image bytes and content type
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_PNG)
//                .body(image);
//
//	}
//
//	@PutMapping("admin/updateProduct/{name}")
//	public ResponseEntity<Products> updateProduct(@PathVariable String name,
//			@RequestParam(required = false) String upName, @RequestParam(required = false) String description,
//			@RequestParam(required = false) MultipartFile imageFile, @RequestParam(required = false) Boolean isActive)
//			throws Exception
//
//	{
//		return ResponseEntity.ok(productService.updateProduct(name, upName, description, imageFile, isActive));
//	}
//
//	@PostMapping("/addSubscription")
//
//	public ResponseEntity<Products> addSubscription(@RequestParam(required = false) Integer userId,
//			@RequestParam(required = false) Integer productId) {
//
//		if (userId == null && productId == null)
//			throw new InvalidSubscriptionException("Missing required parameters: userId, productId");
//		else if (userId == null)
//			throw new InvalidSubscriptionException("Missing required parameter: userId");
//		if (productId == null)
//			throw new InvalidSubscriptionException("Missing required parameter: productId");
//		return ResponseEntity.ok(productServiceImpl.addSubscription(userId, productId));
//
//	}
//
//	@PutMapping("/removeSubscription")
//
//	public ResponseEntity<Products> removeSubscription(@RequestParam(required = false) Integer userId,
//			@RequestParam(required = false) Integer productId) {
//
//		if (userId == null && productId == null)
//			throw new InvalidSubscriptionException("Missing required parameters: userId, productId");
//		else if (userId == null)
//			throw new InvalidSubscriptionException("Missing required parameter: userId");
//		if (productId == null)
//			throw new InvalidSubscriptionException("Missing required parameter: productId");
//		return ResponseEntity.ok(productServiceImpl.removeSubscription(userId, productId));
//
//	}
//
//	@GetMapping("/viewSubscriptionList")
//
//	public ResponseEntity<List<ProductSubscription>> getSubscriptionList(@RequestParam(required=false) Integer productId) {
//
//		if(productId == null)
//			throw new InvalidInputException("Missing required parameter : productId");
//		return ResponseEntity.ok(productServiceImpl.getSubscriptionList(productId));
//
//	}
//
//	@GetMapping("/topSubscribedProduct")
//
//	public ResponseEntity<?> findTopSubscribedProduct() {
//
//		return ResponseEntity.ok(productServiceImpl.findTopSubscribedProduct());
//
//	}
//
//
//	@GetMapping("/topRatedProducts")
//
//	public ResponseEntity<?> findTopRatedProducts() {
//
//		List<ProductViewDTO> topRatedProducts = productService.findTopRatedProducts();
//
//		return ResponseEntity.ok(topRatedProducts);
//
//	}
//
//	@GetMapping("/viewUsersSubscribedToProduct")
//	public ResponseEntity<List<User>> viewUsersSubscribedToProduct(@RequestParam int productId) {
//		List<User> subscribedUsers = productService.getUsersSubscribedToProduct(productId);
//
//		if (subscribedUsers.isEmpty()) {
//			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//		}
//		return ResponseEntity.ok(subscribedUsers);
//	}
//
//
//}


package com.cts.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.cts.dto.ProductUploadDTO;
import com.cts.dto.ProductViewDTO;
import com.cts.entity.Products;
import com.cts.entity.ReviewsAndRatings;
import com.cts.entity.User;
import com.cts.exception.InvalidInputException;
import com.cts.exception.InvalidProductException;
import com.cts.exception.InvalidSubscriptionException;
import com.cts.entity.ProductSubscription;
import com.cts.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:3000") // Consider externalizing this
@RequestMapping("/OMP")
@Validated // Needed for @RequestParam validation
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // API call for adding new Product
    @PostMapping("/admin/addProduct")
    public ResponseEntity<Products> createNewProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("imageFile") MultipartFile file, // This should be the filename
            @RequestParam(required=false, value="isActive") Boolean isActive) throws IOException { // Corrected the exception
    	
    	String imageUrl=  Paths.get(file.getOriginalFilename()).getFileName().toString();

        Products newProduct = productService.addProduct(name, description, imageUrl, isActive);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED); // Use 201 Created for successful resource creation
    }

    @GetMapping("/viewAllProducts")
    public ResponseEntity<List<ProductViewDTO>> viewAllProducts() {
        List<ProductViewDTO> products = productService.viewAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK); // Explicitly return the status
    }

    // API call for viewing specific product using specific Product id
    @GetMapping("/viewProductDetails/{productId}")
    public ResponseEntity<ProductViewDTO> viewProductDetails(@PathVariable @Positive int productId) { // Added validation
        ProductViewDTO product = productService.viewProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // API call for fetching image of specific product using id
    @GetMapping("/product/image/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable @jakarta.validation.constraints.Positive int id) { // Added validation
        byte[] imageData = productService.getProductImage(id);
        if (imageData != null && imageData.length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) 
                    .body(new ByteArrayResource(imageData));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/product/imageByName/{name}")
    public ResponseEntity<Resource> getImageByName(@PathVariable String name) {
        byte[] imageData = productService.getProductImageByName(name);
        if (imageData != null && imageData.length > 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // Adjust content type based on your images
                    .body(new ByteArrayResource(imageData));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("admin/updateProduct/{name}")
    public ResponseEntity<Products> updateProduct(
            @PathVariable String name, // This is the name to search for the product
            @RequestParam(required = false, value = "upName") String upName,
            @RequestParam(required = false, value = "description") String description,
            @RequestParam(required = false, value = "file") MultipartFile file,
            @RequestParam(required = false, value = "isActive") Boolean isActive)
            throws Exception {

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = Paths.get(file.getOriginalFilename()).getFileName().toString();
        }
        Products updatedProduct = productService.updateProduct(name, upName, description, imageUrl, isActive);

        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PostMapping("/addSubscription")
    public ResponseEntity<Products> addSubscription(
            @RequestParam("userId") @Positive Integer userId,  // Added validation
            @RequestParam("productId") @Positive Integer productId) { // Added validation

        Products result = productService.addSubscription(userId, productId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/removeSubscription")
    public ResponseEntity<Products> removeSubscription(
            @RequestParam("userId") @Positive Integer userId,  // Added validation
            @RequestParam("productId") @Positive Integer productId) { // Added validation

        Products result = productService.removeSubscription(userId, productId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/viewSubscriptionList")
    public ResponseEntity<List<ProductSubscription>> getSubscriptionList(@RequestParam @Positive Integer productId) { // Added validation
        List<ProductSubscription> subscriptionList = productService.getSubscriptionList(productId);
        return new ResponseEntity<>(subscriptionList, HttpStatus.OK);
    }

    @GetMapping("/topSubscribedProduct")
    public ResponseEntity<?> findTopSubscribedProduct() {
        Object topSubscribedProduct = productService.findTopSubscribedProduct();
        return new ResponseEntity<>(topSubscribedProduct, HttpStatus.OK);
    }

    @GetMapping("/topRatedProducts")
    public ResponseEntity<?> findTopRatedProducts() {
        List<ProductViewDTO> topRatedProducts = productService.findTopRatedProducts();
        return new ResponseEntity<>(topRatedProducts, HttpStatus.OK);
    }

    @GetMapping("/viewUsersSubscribedToProduct")
    public ResponseEntity<List<User>> viewUsersSubscribedToProduct(@RequestParam int productId) { // Added validation
        List<User> subscribedUsers = productService.getUsersSubscribedToProduct(productId);
        return new ResponseEntity<>(subscribedUsers, subscribedUsers.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }

    @PostMapping("/admin/uploadMultipleRecords")
    public ResponseEntity<?> uploadProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please upload an Excel file!", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/vnd.ms-excel") && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
            return new ResponseEntity<>("Please upload a valid Excel file (.xls or .xlsx)!", HttpStatus.BAD_REQUEST);
        }

        try {
            List<ProductUploadDTO> productUploadDTOs = productService.readProductsFromXlsx(file); // Get ProductUploadDTOs
            List<Products> savedProducts = productService.saveProducts(productUploadDTOs);   // Pass ProductUploadDTOs
            return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error processing the Excel file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}