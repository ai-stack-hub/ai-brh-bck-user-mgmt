package com.brandshub.userservice.service;

import com.brandshub.userservice.dto.LoginRequest;
import com.brandshub.userservice.dto.LoginResponse;
import com.brandshub.userservice.dto.UserRegistrationRequest;
import com.brandshub.userservice.dto.UserResponse;
import com.brandshub.userservice.entity.User;

import java.util.List;

/**
 * Service interface for user management operations.
 * 
 * <p>Defines business logic methods for user registration, authentication,
 * profile management, and user administration.</p>
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface UserService {

    /**
     * Register a new user in the system.
     * 
     * @param request the user registration request
     * @return the created user response
     * @throws RuntimeException if username or email already exists
     */
    UserResponse registerUser(UserRegistrationRequest request);

    /**
     * Authenticate a user and generate JWT token.
     * 
     * @param request the login request
     * @return the login response with JWT token
     * @throws RuntimeException if authentication fails
     */
    LoginResponse login(LoginRequest request);

    /**
     * Get user by ID.
     * 
     * @param userId the user ID
     * @return the user response
     * @throws RuntimeException if user not found
     */
    UserResponse getUserById(Long userId);

    /**
     * Get user by username.
     * 
     * @param username the username
     * @return the user response
     * @throws RuntimeException if user not found
     */
    UserResponse getUserByUsername(String username);

    /**
     * Get user by email.
     * 
     * @param email the email address
     * @return the user response
     * @throws RuntimeException if user not found
     */
    UserResponse getUserByEmail(String email);

    /**
     * Update user profile information.
     * 
     * @param userId the user ID
     * @param request the user update request
     * @return the updated user response
     * @throws RuntimeException if user not found
     */
    UserResponse updateUser(Long userId, UserRegistrationRequest request);

    /**
     * Delete a user from the system.
     * 
     * @param userId the user ID
     * @throws RuntimeException if user not found
     */
    void deleteUser(Long userId);

    /**
     * Get all users with optional filtering.
     * 
     * @return list of all users
     */
    List<UserResponse> getAllUsers();

    /**
     * Get users by user type.
     * 
     * @param userType the user type
     * @return list of users with the specified type
     */
    List<UserResponse> getUsersByType(User.UserType userType);

    /**
     * Get users by status.
     * 
     * @param status the user status
     * @return list of users with the specified status
     */
    List<UserResponse> getUsersByStatus(User.UserStatus status);

    /**
     * Search users by name (first name or last name).
     * 
     * @param name the name to search for
     * @return list of users matching the name criteria
     */
    List<UserResponse> searchUsersByName(String name);

    /**
     * Search users by company name.
     * 
     * @param companyName the company name to search for
     * @return list of users from the specified company
     */
    List<UserResponse> searchUsersByCompany(String companyName);

    /**
     * Update user status.
     * 
     * @param userId the user ID
     * @param status the new status
     * @return the updated user response
     * @throws RuntimeException if user not found
     */
    UserResponse updateUserStatus(Long userId, User.UserStatus status);

    /**
     * Add role to user.
     * 
     * @param userId the user ID
     * @param role the role to add
     * @return the updated user response
     * @throws RuntimeException if user not found
     */
    UserResponse addRoleToUser(Long userId, String role);

    /**
     * Remove role from user.
     * 
     * @param userId the user ID
     * @param role the role to remove
     * @return the updated user response
     * @throws RuntimeException if user not found
     */
    UserResponse removeRoleFromUser(Long userId, String role);

    /**
     * Update user's last login timestamp.
     * 
     * @param userId the user ID
     * @throws RuntimeException if user not found
     */
    void updateLastLogin(Long userId);
} 