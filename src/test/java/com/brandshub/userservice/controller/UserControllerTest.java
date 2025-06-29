package com.brandshub.userservice.controller;

import com.brandshub.userservice.dto.LoginRequest;
import com.brandshub.userservice.dto.LoginResponse;
import com.brandshub.userservice.dto.UserRegistrationRequest;
import com.brandshub.userservice.dto.UserResponse;
import com.brandshub.userservice.entity.User;
import com.brandshub.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController using MockMvc.
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse testUserResponse;
    private UserRegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // Create test user response
        testUserResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .fullName("Test User")
                .companyName("Test Company")
                .phoneNumber("1234567890")
                .userType(User.UserType.EXTERNAL)
                .status(User.UserStatus.ACTIVE)
                .roles(new HashSet<>(Arrays.asList("USER")))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Create registration request
        registrationRequest = UserRegistrationRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .companyName("New Company")
                .phoneNumber("0987654321")
                .userType(User.UserType.EXTERNAL)
                .build();

        // Create login request
        loginRequest = LoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();

        // Create login response
        loginResponse = LoginResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(testUserResponse)
                .build();
    }

    @Test
    void registerUser_Success() throws Exception {
        // Given
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registerUser_ValidationError() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void login_ValidationError() throws Exception {
        // Given
        LoginRequest invalidRequest = LoginRequest.builder()
                .usernameOrEmail("") // Invalid: empty
                .password("") // Invalid: empty
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_Success() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUserResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(UserRegistrationRequest.class))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Success() throws Exception {
        // Given
        // No need to mock void method

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsersByName_Success() throws Exception {
        // Given
        when(userService.searchUsersByName("test")).thenReturn(Arrays.asList(testUserResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/users/search/name")
                .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchUsersByCompany_Success() throws Exception {
        // Given
        when(userService.searchUsersByCompany("Test Company")).thenReturn(Arrays.asList(testUserResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/users/search/company")
                .param("company", "Test Company"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Test Company"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_Success() throws Exception {
        // Given
        when(userService.updateUserStatus(eq(1L), eq(User.UserStatus.INACTIVE))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/users/1/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addRoleToUser_Success() throws Exception {
        // Given
        when(userService.addRoleToUser(eq(1L), eq("ADMIN"))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users/1/roles")
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeRoleFromUser_Success() throws Exception {
        // Given
        when(userService.removeRoleFromUser(eq(1L), eq("USER"))).thenReturn(testUserResponse);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1/roles")
                .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void accessProtectedEndpoint_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void accessAdminEndpoint_WithoutAdminRole_ReturnsForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }
} 