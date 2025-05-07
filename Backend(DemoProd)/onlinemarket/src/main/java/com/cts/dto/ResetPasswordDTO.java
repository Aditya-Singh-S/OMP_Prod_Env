//package com.cts.dto;
// 
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
// 
//@Data
//@NoArgsConstructor
//@Getter
//@Setter
//@AllArgsConstructor
//public class ResetPasswordDTO
//{
//    private String email;
//    private String newPassword;
//    private String confirmPassword;
//}
// 
package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\s/]).{6,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, no spaces, and no slashes")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
