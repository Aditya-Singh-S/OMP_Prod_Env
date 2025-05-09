package com.cts.mapper;
 
import org.springframework.stereotype.Component;
 
import com.cts.dto.RequestDTO;

import com.cts.dto.ResponseDTO;

import com.cts.entity.User;
 
@Component

public class UserMapper {
 
    // Converts RequestDTO to User Entity

    public User toEntity(RequestDTO requestDTO) {

        if (requestDTO == null) {

            return null;

        }
 
        // Dynamically constructing the address

        String address = String.format("%s, %s, %s",

                requestDTO.getAddressLine1(),

                requestDTO.getAddressLine2(),

                requestDTO.getPostalCode());
 
        return User.builder()

                .firstName(requestDTO.getFirstName())

                .lastName(requestDTO.getLastName())

                .email(requestDTO.getEmail())

                .password(requestDTO.getPassword())

                .nickName(requestDTO.getNickName())

                .addressLine1(requestDTO.getAddressLine1())

                .addressLine2(requestDTO.getAddressLine2())

                .postalCode(requestDTO.getPostalCode())

                .address(address)

                .contactNumber(requestDTO.getContactNumber())

                .dateOfBirth(requestDTO.getDateOfBirth())

                .photo(requestDTO.getPhoto())

                .build();

    }
 
    // Converts User Entity to ResponseDTO

    public ResponseDTO toDTO(User user) {

        if (user == null) {

            return null;

        }
 
        return ResponseDTO.builder()

                .userID(user.getUserID())

                .firstName(user.getFirstName())

                .lastName(user.getLastName())

                .email(user.getEmail())

                .nickName(user.getNickName())

                .address(user.getAddress()) 
                
                .addressLine1(user.getAddressLine1())
                
                .addressLine2(user.getAddressLine2())
                
                .postalCode(user.getPostalCode())

                .photo(user.getPhoto()) 

                .contactNumber(user.getContactNumber())

                .dateOfBirth(user.getDateOfBirth())

                .userRole(user.getUserRole())

                .emailVerification(user.isEmailVerification())

                .isActive(user.isActive())

                .createdOn(user.getAddedOn())

                .updatedOn(user.getUpdatedOn())

                .build();

    }

}

 