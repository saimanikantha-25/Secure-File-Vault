# Project Setup Instructions

This document guides you through setting up the Secure File Vault development environment.

## Prerequisites

Ensure you have the following installed on your system:

1. **Java Development Kit (JDK) 21**: Confirm version using:
   ```bash
   java -version
   ```
2. **MySQL Database Server 8.x**: Run local server on port `3306`.
3. **IDE (Optional)**: IntelliJ IDEA (recommended) or VS Code.

## Local Configuration

1. **Environment Setup**:
   Copy the `.env.example` file in the root directory and rename it to `.env`:
   ```bash
   cp .env.example .env
   ```
   *Note: `.env` is ignored by git to protect credentials.*

2. **Configure Database**:
   Log in to MySQL and run the initialization script to create the database:
   ```sql
   SOURCE database/schema/create_database.sql;
   ```

3. **Backend Environment Variables Configuration**:
   The backend reads connection and security parameters from environment variables. Ensure these variables are exported in your terminal before building/running, or defined in your IDE configuration:
   - On Windows (PowerShell):
     ```powershell
     $env:DB_HOST="localhost"
     $env:DB_PORT="3306"
     $env:DB_NAME="secure_file_vault"
     $env:DB_USERNAME="root"
     $env:DB_PASSWORD="your_mysql_password"
     $env:JWT_SECRET="my_super_secret_key_of_at_least_32_characters_long_for_hmac_256"
     $env:JWT_EXPIRATION="3600000"
     $env:JWT_ISSUER="SecureFileVault"
     ```
   - On Linux/macOS:
     ```bash
     export DB_HOST="localhost"
     export DB_PORT="3306"
     export DB_NAME="secure_file_vault"
     export DB_USERNAME="root"
     export DB_PASSWORD="your_mysql_password"
     export JWT_SECRET="my_super_secret_key_of_at_least_32_characters_long_for_hmac_256"
     export JWT_EXPIRATION="3600000"
     export JWT_ISSUER="SecureFileVault"
     ```

## Running the Application

1. Open a terminal and navigate to the `backend/` folder.
2. Build the project using the Maven wrapper:
   - On Windows (PowerShell):
     ```powershell
     .\mvnw clean compile
     ```
   - On Linux/macOS:
     ```bash
     chmod +x mvnw
     ./mvnw clean compile
     ```
3. Run the Spring Boot application:
   ```bash
   .\mvnw spring-boot:run
   ```
   *Note: Flyway migrations (e.g. system health schemas and users table schemas) execute automatically at startup, creating system schemas.*
4. Confirm health check is green by visiting:
   `http://localhost:8080/actuator/health`

## API Documentation (Endpoints Example)

### User Registration API
- **Endpoint**: `POST /api/v1/users/register`
- **Content-Type**: `application/json`

**Sample Request Payload**:
```json
{
  "username": "validuser",
  "email": "validuser@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Sample Success Response (201 Created)**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "validuser",
    "email": "validuser@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "createdAt": "2026-07-16T16:15:47Z"
  },
  "timestamp": "2026-07-16T16:15:47.123Z",
  "statusCode": 201
}
```

**Sample Validation Failure Response (400 Bad Request)**:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "password": ["Password must be at least 8 characters long"],
    "email": ["Invalid email format"]
  },
  "timestamp": "2026-07-16T16:15:48.456Z",
  "statusCode": 400
}
```

**Sample Duplicate Conflict Response (409 Conflict)**:
```json
{
  "success": false,
  "message": "Username 'validuser' is already registered.",
  "data": null,
  "timestamp": "2026-07-16T16:15:49.789Z",
  "statusCode": 409
}
```

### User Authentication API
- **Endpoint**: `POST /api/v1/auth/login`
- **Content-Type**: `application/json`

**Sample Request Payload**:
```json
{
  "loginIdentifier": "validuser",
  "password": "Password123!"
}
```

**Sample Success Response (200 OK)**:
```json
{
  "success": true,
  "message": "Authentication successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWxpZHVzZXIiLCJyb2xlIjoiVVNFUiIsImlzcyI6IlNlY3VyZUZpbGVWYXVsdCIsImlhdCI6MTc4MzI2MDAwMCwiZXhwIjoxNzgzMjYzNjAwfQ.signature",
    "type": "Bearer",
    "expiresInMs": 3600000,
    "username": "validuser",
    "role": "USER"
  },
  "timestamp": "2026-07-29T11:15:47.123Z",
  "statusCode": 200
}
```

**Sample Invalid Credentials Response (401 Unauthorized)**:
```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null,
  "timestamp": "2026-07-29T11:15:48.456Z",
  "statusCode": 401
}
```


