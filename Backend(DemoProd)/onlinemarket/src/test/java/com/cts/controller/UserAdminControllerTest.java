package com.cts.controller;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
 
import com.cts.dto.UserAdminDTO;
import com.cts.dto.UserDetailDTO;
import com.cts.entity.User;
import com.cts.service.UserAdminService;
 
@ExtendWith(MockitoExtension.class)
public class UserAdminControllerTest {
 
    @Mock
    private UserAdminService userAdminService;
 
    @Mock
    private MultipartFile imageFile;
 
    @InjectMocks
    private UserAdminController userAdminController;
 
    private User testUser;
 
    @BeforeEach
    void setUp() throws IOException, ParseException {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setContactNumber("1234567890");
 
        // Using lenient() to prevent strict stubbing problems
        Mockito.lenient().when(userAdminService.updateUserByEmail(anyBoolean(), anyString()))
               .thenReturn(testUser);
    }
 
    /**
     * Test case for createUser API
     */
    @Test
    void testCreateUser() throws IOException, ParseException {
        UserAdminDTO userAdminDTO = new UserAdminDTO();
        userAdminDTO.setFirstName("John");
        userAdminDTO.setLastName("Doe");
        userAdminDTO.setEmail("john.doe@example.com");
        userAdminDTO.setContactNumber("1234567890");
 
        when(userAdminService.createUser(any(UserAdminDTO.class), any(MultipartFile.class))).thenReturn(testUser);
 
        ResponseEntity<User> response = userAdminController.createUser(
                "Bearer token", "John", "Doe", "john.doe@example.com",
                "JD", "Address Line 1", "Address Line 2", "123456",
                "9876543210", "1990-01-01", true, imageFile);
 
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
    }
 
    /**
     * Test case for getAllUsers API
     */
    @Test
    void testGetAllUsersForAdmin() {
        List<User> userList = Arrays.asList(testUser);
        List<UserDetailDTO> userDetailDTOList = Arrays.asList(new UserDetailDTO( "John", "Doe", "john.doe@example.com", "1990-01-01", 
        		"9876543210", null, null, "Address Line 1", "Address Line 2",
        		"123456", true, "ADMIN"));
 
        when(userAdminService.getAllUsers()).thenReturn(userList);
 
        ResponseEntity<List<UserDetailDTO>> response = userAdminController.getAllUsersForAdmin("Bearer token");
 
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("John", response.getBody().get(0).getFirstName());
    }
 
    /**
     * Test case for updateUser API
     */
    @Test
    void testUpdateUser() throws IOException, ParseException {
        doReturn(testUser).when(userAdminService).updateUserByEmail(anyBoolean(), anyString());
 
        ResponseEntity<User> response = userAdminController.updateUser("Bearer token", "john.doe@example.com", true);
 
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
    }
}
 