package com.brandshub.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Brands Hub User Service.
 * 
 * <p>This microservice handles user management operations including:
 * <ul>
 *   <li>User registration and authentication</li>
 *   <li>User profile management</li>
 *   <li>JWT token generation and validation</li>
 *   <li>Role-based access control</li>
 * </ul>
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing // Enable JPA auditing for created/updated timestamps
public class UserServiceApplication {

    /**
     * Main method to start the User Service application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
} 