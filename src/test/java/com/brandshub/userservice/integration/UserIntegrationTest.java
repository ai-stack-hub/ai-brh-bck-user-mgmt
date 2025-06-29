package com.brandshub.userservice.integration;

import com.brandshub.userservice.dto.LoginRequest;
import com.brandshub.userservice.dto.UserRegistrationRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for User Service using RestAssured and TestContainers.
 * 
 * @author Brands Hub Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class UserIntegrationTest {

    @Container
    static MSSQLServerContainer<?> sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
            .acceptLicense()
            .withPassword("TestPassword123!");

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlServer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlServer::getUsername);
        registry.add("spring.datasource.password", sqlServer::getPassword);
        registry.add("spring.datasource.driver-class-name", sqlServer::getDriverClassName);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    void testUserRegistrationAndLoginFlow() {
        // Create registration request
        UserRegistrationRequest registrationRequest = UserRegistrationRequest.builder()
                .username("integrationtest")
                .email("integration@test.com")
                .password("password123")
                .firstName("Integration")
                .lastName("Test")
                .companyName("Integration Test Company")
                .phoneNumber("1234567890")
                .userType(com.brandshub.userservice.entity.User.UserType.EXTERNAL)
                .build();

        // Register user
        String userId = given()
                .contentType(ContentType.JSON)
                .body(registrationRequest)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("username", equalTo("integrationtest"))
                .body("email", equalTo("integration@test.com"))
                .body("firstName", equalTo("Integration"))
                .body("lastName", equalTo("Test"))
                .body("companyName", equalTo("Integration Test Company"))
                .body("phoneNumber", equalTo("1234567890"))
                .body("userType", equalTo("EXTERNAL"))
                .body("status", equalTo("ACTIVE"))
                .body("roles", hasItem("USER"))
                .extract()
                .path("id");

        // Login with registered user
        LoginRequest loginRequest = LoginRequest.builder()
                .usernameOrEmail("integrationtest")
                .password("password123")
                .build();

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("tokenType", equalTo("Bearer"))
                .body("expiresIn", notNullValue())
                .body("user.id", equalTo(Integer.parseInt(userId)))
                .body("user.username", equalTo("integrationtest"))
                .extract()
                .path("token");

        // Get user profile using JWT token
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(userId)))
                .body("username", equalTo("integrationtest"))
                .body("email", equalTo("integration@test.com"));
    }

    @Test
    void testUserRegistrationValidation() {
        // Test with invalid email
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("invalid-email")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/users/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation Error"))
                .body("details.email", notNullValue());

        // Test with short password
        UserRegistrationRequest shortPasswordRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("123")
                .firstName("Test")
                .lastName("User")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(shortPasswordRequest)
                .when()
                .post("/users/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation Error"))
                .body("details.password", notNullValue());
    }

    @Test
    void testLoginValidation() {
        // Test with empty credentials
        LoginRequest emptyRequest = LoginRequest.builder()
                .usernameOrEmail("")
                .password("")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(emptyRequest)
                .when()
                .post("/users/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Validation Error"));
    }

    @Test
    void testProtectedEndpointsWithoutAuthentication() {
        // Try to access protected endpoint without authentication
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(401);
    }

    @Test
    void testSwaggerDocumentationAccess() {
        // Test Swagger UI access
        given()
                .when()
                .get("/swagger-ui.html")
                .then()
                .statusCode(200);

        // Test API docs access
        given()
                .when()
                .get("/api-docs")
                .then()
                .statusCode(200);
    }
} 