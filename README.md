# MT-26: JWT Authentication & Budget Management API

## Overview

MT-26 is a comprehensive RESTful API application built with **Spring Boot 3.2.5** that combines robust JWT-based authentication with a full-featured budget and transaction management system. This enterprise-grade application provides secure user authentication, role-based access control, and complete financial tracking capabilities including budget planning, category management, and transaction recording.

---

## Key Features

### Security & Authentication
- **JWT (JSON Web Token)** based authentication
- Role-based access control (RBAC)
- Secure password management with Spring Security
- Custom user details service with database integration
- Spring Security filter chain configuration

### Budget Management
- Create and manage budgets with spending limits
- Track budget utilization and remaining balance
- Budget categorization for better financial organization
- Real-time budget monitoring and alerts

### Transaction Management
- Record and track all financial transactions
- Associate transactions with budgets and categories
- Transaction history and detailed reporting
- Multi-category transaction support

### Category Management
- Flexible category creation and management
- Categorize budgets and transactions
- Hierarchical category organization
- Easy category assignment and modification

### Protected Resources
- Dedicated controller for protected endpoints
- Token-based access validation
- User-specific resource access

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.2.5 |
| **Java Version** | OpenJDK | 17 |
| **Database** | MySQL | 8.0+ |
| **Security** | Spring Security + JWT | Latest |
| **ORM** | Spring Data JPA | Hibernate |
| **Build Tool** | Maven | 3.8+ |
| **Connector** | MySQL Connector/J | Latest |

---

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher
- **MySQL Server**: Version 8.0 or higher
- **Apache Maven**: Version 3.8.0 or higher
- **Git**: For version control

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd MT-26/bk-ed
```

### 2. Database Configuration

Create a MySQL database:

```sql
CREATE DATABASE jwt_db;
USE jwt_db;
```

### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_db
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.jpa.hibernate.ddl-auto=update
```

### 4. JWT Configuration

The JWT configuration can be customized in `application.properties`:

```properties
jwt.secret=<your-secret-key>
jwt.expiration=86400000  # Token expiration in milliseconds (24 hours default)
```

### 5. Build the Application

```bash
mvn clean install
```

### 6. Run the Application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

---

## Project Structure

```
bk-ed/
├── src/main/java/bk_ed/spbt/MT_26/
│   ├── Mt26Application.java              # Main Spring Boot application class
│   ├── config/
│   │   └── SecurityConfig.java           # Spring Security configuration
│   ├── controller/
│   │   ├── AuthController.java           # Authentication endpoints
│   │   ├── BudgetController.java         # Budget management endpoints
│   │   ├── CategoryController.java       # Category management endpoints
│   │   ├── TransactionController.java    # Transaction management endpoints
│   │   └── ProtectedController.java      # Protected resource endpoints
│   ├── dto/
│   │   ├── AuthRequest.java              # Authentication request DTO
│   │   └── AuthResponse.java             # Authentication response DTO
│   ├── entity/
│   │   ├── AppUser.java                  # User entity with roles
│   │   ├── Budget.java                   # Budget entity
│   │   ├── Category.java                 # Category entity
│   │   ├── Role.java                     # User role entity
│   │   └── Transaction.java              # Transaction entity
│   ├── repository/
│   │   ├── UserRepository.java           # User data access layer
│   │   ├── BudgetRepository.java         # Budget data access layer
│   │   ├── CategoryRepository.java       # Category data access layer
│   │   └── TransactionRepository.java    # Transaction data access layer
│   ├── security/
│   │   ├── JwtService.java               # JWT token generation & validation
│   │   ├── JwtAuthenticationFilter.java  # JWT filter for request interception
│   │   └── CustomUserDetailsService.java # Custom user details loading
│   └── service/
│       ├── BudgetService.java            # Budget business logic
│       ├── CategoryService.java          # Category business logic
│       └── TransactionService.java       # Transaction business logic
├── src/main/resources/
│   └── application.properties            # Application configuration
├── pom.xml                               # Maven project configuration
└── target/                               # Compiled output
```

---

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |

### Budget Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/budgets` | Retrieve all user budgets |
| POST | `/budgets` | Create a new budget |
| GET | `/budgets/{id}` | Get budget details |
| PUT | `/budgets/{id}` | Update budget |
| DELETE | `/budgets/{id}` | Delete budget |

### Category Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | Retrieve all categories |
| POST | `/categories` | Create new category |
| PUT | `/categories/{id}` | Update category |
| DELETE | `/categories/{id}` | Delete category |

### Transaction Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/transactions` | Retrieve all transactions |
| POST | `/transactions` | Create new transaction |
| GET | `/transactions/{id}` | Get transaction details |
| PUT | `/transactions/{id}` | Update transaction |
| DELETE | `/transactions/{id}` | Delete transaction |

### Protected Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/protected/user-info` | Retrieve current user information |
| POST | `/protected/verify-token` | Verify JWT token validity |

**Note**: All endpoints except `/auth/register` and `/auth/login` require a valid JWT token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

---

## Testing

### Using Postman

A Postman collection is provided for API testing:

1. **Import the collection**: Open Postman and import `MT26-Postman-Collection.json` or `JWT_Auth_API.postman_collection.json`
2. **Configure environment variables**: Set `base_url` to `http://localhost:8080`
3. **Authenticate**: Execute the login endpoint to obtain a JWT token
4. **Set token**: Use the token in subsequent requests via the `Authorization` header

### Sample Authentication Flow

```bash
# 1. Register a new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'

# 2. Login and get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password123"}'

# 3. Use token for protected endpoints
curl -X GET http://localhost:8080/protected/user-info \
  -H "Authorization: Bearer <your-jwt-token>"
```

---

## Configuration Details

### JWT Configuration

- **Secret Key**: Configured in `application.properties` - keep this secure and use environment variables in production
- **Token Expiration**: Default 24 hours (86400000 milliseconds)
- **Algorithm**: HS256 (HMAC with SHA-256)

### Database Configuration

- **Database**: MySQL 8.0+
- **Dialect**: MySQL8Dialect
- **DDL Auto**: Update (creates/updates tables automatically)
- **SQL Logging**: Enabled for development (disable in production for performance)

---

## Build & Deployment

### Build JAR File

```bash
mvn clean package
```

### Run JAR File

```bash
java -jar target/MT-26-0.0.1-SNAPSHOT.jar
```

### Environment-Specific Profiles

Create profile-specific properties files:
- `application-dev.properties`
- `application-prod.properties`

Run with specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## Python Version

A Python implementation of this application is also available in the `py-version/` directory. Refer to that directory for Python-specific setup and execution instructions.

---

## Security Best Practices

1. **JWT Secret**: Use a strong, randomly generated secret key (minimum 256 bits)
2. **Environment Variables**: Store sensitive data (database credentials, JWT secret) in environment variables, not in code
3. **HTTPS**: Use HTTPS in production to protect JWT tokens in transit
4. **Token Expiration**: Set appropriate token expiration times based on your security requirements
5. **Password Management**: Ensure passwords are hashed and salted before storage
6. **CORS**: Configure CORS appropriately for your frontend domain

---

## Troubleshooting

### Database Connection Issues

- Verify MySQL server is running
- Check database credentials in `application.properties`
- Ensure the database `jwt_db` exists
- Verify firewall and port 3306 accessibility

### JWT Token Errors

- Ensure the token is included in the `Authorization` header with format: `Bearer <token>`
- Verify token has not expired
- Check JWT secret key matches between token generation and validation

### Build Failures

- Clear Maven cache: `mvn clean`
- Ensure Java 17+ is installed: `java -version`
- Update Maven dependencies: `mvn dependency:resolve`

---

## Contributing

Contributions are welcome! Please follow the standard Git workflow:

1. Create a feature branch: `git checkout -b feature/feature-name`
2. Commit your changes: `git commit -m "Add feature description"`
3. Push to the branch: `git push origin feature/feature-name`
4. Submit a pull request

---

## License

This project is provided as-is for educational and development purposes.

---

## Support & Documentation

For issues, questions, or suggestions, please refer to:
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security Documentation: https://spring.io/projects/spring-security
- JWT Documentation: https://jwt.io/

---

**Last Updated**: April 2026  
**Application Version**: 0.0.1-SNAPSHOT  
**Spring Boot Version**: 3.2.5
