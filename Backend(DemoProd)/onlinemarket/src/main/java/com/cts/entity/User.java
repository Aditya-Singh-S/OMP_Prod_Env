package com.cts.entity;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
 
import com.cts.enums.UserRole;
import com.cts.exception.AgeValidationException;
import com.cts.exception.PhotoSizeValidationException;
import com.fasterxml.jackson.annotation.JsonManagedReference;
 
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userid")
    private int userID;
 
    @Column(name="firstname")
    private String firstName;
 
    @Column(name="lastname")
    private String lastName;
 
    @Column(name="email")
    private String email;
 
//    @Column(nullable = false)
   	@Column(name="password")
    private String password;
 
    @Column(name="nickname")
    private String nickName;
 
    @Column(name="addressline1")
    private String addressLine1;
 
    @Column(name="addressline2")
    private String addressLine2;
 
    @Column(name="postalcode")
    private String postalCode;
 
//   @Column(nullable = false)
    private String address;
 
    @Column(name="contactnumber")
    private String contactNumber;
 
    @Column
    private String photo;
 
    @Column(name="dateofbirth")
    private String dateOfBirth;
 
    @Enumerated(EnumType.STRING)
//   @Column(nullable = false)
    @Builder.Default
    @Column(name="userrole")
    private UserRole userRole=UserRole.USER;
 
    @Builder.Default
    @Column(name="emailverification")
    private boolean emailVerification=true;
 
    @Column(name="isactive")
    @Builder.Default
    private boolean isActive=true;
 
//   @Column(nullable = false)
    @Builder.Default
    @Column(name="addedon")
    private LocalDateTime addedOn=LocalDateTime.now();
 
//    @Column(name="updatedon", nullable = false)
 
    @Column(name="updatedon")
    @Builder.Default
    private LocalDateTime updatedOn=LocalDateTime.now();
 
 
    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, mappedBy="user")
    @JsonManagedReference
    private List<ProductSubscription> productSubscriptionList;
 
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ReviewsAndRatings> reviewAndRating;
 
}