package com.cts.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="productid")
    private int productid;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="image_url") 
    private String imageUrl;

    @Column(name="isactive")
    private Boolean isActive = true;

    @Column(name="addedon")
    private LocalDateTime addedOn = LocalDateTime.now();

    @Column(name="updatedon")
    private LocalDateTime updatedOn = LocalDateTime.now();

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, mappedBy = "products")
    @JsonManagedReference
    private List<ProductSubscription> productSubscriptionList;
}