package com.cts.dto;



import com.cts.entity.Products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDTO {
	private int productid;
	private String name;
	private String description;
	private String imageUrl;
	
	public ProductDTO(Products product) {
		// TODO Auto-generated constructor stub
		this.productid = product.getProductid();
		this.name=product.getName();
		this.description = product.getDescription();
		
		//Assigning image URL for retrieving specific data
		this.imageUrl = "OMP/product/image/" + product.getProductid();
	}
}
