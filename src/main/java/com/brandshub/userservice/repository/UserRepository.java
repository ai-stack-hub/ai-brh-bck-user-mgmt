package com.brandshub.userservice.repository;

import com.brandshub.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * <p>Provides data access methods for user management including:
 * <ul>
 *   <li>Basic CRUD operations</li>
 *   <li>Custom queries for user search and filtering</li>
 *   <li>User authentication and validation</li>
 * </ul>
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their unique email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given username.
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user exists with the given email address.
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by their user type (INTERNAL or EXTERNAL).
     * 
     * @param userType the type of users to find
     * @return list of users with the specified type
     */
    List<User> findByUserType(User.UserType userType);

    /**
     * Find users by their status (ACTIVE, INACTIVE, SUSPENDED, PENDING).
     * 
     * @param status the status of users to find
     * @return list of users with the specified status
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * Find users by company name (case-insensitive search).
     * 
     * @param companyName the company name to search for
     * @return list of users from the specified company
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    List<User> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);

    /**
     * Find users by name (first name or last name) using case-insensitive search.
     * 
     * @param name the name to search for
     * @return list of users matching the name criteria
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find active users by user type.
     * 
     * @param userType the type of users to find
     * @return list of active users with the specified type
     */
    List<User> findByUserTypeAndStatus(User.UserType userType, User.UserStatus status);

    /**
     * Count users by user type.
     * 
     * @param userType the type of users to count
     * @return number of users with the specified type
     */
    long countByUserType(User.UserType userType);

    /**
     * Count users by status.
     * 
     * @param status the status of users to count
     * @return number of users with the specified status
     */
    long countByStatus(User.UserStatus status);

    /**
     * Find users created within a specific date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of users created within the date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate ORDER BY u.createdAt DESC")
    List<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate);
} 