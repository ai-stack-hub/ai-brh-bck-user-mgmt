# Brands Hub - User Service

## Overview
User Service is a microservice for managing internal and external users in the Brands Hub B2B portal. It provides REST APIs for user registration, authentication, and profile management, and is designed for deployment on Azure Kubernetes Service (AKS).

## Features
- User registration and login (JWT authentication)
- Azure SQL integration for user data
- RESTful APIs
- Swagger/OpenAPI documentation
- Javadoc and code comments
- Unit and integration tests
- Dockerized for AKS
- GitHub Actions CI/CD with SonarQube and ACR integration

## Tech Stack
- Java 21
- Spring Boot 3.x
- Azure SQL (JPA/Hibernate)
- JWT
- Swagger/OpenAPI
- JUnit5, Mockito, Rest-Assured
- Docker
- GitHub Actions, SonarQube, Azure Container Registry (ACR), AKS

## Setup
1. Clone the repository
2. Configure Azure SQL connection in `application.yml`
3. Build the project: `./mvnw clean install`

## Running Locally
```
./mvnw spring-boot:run
```

## API Documentation
Swagger UI available at `/swagger-ui.html` after running the service.

## Testing
- Unit tests: `./mvnw test`
- Integration tests: `./mvnw verify`

## Docker
Build Docker image:
```
docker build -t user-service:latest .
```

## CI/CD
- GitHub Actions pipeline for build, test, SonarQube analysis, push to ACR, and deploy to AKS.

## Contact
For questions, contact the Brands Hub team. 