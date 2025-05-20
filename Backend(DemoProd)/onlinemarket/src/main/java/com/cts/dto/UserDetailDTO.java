package com.cts.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
import java.util.Date;
import java.time.LocalDateTime;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String contactNumber;
    private Date addedOn;
    private Date updatedOn;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private boolean isActive; 
    private String userRole;
    private boolean isEmailVerification;
}