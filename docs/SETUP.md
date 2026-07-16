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
   Log in to MySQL and create the database specified in `.env`:
   ```sql
   CREATE DATABASE secure_file_vault;
   ```

3. **Backend Configuration**:
   The backend reads credentials from properties matched to environment variables. In later phases, we will bind application properties to environment configurations.

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
4. Confirm health check is green by visiting:
   `http://localhost:8080/actuator/health`
