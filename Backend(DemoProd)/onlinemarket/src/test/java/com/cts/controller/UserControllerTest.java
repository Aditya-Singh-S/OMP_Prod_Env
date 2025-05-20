package com.cts.controller;

import com.cts.dto.ProductViewDTO;
import com.cts.dto.RequestDTO;
import com.cts.dto.ResponseDTO;
import com.cts.dto.SignInRequest;
import com.cts.dto.SignInResponse;
import com.cts.entity.User;
import com.cts.exception.PasswordsMismatchException;
import com.cts.exception.UserNotFoundException;
import com.cts.service.ProductService;
import com.cts.service.SNSService;
import com.cts.service.UserService;
import com.cts.service.UserValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private SNSService snsService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(userController).build(); // Added setControllerAdvice
    }

    @Test
    void createUser_ValidInput_ReturnsOk() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.png", "image/png", "some image".getBytes());
        ResponseDTO mockResponse = new ResponseDTO();
        when(userService.createUser(any(RequestDTO.class), eq(imageFile))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/OMP/register")
                        .file(imageFile)
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password")
                        .param("contactNumber", "1234567890")
                        .param("dateOfBirth", "2000-01-01"))
                .andExpect(status().isOk());
    }




    @Test
    void createUser_MissingRequiredField_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/OMP/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password")) // Missing contactNumber and dateOfBirth
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUser_ReturnsListOfUsers() throws Exception {
        List<ResponseDTO> mockUsers = Arrays.asList(new ResponseDTO(), new ResponseDTO());
        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/getAllUser"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserIdByEmail_ValidEmail_ReturnsUserId() throws Exception {
        int mockUserId = 123;
        String email = "test@example.com";
        when(userService.getUserIdByEmail(email)).thenReturn(mockUserId);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/getUserIdByEmail")
                        .param("emailId", email))
                .andExpect(status().isOk());
    }

    @Test
    void getUserEmailById_ValidId_ReturnsEmail() throws Exception {
        String mockEmail = "test@example.com";
        int userId = 123;
        when(userService.getUserEmailById(userId)).thenReturn(mockEmail);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/getUserEmailById")
                        .param("id", String.valueOf(userId)))
                .andExpect(status().isOk());
    }

    @Test
    void getImage_ValidUserId_ReturnsImage() throws Exception {
        int userId = 123;
        byte[] mockImage = "some image data".getBytes();
        when(userService.getUserImage(userId)).thenReturn(mockImage);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/user/image/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void getUserDetailsById_ValidUserId_ReturnsUserDetails() throws Exception {
        int userId = 123;
        ResponseDTO mockUser = new ResponseDTO();
        when(userService.getUserDetailsById(userId)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/myDetails")
                        .header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserDetailsById_MissingUserId_ReturnsNotFound() throws Exception {
        when(userService.getUserDetailsById(anyInt())).thenThrow(new UserNotFoundException("User not found with ID: 123"));

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/myDetails")
                        .header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                        .param("userId", "123"))
                .andExpect(status().isNotFound());
    }





    @Test
    void loginUser_ValidCredentials_ReturnsOkWithResponse() throws Exception {
        SignInRequest signInRequest = new SignInRequest("test@example.com", "password");
        SignInResponse mockResponse = new SignInResponse("Login Successfull", true, "test@example.com");
        when(userService.validateUser(any(SignInRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsNotFound() throws Exception {
        SignInRequest signInRequest = new SignInRequest("test@example.com", "wrongpassword");
        when(userService.validateUser(any(SignInRequest.class))).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void verifyEmail_ValidEmail_ReturnsVerificationMessage() throws Exception {
        String email = "test@example.com";
        String mockMessage = "Email verified successfully! Account is now active.";
        when(userService.verifyEmail(email)).thenReturn(mockMessage);

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/verify-email")
                        .param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    void handleMissingParams_MissingEmailParam_ReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/verify-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_ValidInput_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/reset-password")
                        .param("email", "test@example.com")
                        .param("newPassword", "newpassword")
                        .param("confirmPassword", "newpassword"))
                .andExpect(status().isOk());

    }

    @Test
    void resetPassword_UserNotFound_ReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException("User not found!")).when(userService).resetPassword("nonexistent@example.com", "newpassword", "newpassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/reset-password")
                        .param("email", "nonexistent@example.com")
                        .param("newPassword", "newpassword")
                        .param("confirmPassword", "newpassword"))
                .andExpect(status().isNotFound());
    }

    @Test
    void resetPassword_PasswordsMismatch_ReturnsBadRequest() throws Exception {
        doThrow(new PasswordsMismatchException("Password does not match!")).when(userService).resetPassword("test@example.com", "newpassword", "differentpassword");


        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/reset-password")
                        .param("email", "test@example.com")
                        .param("newPassword", "newpassword")
                        .param("confirmPassword", "differentpassword"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateResetLink_ValidEmail_ReturnsResetLink() throws Exception {
        String email = "test@example.com";
        String mockLink = "http://127.0.0.1:3000/reset-page?email=test@example.com";
        when(userService.generateResetLink(email)).thenReturn(mockLink);

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/generate-reset-link")
                        .param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    void generateResetLink_UserNotFound_ReturnsNotFound() throws Exception {
        String email = "nonexistent@example.com";
        when(userService.generateResetLink(email)).thenReturn("User not found");

        mockMvc.perform(MockMvcRequestBuilders.post("/OMP/generate-reset-link")
                        .param("email", email))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductSubscriptionList_ValidUserIdAndAuth_ReturnsList() throws Exception {
        int userId = 123;
        List<ProductViewDTO> mockList = Arrays.asList(new ProductViewDTO(), new ProductViewDTO());
        when(productService.getProductSubscriptionList(userId)).thenReturn(mockList);

        mockMvc.perform(MockMvcRequestBuilders.get("/OMP/getProductSubscriptionList")
                        .header("Authorization", "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk());
    }
}