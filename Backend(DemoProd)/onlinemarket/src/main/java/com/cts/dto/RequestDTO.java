package com.cts.dto;
 
import java.util.Date;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String photo;
    private String nickName;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private String contactNumber;
    private String dateOfBirth;
 
    public String getAddress() {
        return String.format("%s, %s, %s", addressLine1, addressLine2, postalCode);
    }
}