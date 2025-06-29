package com.brandshub.userservice.service;

import com.brandshub.userservice.dto.LoginRequest;
import com.brandshub.userservice.dto.LoginResponse;
import com.brandshub.userservice.dto.UserRegistrationRequest;
import com.brandshub.userservice.dto.UserResponse;
import com.brandshub.userservice.entity.User;
import com.brandshub.userservice.exception.AuthenticationException;
import com.brandshub.userservice.exception.DuplicateResourceException;
import com.brandshub.userservice.exception.UserNotFoundException;
import com.brandshub.userservice.repository.UserRepository;
import com.brandshub.userservice.security.JwtTokenProvider;
import com.brandshub.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService implementation.
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password(new BCryptPasswordEncoder().encode("password123"))
                .firstName("Test")
                .lastName("User")
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
    }

    @Test
    void registerUser_Success() {
        // Given
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.registerUser(registrationRequest);

        // Then
        assertNotNull(result);
        assertEquals(registrationRequest.getUsername(), result.getUsername());
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        assertEquals(registrationRequest.getFirstName(), result.getFirstName());
        assertEquals(registrationRequest.getLastName(), result.getLastName());
        verify(userRepository).existsByUsername(registrationRequest.getUsername());
        verify(userRepository).existsByEmail(registrationRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(registrationRequest));
        verify(userRepository).existsByUsername(registrationRequest.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(registrationRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(registrationRequest));
        verify(userRepository).existsByUsername(registrationRequest.getUsername());
        verify(userRepository).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Given
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(testUser.getUsername())).thenReturn("jwt-token");

        // When
        LoginResponse result = userService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("Bearer", result.getTokenType());
        assertNotNull(result.getUser());
        assertEquals(testUser.getUsername(), result.getUser().getUsername());
        verify(userRepository).findByUsername(loginRequest.getUsernameOrEmail());
        verify(jwtTokenProvider).generateToken(testUser.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(loginRequest.getUsernameOrEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
        verify(userRepository).findByUsername(loginRequest.getUsernameOrEmail());
        verify(userRepository).findByEmail(loginRequest.getUsernameOrEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        // Given
        when(userRepository.findByUsername(loginRequest.getUsernameOrEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(AuthenticationException.class, () -> userService.login(loginRequest));
        verify(userRepository).findByUsername(loginRequest.getUsernameOrEmail());
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponse result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getUsername(), result.get(0).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void updateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.updateUser(1L, registrationRequest);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, registrationRequest));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void updateUserStatus_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.updateUserStatus(1L, User.UserStatus.INACTIVE);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addRoleToUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.addRoleToUser(1L, "ADMIN");

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void removeRoleFromUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.removeRoleFromUser(1L, "USER");

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
} 