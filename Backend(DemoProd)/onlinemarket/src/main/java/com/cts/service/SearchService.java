//package com.cts.service;
//
//import java.util.List;
//
//import com.cts.dto.ProductDTO;
//
//public interface SearchService {
//	public List<ProductDTO> searchProductByName(String name);
//	
//	public List<ProductDTO> searchProductBySubsCount(int count);
//
//	public List<ProductDTO> searchProductByRating(double rating);
//
//	public List<ProductDTO> searchProductBySubsCountAndRating(int count, double rating);
//	
//	public List<ProductDTO> searchProductByNameSubsRating(String name, int count, double rating);
//}

package com.cts.service;
 
import java.util.List;
 
import com.cts.dto.ProductViewDTO;
 
public interface SearchService {
	public List<ProductViewDTO> searchProductByName(String name);
	public List<ProductViewDTO> searchProductBySubsCount(int count);
 
	public List<ProductViewDTO> searchProductByRating(double rating);
 
	public List<ProductViewDTO> searchProductBySubsCountAndRating(int count, double rating);
	public List<ProductViewDTO> searchProductByNameSubsRating(String name, int count, double rating);
	public List<ProductViewDTO> searchProductByNameAndRating(String name, double rating);
	public List<ProductViewDTO> searchProductByNameAndSubsCount(String name, int count);
	
}