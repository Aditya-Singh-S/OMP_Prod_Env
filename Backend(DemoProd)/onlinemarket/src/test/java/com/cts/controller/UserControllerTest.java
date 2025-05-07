package com.cts.controller;

import com.cts.dto.SignInRequest;
import com.cts.dto.SignInResponse;
import com.cts.dto.RequestDTO;
import com.cts.dto.ResponseDTO;
import com.cts.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginUser_validRequest_shouldReturnOkResponse()
    {
        SignInRequest signInRequest = new SignInRequest("john.doe@example.com", "password123");
        SignInResponse signInResponse = new SignInResponse("Login successful", true,"john.doe@example.com");

        when(userService.validateUser(signInRequest)).thenReturn(signInResponse);

        ResponseEntity<SignInResponse> responseEntity = userController.loginUser(signInRequest);

        assertEquals(ResponseEntity.ok(signInResponse), responseEntity);
    }
}