//package com.cts.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.cts.dto.ProductViewDTO;
//import com.cts.entity.Products;
//
//public interface ProductViewRepository extends JpaRepository<ProductViewDTO, Integer> {
//	
//	List<ProductViewDTO> findByNameContainingIgnoreCase(String name);
//	
//	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE subscription_count >= :count", nativeQuery = true)
//	List<ProductViewDTO> searchBySubsCount(@Param("count") int count);
//	
//	@Query(value="SELECT * FROM product_subscription_and_ratings_view WHERE avg_rating >= :rating", nativeQuery = true)
//	List<ProductViewDTO> searchProductByRating(double rating);
//	
//	@Query(value="SELECT * FROM product_subscription_and_ratings_view WHERE subscription_count>= :count and avg_rating >= :rating", nativeQuery = true)
//	List<ProductViewDTO> searchProductBySubsCountAndRating(int count, double rating);
//	
//	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE " +
//            "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +
//            "(:count IS NULL OR subscription_count >= :count) AND " +
//            "(:rating IS NULL OR avg_rating >= :rating)", nativeQuery=true)
//	List<ProductViewDTO> searchProductByNameSubsRating(String name, int count, double rating);
//	
//	
//	
//	@Query(value=""" 
//			SELECT * FROM product_subscription_and_ratings_view 
//			WHERE isactive = 1 ORDER BY subscription_count DESC LIMIT 2
//			""", nativeQuery = true)
//	List<ProductViewDTO> findTopSubscribedProduct();
//	
//	
//	@Query(value=""" 
//			SELECT * FROM product_subscription_and_ratings_view 
//			WHERE isactive = 1 ORDER BY avg_rating DESC LIMIT 2
//			""", nativeQuery = true)
//	List<ProductViewDTO> findTopRatedProducts();
//}

package com.cts.repository;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
 
import com.cts.dto.ProductViewDTO;

import com.cts.entity.Products;
 
public interface ProductViewRepository extends JpaRepository<ProductViewDTO, Integer> {

	List<ProductViewDTO> findByNameContainingIgnoreCase(String name);

	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE subscription_count >= :count", nativeQuery = true)

	List<ProductViewDTO> searchBySubsCount(@Param("count") int count);

	@Query(value="SELECT * FROM product_subscription_and_ratings_view WHERE avg_rating >= :rating", nativeQuery = true)

	List<ProductViewDTO> searchProductByRating(double rating);

	@Query(value="SELECT * FROM product_subscription_and_ratings_view WHERE subscription_count>= :count and avg_rating >= :rating", nativeQuery = true)

	List<ProductViewDTO> searchProductBySubsCountAndRating(int count, double rating);

	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE " +

            "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +

            "(:count IS NULL OR subscription_count >= :count) AND " +

            "(:rating IS NULL OR avg_rating >= :rating)", nativeQuery=true)

	List<ProductViewDTO> searchProductByNameSubsRating(String name, int count, double rating);
	
	
	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE " +

            "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +

            "(:rating IS NULL OR avg_rating >= :rating)", nativeQuery=true)
	List<ProductViewDTO> searchProductByNameAndRating(String name, double rating);
	
	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE " +

            "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +

            "(:count IS NULL OR subscription_count >= :count)", nativeQuery=true)
	List<ProductViewDTO> searchProductByNameAndSubsCount(String name, int count);

	
	@Query(value=""" 

			SELECT * FROM product_subscription_and_ratings_view 

			WHERE isactive = 1 ORDER BY subscription_count DESC LIMIT 2

			""", nativeQuery = true)

	List<ProductViewDTO> findTopSubscribedProduct();


	@Query(value=""" 

			SELECT * FROM product_subscription_and_ratings_view 

			WHERE isactive = 1 ORDER BY avg_rating DESC LIMIT 2

			""", nativeQuery = true)

	List<ProductViewDTO> findTopRatedProducts();
	
	@Query(value="SELECT DISTINCT p.* FROM product_subscription_and_ratings_view p " +
			" JOIN productsubscriptions ps ON ps.productid = p.productid " +
			" WHERE ps.userid = :userid AND ps.optin = true", nativeQuery=true)
	List<ProductViewDTO> getSubscribedListByUser(int userid);

}

 