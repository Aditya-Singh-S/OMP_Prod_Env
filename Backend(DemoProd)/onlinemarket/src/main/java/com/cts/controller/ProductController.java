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
@CrossOrigin(origins = "http://127.0.0.1:3000")
@RequestMapping("/OMP")
@Validated
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
            @RequestParam("imageFile") MultipartFile file,
            @RequestParam(required = false, value = "isActive") Boolean isActive) throws IOException {
        Products newProduct = productService.addProduct(name, description, file, isActive);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping("/viewAllProducts")
    public ResponseEntity<List<ProductViewDTO>> viewAllProducts() {
        List<ProductViewDTO> products = productService.viewAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // API call for viewing specific product using specific Product id
    @GetMapping("/viewProductDetails/{productId}")
    public ResponseEntity<ProductViewDTO> viewProductDetails(@PathVariable @Positive int productId) {
        ProductViewDTO product = productService.viewProductById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // API call for fetching image of specific product using id
    @GetMapping("/product/image/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable @jakarta.validation.constraints.Positive int id) {
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
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new ByteArrayResource(imageData));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("admin/updateProduct/{name}")
    public ResponseEntity<Products> updateProduct(
            @PathVariable String name,
            @RequestParam(required = false, value = "upName") String upName,
            @RequestParam(required = false, value = "description") String description,
            @RequestParam(required = false, value = "file") MultipartFile file,
            @RequestParam(required = false, value = "isActive") Boolean isActive)
            throws Exception {
        Products updatedProduct = productService.updateProduct(name, upName, description, file, isActive);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PostMapping("/addSubscription")
    public ResponseEntity<Products> addSubscription(
            @RequestParam("userId") @Positive Integer userId,
            @RequestParam("productId") @Positive Integer productId) {
        Products result = productService.addSubscription(userId, productId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/removeSubscription")
    public ResponseEntity<Products> removeSubscription(
            @RequestParam("userId") @Positive Integer userId,
            @RequestParam("productId") @Positive Integer productId) {
        Products result = productService.removeSubscription(userId, productId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/viewSubscriptionList")
    public ResponseEntity<List<ProductSubscription>> getSubscriptionList(@RequestParam @Positive Integer productId) {
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
    public ResponseEntity<List<User>> viewUsersSubscribedToProduct(@RequestParam int productId) {
        List<User> subscribedUsers = productService.getUsersSubscribedToProduct(productId);
        return new ResponseEntity<>(subscribedUsers, subscribedUsers.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK);
    }
//    @PostMapping("/admin/uploadMultipleRecords")
//  public ResponseEntity<?> uploadProducts(@RequestParam("file") MultipartFile file) {
//      if (file.isEmpty()) {
//          return new ResponseEntity<>("Please upload an Excel file!", HttpStatus.BAD_REQUEST);
//      }
//
//      String contentType = file.getContentType();
//      if (contentType == null || (!contentType.equals("application/vnd.ms-excel") && !contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {
//          return new ResponseEntity<>("Please upload a valid Excel file (.xls or .xlsx)!", HttpStatus.BAD_REQUEST);
//      }
//
//      try {
//          List<ProductUploadDTO> productUploadDTOs = productService.readProductsFromXlsx(file); // Get ProductUploadDTOs
//          List<Products> savedProducts = productService.saveProducts(productUploadDTOs);   // Pass ProductUploadDTOs
//          return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
//      } catch (IOException e) {
//          return new ResponseEntity<>("Error processing the Excel file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//      } catch (Exception e) {
//          return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//      }
//  }
    
    @PostMapping("/admin/uploadMultipleRecords")
    public ResponseEntity<List<Products>> uploadMultipleProducts(@RequestParam("file") MultipartFile file, @RequestParam boolean bulkProductisactive) {
        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            List<Products> uploadedProducts = productService.addMultipleProducts(file,bulkProductisactive);
            return new ResponseEntity<>(uploadedProducts, HttpStatus.CREATED);
        } catch (IOException e) {
            System.err.println("Error processing Excel file: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
