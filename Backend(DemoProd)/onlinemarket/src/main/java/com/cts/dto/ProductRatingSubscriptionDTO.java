package com.cts.dto;
 
import java.util.List;
 
import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;
 
@Data

@AllArgsConstructor

@NoArgsConstructor

@Builder

public class ProductRatingSubscriptionDTO {

	private ProductDTO productDTO;

	private double rating;

	private int subscriptionCount;

}

 