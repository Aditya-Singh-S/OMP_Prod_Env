//package com.cts.repository;
//
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.cts.entity.ProductSubscription;
//import com.cts.entity.Products;
//
//@Repository
//public interface ProductRepository extends JpaRepository<Products, Integer> {
//	
//	List<Products> findByNameContainingIgnoreCase(String name);
//	
//	@Query(value = "SELECT * FROM product_subscription_view WHERE subscription_count >= :count", nativeQuery = true)
//	List<Products> searchBySubsCount(@Param("count") int count);
//
//	@Query(value="SELECT * FROM product_ratings_view WHERE avg_rating >= :rating", nativeQuery = true)
//	List<Products> searchProductByRating(double rating);
//
//	@Query(value="SELECT * FROM product_subscription_and_ratings_view WHERE subscription_count>= :count and avg_rating >= :rating", nativeQuery = true)
//	List<Products> searchProductBySubsCountAndRating(int count, double rating);
//	
//	@Query("SELECT subscription FROM ProductSubscription subscription WHERE subscription.subscriptionId = :subscriptionId")
//    Optional<ProductSubscription> findSubscriptionById(@Param("subscriptionId") int subscriptionId);
//	
//
//	@Query(value = "SELECT * FROM product_subscription_and_ratings_view WHERE " +
//            "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%',:name,'%'))) AND " +
//            "(:count IS NULL OR subscription_count >= :count) AND " +
//            "(:rating IS NULL OR avg_rating >= :rating)", nativeQuery=true)
//	List<Products> searchProductByNameSubsRating(String name, int count, double rating);
//
//	@Query(value="SELECT productid FROM productsubscriptions WHERE optin = 1 "
//			+ "GROUP BY productid "
//			+ "ORDER BY COUNT(*)"
//			+ "DESC LIMIT 2",nativeQuery=true)
//	List<Integer> findTopSubscribedProduct();
//	
//}

package com.cts.repository;
 
 
import java.util.List;

import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import com.cts.dto.ProductViewDTO;
import com.cts.entity.ProductSubscription;

import com.cts.entity.Products;
 
@Repository

public interface ProductRepository extends JpaRepository<Products, Integer> {
 
	
 
	

	@Query("SELECT subscription FROM ProductSubscription subscription WHERE subscription.subscriptionId = :subscriptionId")

    Optional<ProductSubscription> findSubscriptionById(@Param("subscriptionId") int subscriptionId);

 
	Optional<Products> findByName(String name);
	

}

 