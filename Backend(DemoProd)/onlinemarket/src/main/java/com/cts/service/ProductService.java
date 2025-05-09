//package com.cts.service;
//
//import java.io.IOException;
//import java.util.List;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import com.cts.dto.ProductDTO;
//import com.cts.dto.ProductRatingSubscriptionDTO;
//import com.cts.entity.ProductSubscription;
//import com.cts.entity.Products;
//import com.cts.entity.ReviewsAndRatings;
//import com.cts.exception.InvalidProductException;
//
//public interface ProductService {
//	public List<ProductDTO> viewAllProducts();
//	
//	public Products viewProductById(int id);
//	
//	public Products addProduct(String name, String description, MultipartFile imageFile) throws IOException;
//
//	public Products updateProduct(int productId, String name, String description, MultipartFile productImage) throws InvalidProductException, IOException;
//
//	public byte[] getProductImage(int id);
//	
//	public void removeProduct(int productId);
//
//	 public Products addSubscription(int userId,int productId);
//
//	 public Products removeSubscription(int userId,int productId);
//
//	 public List<ProductSubscription> getSubscriptionList(int productId);
//
//	 public List<ProductRatingSubscriptionDTO> getProductSubscriptionList(int userId);
//	
//	//public List<ProductSubscription> getSubscriptionsByEmail(String email);
//
//	//public List<ReviewsAndRatings> getReviewsByEmail(String email);
//	
//	public List<ProductRatingSubscriptionDTO> findTopSubscribedProduct();
//	
//	public List<ProductRatingSubscriptionDTO> findTopRatedProducts();
//	
//}
package com.cts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cts.dto.ProductUploadDTO;
import com.cts.dto.ProductViewDTO;
import com.cts.entity.ProductSubscription;
import com.cts.entity.Products;
import com.cts.entity.User;
import com.cts.exception.InvalidInputException;
import com.cts.exception.InvalidProductException;

public interface ProductService {

    List<ProductViewDTO> viewAllProducts();

    ProductViewDTO viewProductById(int id);

    // Updated addProduct method to take imageFileName
    //Products addProduct(String name, String description, String imageFileName, Boolean isActive) throws IOException;

    // Modified to return byte array for image data
    byte[] getProductImage(int id);
    // Modified to return byte array for image data
    byte[] getProductImageByName(String name);

    Products addSubscription(Integer userId, Integer productId);

    Products removeSubscription(Integer userId, Integer productId);

    List<ProductSubscription> getSubscriptionList(Integer productId);

    List<ProductViewDTO> getProductSubscriptionList(int userId);

    // Updated updateProduct method to take imageFileName
    //Products updateProduct(String name, String upName, String description, String imageFileName, Boolean isActive) throws InvalidProductException, IOException;

    List<ProductViewDTO> findTopSubscribedProduct();

    List<ProductViewDTO> findTopRatedProducts();

    List<User> getUsersSubscribedToProduct(int productId);

//    List<ProductUploadDTO> readProductsFromXlsx(MultipartFile file) throws IOException;
//
//    List<Products> saveProducts(List<ProductUploadDTO> uploadDTOs);

	Products addProduct(String name, String description, MultipartFile imageFile, Boolean isActive) throws IOException;

	Products updateProduct(String name, String upName, String description, MultipartFile imageFile, Boolean isActive)
			throws InvalidInputException, IOException;
}


 
