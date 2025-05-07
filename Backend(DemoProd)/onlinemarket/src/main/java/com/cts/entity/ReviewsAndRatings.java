package com.cts.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reviewsandratings", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "productId"}))
public class ReviewsAndRatings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ratingid")
    private long ratingId;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="userid", referencedColumnName = "userid")
    @JsonBackReference
    private User user;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="productid")
    @JsonBackReference
    private Products products;
    
    @Column(name = "isactive", nullable = false)
    private boolean reviewActiveStatus = true;
    
    @Column(name="rating")
    private double rating;
    
    @Column(name="review")
    private String review;
    
    @Column(name="addedon")
    private Timestamp reviewCreatedOn;
    
    @Column(name = "updatedon")
    private Timestamp reviewUpdateOn;
    
    //private Timestamp reviewDeletedOn;

}
