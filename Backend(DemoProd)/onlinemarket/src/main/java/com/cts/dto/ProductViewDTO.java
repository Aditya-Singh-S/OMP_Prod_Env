package com.cts.dto;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "product_subscription_and_ratings_view")
@Immutable
public class ProductViewDTO {
	
	@Id
	private int productid;
	private String name;
	private String description;
	private String imageUrl;
	private boolean isactive;
	private double avg_rating;
	private int subscription_count;

	
}
