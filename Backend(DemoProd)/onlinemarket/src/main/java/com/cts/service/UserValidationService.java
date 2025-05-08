 
package com.cts.service;
 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;
 
import com.cts.entity.User;
import com.cts.exception.AgeValidationException;
import com.cts.exception.DuplicateEmailException;
import com.cts.exception.InvalidEmailFormatException;
import com.cts.exception.InvalidInputException;
import com.cts.exception.PhotoSizeValidationException;
import com.cts.repository.UserRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
public class UserValidationService {
 
    @Autowired
    private UserRepository userRepository;
 
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
 
    public void validate(User user) {
        validateMandatoryFields(user);
        validateAge(user.getDateOfBirth());
//        validatePhotoSize(user.getPhoto());
    }
 
    public void validateOnUpdate(User user) {
        validateMandatoryFieldsOnUpdate(user);
        validateAgeOnUpdate(user.getDateOfBirth());
//        validatePhotoSizeOnUpdate(user.getPhoto());
        checkPostalCodeAndDateOfBirthOnUpdate(user.getPostalCode(), user.getDateOfBirth());
    }
    
    public void validateAdminAddUser(User user) {
    	validateMandatoryFieldsAdminAddUser(user);
        validateAge(user.getDateOfBirth());
//        validatePhotoSize(user.getPhoto());
    }
 
    private void validateMandatoryFields(User user) {
        isValidFirstName(user.getFirstName());
        isValidLastName(user.getLastName());
        isValidNickName(user.getNickName());
        isValidEmail(user.getEmail());
        isValidContactNumber(user.getContactNumber());
        isValidPassword(user.getPassword());
        isValidAddressLine1(user.getAddressLine1());
        isValidAddressLine2(user.getAddressLine2());
        isValidPostalCode(user.getPostalCode());
        isValidDateOfBirth(user.getDateOfBirth());
    }
 
 
    private void validateMandatoryFieldsOnUpdate(User user) {
        isValidFirstNameOnUpdate(user.getFirstName());
        isValidLastNameOnUpdate(user.getLastName());
        isValidNickNameOnUpdate(user.getNickName());
        isValidEmailOnUpdate(user.getEmail());
        isValidContactNumberOnUpdate(user.getContactNumber());
        isValidAddressLine1OnUpdate(user.getAddressLine1());
        isValidAddressLine2OnUpdate(user.getAddressLine2());
        isValidPostalCodeOnUpdate(user.getPostalCode());
        isValidDateOfBirthOnUpdate(user.getDateOfBirth());
 
    }
    
    private void validateMandatoryFieldsAdminAddUser(User user) {
    	isValidFirstName(user.getFirstName());
        isValidLastName(user.getLastName());
        isValidNickName(user.getNickName());
        isValidEmail(user.getEmail());
        isValidContactNumber(user.getContactNumber());
        isValidAddressLine1(user.getAddressLine1());
        isValidAddressLine2(user.getAddressLine2());
        isValidPostalCode(user.getPostalCode());
        isValidDateOfBirth(user.getDateOfBirth());
    }
 
 
    //checking the validation of the first name
    private void isValidFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidInputException("First Name cannot be blank.");
        }
        if (firstName.length() < 3 || firstName.length() > 15) {
            throw new InvalidInputException("Invalid Format of First Name: Length must be between 3 and 15 characters.");
        }
        if (!firstName.matches("[a-zA-Z._0-9]+")) {
            throw new InvalidInputException("Invalid Format of First Name: Only alphabetic characters, dots, and underscores are allowed.");
        }
    }
 
    private void isValidFirstNameOnUpdate(String firstName) {
        if (firstName != null) {
            if (firstName.isBlank()) {
                throw new InvalidInputException("First Name cannot be blank.");
            }
            if (firstName.length() < 3 || firstName.length() > 15 || !firstName.matches("[a-zA-Z._0-9]+")) {
                throw new InvalidInputException("Invalid Format of First Name: Length must be between 3 and 15 characters, and only alphabetic characters, dots, and underscores are allowed.");
            }
        }
    }
 
 
    //checking the validation of the last name
    private void isValidLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidInputException("Last Name cannot be blank.");
        }
        if (lastName.length() < 3 || lastName.length() > 15) {
            throw new InvalidInputException("Invalid Format of Last Name: Length must be between 3 and 15 characters.");
        }
        if (!lastName.matches("[a-zA-Z._0-9]+")) {
            throw new InvalidInputException("Invalid Format of Last Name: Only alphabetic characters, dots, and underscores are allowed.");
        }
    }
 
    private void isValidLastNameOnUpdate(String lastName) {
        if (lastName != null) {
            if (lastName.isBlank()) {
                throw new InvalidInputException("Last Name cannot be blank.");
            }
            if (lastName.length() < 3 || lastName.length() > 15 || !lastName.matches("[a-zA-Z._0-9]+")) {
                throw new InvalidInputException("Invalid Format of Last Name: Length must be between 3 and 15 characters, and only alphabetic characters, dots, and underscores are allowed.");
            }
        }
    }
 
 
    //validating nick name format
    private void isValidNickName(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            throw new InvalidInputException("Nick Name cannot be blank.");
        }
        if (nickName.length() < 3 || !nickName.matches("[a-zA-Z]+")) {
            throw new InvalidInputException("Invalid Format of Nick Name: Length must be at least 3 characters and contain only alphabetic characters.");
        }
    }
 
    private void isValidNickNameOnUpdate(String nickName) {
        if (nickName != null) {
            if (nickName.isBlank()) {
                throw new InvalidInputException("Nick Name cannot be blank.");
            }
            if (nickName.length() < 3 || !nickName.matches("[a-zA-Z]+")) {
                throw new InvalidInputException("Invalid Format of Nick Name: Length must be at least 3 characters and contain only alphabetic characters.");
            }
        }
    }
 
 
    //checking that if the email is valid/in format
    private void isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidInputException("Email cannot be blank.");
        }
        String emailRegex = "^[a-zA-Z0-9]+([._-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]+\\.(com|net|org)$";
        Pattern pattern = Pattern.compile(emailRegex);
 
        if (!pattern.matcher(email).matches()) {
            throw new InvalidEmailFormatException("Invalid email format.");
        }
 
        if (isDuplicateEmail(email)) {
            throw new DuplicateEmailException("Duplicate email: Email already exists in the database.");
        }
    }
 
    private void isValidEmailOnUpdate(String email) {
        if (email != null) {
            if (email.isBlank()) {
                throw new InvalidInputException("Email cannot be blank during update.");
            }
            String emailRegex = "^[a-zA-Z0-9]+([._-]?[a-zA-Z0-9]+)*@[a-zA-Z0-9]+\\.(com|net|org)$";
            Pattern pattern = Pattern.compile(emailRegex);
            if (!pattern.matcher(email).matches()) {
                throw new InvalidInputException("Invalid email format.");
            }
        }
    }
 
 
    //duplicate email
    private boolean isDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }
 
 
    //contact number validation
    private void isValidContactNumber(String contactNumber) {
        if (contactNumber == null || contactNumber.isBlank()) {
            throw new InvalidInputException("Contact Number cannot be blank.");
        }
        if (contactNumber.length() != 10 || !contactNumber.matches("[1-9][0-9]{9}")) {
            throw new InvalidInputException("Wrong format of contact number: Must be 10 digits and start with 1-9.");
        }
    }
 
    private void isValidContactNumberOnUpdate(String contactNumber) {
        if (contactNumber != null) {
            if (contactNumber.isBlank()) {
                throw new InvalidInputException("Contact Number cannot be blank.");
            }
            if (contactNumber.length() != 10 || !contactNumber.matches("[1-9][0-9]{9}")) {
                throw new InvalidInputException("Wrong format of contact number: Must be 10 digits and start with 1-9.");
            }
        }
    }
 
 
    //password validation
    private void isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidInputException("Password cannot be blank.");
        }
        String valid_password = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])[a-zA-Z0-9!@#$%^&*(),.?\":{}|<>]{6,}$";
        if (password.contains(" ") || password.contains("/")) {
            throw new InvalidInputException("Password must not contain spaces or slashes (/).");
        }
 
        if (!password.matches(valid_password)) {
            throw new InvalidInputException("Password must contain at least one numeric digit, one lowercase letter, one uppercase letter, one special character, and be at least 6 characters long.");
        }
    }
 
 
    // Validate Address Line 1
    private void isValidAddressLine1(String addressLine1) {
        if (addressLine1 == null || addressLine1.isBlank()) {
            throw new InvalidInputException("Address Line 1 cannot be blank.");
        }
        if (addressLine1.length() < 10) {
            throw new InvalidInputException("Address Line 1 must have at least 10 characters.");
        }
    }
 
    private void isValidAddressLine1OnUpdate(String addressLine1) {
        if (addressLine1 != null) {
            if (addressLine1.isBlank()) {
                throw new InvalidInputException("Address Line 1 cannot be blank.");
            }
            if (addressLine1.length() < 10) {
                throw new InvalidInputException("Address Line 1 must have at least 10 characters.");
            }
        }
    }
 
 
    // Validate Address Line 2
    private void isValidAddressLine2(String addressLine2) {
        if (addressLine2 == null || addressLine2.isBlank()) {
            throw new InvalidInputException("Address Line 2 cannot be blank.");
        }
        if (addressLine2.length() < 10) {
            throw new InvalidInputException("Address Line 2 must have at least 10 characters.");
        }
    }
 
    private void isValidAddressLine2OnUpdate(String addressLine2) {
        if (addressLine2 != null) {
            if (addressLine2.isBlank()) {
                throw new InvalidInputException("Address Line 2 cannot be blank.");
            }
            if (addressLine2.length() < 10) {
                throw new InvalidInputException("Address Line 2 must have at least 10 characters.");
            }
        }
    }
 
 
    //validate postal code
    private void isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            throw new InvalidInputException("Postal Code cannot be blank.");
        }
        if (!postalCode.matches("[0-9]{6}")) {
            throw new InvalidInputException("Invalid Postal Code: Must be exactly 6 digits.");
        }
        try {
            int code = Integer.parseInt(postalCode);
            if (code < 100000 || code > 999999) {
                throw new InvalidInputException("Invalid Postal Code");
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Invalid Postal Code: Must be numeric");
        }
    }
 
    public void isValidPostalCodeOnUpdate(String postalCode) {
        if (postalCode != null) {
            if (!postalCode.matches("[0-9]{6}")) {
                throw new InvalidInputException("Invalid Postal Code: Must be exactly 6 digits.");
            }
            try {
                int code = Integer.parseInt(postalCode);
                if (code < 100000 || code > 999999) {
                    throw new InvalidInputException("Invalid Postal Code");
                }
            } catch (NumberFormatException e) {
                throw new InvalidInputException("Invalid Postal Code: Must be numeric");
            }
        }
 
    }
 
    // Validate Date of Birth
    private void isValidDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isBlank()) {
            throw new InvalidInputException("Date of Birth cannot be blank.");
        }
 
        try {
            LocalDate.parse(dateOfBirth, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid Date of Birth format.  Use YYYY-MM-DD");
        }
    }
 
    private void isValidDateOfBirthOnUpdate(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isBlank()) {
            throw new InvalidInputException("Date of Birth cannot be blank.");
        }
        try {
            LocalDate.parse(dateOfBirth, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid Date of Birth format.  Use YYYY-MM-DD");
        }
    }
 
    //validate the age of the user(must be greater or equal to 18 years old)
    private void validateAge(String dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Invalid Date of Birth format.  Use YYYY-MM-DD");
            }
            LocalDate today = LocalDate.now();
            int age = today.getYear() - birthDate.getYear();
            if (today.getMonthValue() < birthDate.getMonthValue() ||
                    (today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }
            if (age < 18) {
                throw new AgeValidationException("User must be at least 18 years old.");
            }
        }
    }
 
    private void validateAgeOnUpdate(String dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(dateOfBirth, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Invalid Date of Birth format.  Use YYYY-MM-DD");
            }
            LocalDate today = LocalDate.now();
            int age = today.getYear() - birthDate.getYear();
            if (today.getMonthValue() < birthDate.getMonthValue() ||
                    (today.getMonthValue() == birthDate.getMonthValue() && today.getDayOfMonth() < birthDate.getDayOfMonth())) {
                age--;
            }
            if (age < 18) {
                throw new AgeValidationException("User must be at least 18 years old.");
            }
        }
    }
 
 
    //validate the photo size that the size of the photo should be in between 10KB to 20 KB
    private void validatePhotoSize(byte[] photo) {
        if (photo != null) {
            int photoSize = photo.length / 1024;
            if (photoSize < 10 || photoSize > 20) {
                throw new PhotoSizeValidationException("Photo size must be between 10KB and 20KB.");
            }
        }
    }
 
    private void validatePhotoSizeOnUpdate(byte[] photo) {
        if (photo == null || photo.length == 0) {
            throw new InvalidInputException("Photo is needed");
        }
 
        if (photo != null && photo.length > 0) {
            int photoSize = photo.length / 1024;
            if (photoSize < 10 || photoSize > 20) {
                throw new PhotoSizeValidationException("Photo size must be between 10KB and 20KB.");
            }
        }
    }
 
    private void checkPostalCodeAndDateOfBirthOnUpdate(String postalCode, String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isBlank() || postalCode == null || postalCode.isBlank()) {
            throw new InvalidInputException("Date of birth and Postal code are required");
        }
    }
}