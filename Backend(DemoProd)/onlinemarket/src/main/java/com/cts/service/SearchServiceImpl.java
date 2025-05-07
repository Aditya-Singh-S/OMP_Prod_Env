package com.cts.service;
 
import java.util.ArrayList;

import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cts.dto.ProductViewDTO;

import com.cts.entity.Products;
import com.cts.exception.InvalidProductException;
import com.cts.exception.InvalidSearchCriteriaException;
import com.cts.repository.ProductRepository;

import com.cts.repository.ProductViewRepository;
 
@Service

public class SearchServiceImpl implements SearchService{

//	

//	@Autowired

//	ProductRepository productRepository;

	@Autowired

	ProductViewRepository productViewRepository;
	
	@Autowired
	SearchValidationService searchValidationService;

	@Override

	public List<ProductViewDTO> searchProductByName(String name)  {
		searchValidationService.validateProductName(name);
		List<ProductViewDTO> products = productViewRepository.findByNameContainingIgnoreCase(name);
		if(products.isEmpty() || products==null) {
			throw new InvalidProductException("Product name does not exist");
		}
		
		return products;

//		List<ProductDTO> productDTOs = new ArrayList<>();

//	    

//	    for (Products product : products) {

//	        productDTOs.add(new ProductDTO(product));

//	    }

//	    return productDTOs;

	}
 
	@Override

	public List<ProductViewDTO> searchProductBySubsCount(int count) {

		// TODO Auto-generated method stub
		
		searchValidationService.validateSubsCount(count);
		return productViewRepository.searchBySubsCount(count);

//		List<ProductDTO> productDTOs = new ArrayList<>();

//	    

//	    for (Products product : products) {

//	        productDTOs.add(new ProductDTO(product));

//	    }

//	    return productDTOs;

	}
 
	@Override

	public List<ProductViewDTO> searchProductByRating(double rating) {

		// TODO Auto-generated method stub
		
		searchValidationService.validateRating(rating);
		return productViewRepository.searchProductByRating(rating);

//		List<ProductDTO> productDTOs = new ArrayList<>();

//	    

//	    for (Products product : products) {

//	        productDTOs.add(new ProductDTO(product));

//	    }

//	    return productDTOs;

	}
 
	@Override

	public List<ProductViewDTO> searchProductBySubsCountAndRating(int count, double rating) {

		searchValidationService.validateSearchCriteria(count, rating);
//		searchValidationService.validateSubsCount(count);
//        searchValidationService.validateRating(rating);
		return productViewRepository.searchProductBySubsCountAndRating(count,rating);

//		List<ProductDTO> productDTOs = new ArrayList<>();
//	    
//	    for (Products product : products) {
//	        productDTOs.add(new ProductDTO(product));
//	    }
//		return productDTOs;

	}
 
	@Override

	public List<ProductViewDTO> searchProductByNameSubsRating(String name, int count, double rating) {

		// TODO Auto-generated method stub
		
		searchValidationService.validateSearchCriteria(name,count,rating);
		return productViewRepository.searchProductByNameSubsRating(name, count, rating);

//		List<ProductDTO> productDTOs = new ArrayList<>();

//	    

//	    for (Products product : products) {

//	        productDTOs.add(new ProductDTO(product));

//	    }

//		return productDTOs;

	}

	@Override
	public List<ProductViewDTO> searchProductByNameAndRating(String name, double rating) {
		// TODO Auto-generated method stub
		
		searchValidationService.validateSearchCriteria(name,rating);
		return productViewRepository.searchProductByNameAndRating(name, rating);
	}
	

	@Override
	public List<ProductViewDTO> searchProductByNameAndSubsCount(String name, int count) {
		// TODO Auto-generated method stub
		searchValidationService.validateSearchCriteria(name, count);
		return productViewRepository.searchProductByNameAndSubsCount(name, count);
	}
 
}
