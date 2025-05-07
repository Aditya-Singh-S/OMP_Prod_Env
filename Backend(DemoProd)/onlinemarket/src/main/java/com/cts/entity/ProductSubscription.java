package com.cts.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "productsubscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "productId"}))
public class ProductSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="subscriptionid")
    private int subscriptionId;
    
    @Builder.Default
    @Column(name="optin")
    private boolean optIn = true;
    
    @Column(name="addedon")
    private LocalDateTime addedOn = LocalDateTime.now();
    
    @Column(name="updatedon")
    private LocalDateTime updatedOn = LocalDateTime.now();

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="userid", referencedColumnName = "userid")
    @JsonBackReference
    private User user;
    
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="productid")
    @JsonBackReference
    private Products products;
    

    public int getUserId() {
        return user != null ? user.getUserID() : null;
    }
}