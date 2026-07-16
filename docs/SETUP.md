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
   The backend reads connection parameters from environment variables. Ensure these variables are exported in your terminal before building/running, or defined in your IDE configuration:
   - On Windows (PowerShell):
     ```powershell
     $env:DB_HOST="localhost"
     $env:DB_PORT="3306"
     $env:DB_NAME="secure_file_vault"
     $env:DB_USERNAME="root"
     $env:DB_PASSWORD="your_mysql_password"
     ```
   - On Linux/macOS:
     ```bash
     export DB_HOST="localhost"
     export DB_PORT="3306"
     export DB_NAME="secure_file_vault"
     export DB_USERNAME="root"
     export DB_PASSWORD="your_mysql_password"
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
