package com.cts.mapper;
 
import com.cts.dto.ProductDTO;
import com.cts.dto.ResponseDTO;
import com.cts.entity.Products;
 
public class ProductMapper {
 
	public static ProductDTO toDTO(Products product) {
 
        if (product == null) {
            return null;
        }
        return ProductDTO.builder()
        		.productid(product.getProductid())
        		.name(product.getName())
        		.description(product.getDescription())
        		.imageUrl(product.getImageUrl())
                .build();
 
    
	}
}