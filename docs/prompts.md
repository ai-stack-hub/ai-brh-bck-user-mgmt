I want to build a B2B Brands Portal called Brands Hub to enable internal and external users to collaborate on document management using SharePoint integration via Microsoft Graph API.
Generate a complete, production-ready Spring Boot microservice called user-service using Java 21, with JWT-based authentication and authorization, Azure SQL database integration, and Swagger/OpenAPI documentation.

The service should support:
User registration, login, JWT authentication using MSAL,
RBAC for INTERNAL/EXTERNAL users, 
User profile management,
Search and filtering capabilities

Tech Stack:
Java 21, Spring Boot latest version, 
Azure SQL Database with JPA, Microsoft Graph API (for SharePoint integration), Spring Security with JWT, Swagger/OpenAPI documentation, Docker for containerization, GitHub Actions for CI/CD (separate CI & CD pipelines), SonarQube for static code analysis, AKS + ACR for deployment

Testing: JUnit 5, Mockito, Rest-Assured

Best Practices:
Follow RESTful API standards, Use Lombok, 
Apply validation annotations for input constraints,
Add inline comments for complex logic,
Include Javadoc and a clear README.md,
Use environment variables for all configuration,
Write comprehensive unit and integration tests,
Enable Swagger documentation on all endpoints,
Ensure the service is production-ready and deployable to AKS

