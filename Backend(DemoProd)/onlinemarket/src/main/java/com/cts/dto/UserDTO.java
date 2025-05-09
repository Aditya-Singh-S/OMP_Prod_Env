package com.cts.dto;
 
import java.util.Date;
 
import com.cts.entity.User;
import com.cts.enums.UserRole;
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String firstName;
 
    private String lastName;
 
    private String email;
 
    private String photo;
 
    private String nickName;
 
    private String address;
 
    private String contactNumber;
 
    private String dateOfBirth;
    private UserRole userRole;
    public UserDTO(User user) {
    	this.firstName = user.getFirstName();
    	this.lastName = user.getLastName();
    	this.address = user.getAddress();
    	this.email = user.getEmail();
    	this.nickName = user.getNickName();
    	this.contactNumber = user.getContactNumber();
    	this.dateOfBirth = user.getDateOfBirth();
    	this.userRole = user.getUserRole();
    	this.photo = "OMP/user/image/" + user.getUserID();
    }
}