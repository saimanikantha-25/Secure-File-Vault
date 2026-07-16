# Secure File Vault

A highly secure, production-grade cloud storage and file management application built with Java 21, Spring Boot, MySQL, and modern security practices (AES encryption, JWT, Multi-Factor Authentication, Audit Logging).

## Project Overview

Secure File Vault is designed to provide secure, encrypted file storage for enterprises and individuals. Files are stored securely on the backend, encrypted at rest, and protected by authentication, authorization, rate limiting, and auditing.

## Features

- **Encrypted Storage**: Files are encrypted at-rest using AES-256 and secure key management.
- **Robust Authentication**: JWT-based stateless session management with Multi-Factor Authentication (Email OTP).
- **Granular Authorization**: Role-Based Access Control (RBAC) to manage administrative and standard operations.
- **Audit Logging**: Comprehensive trace of all administrative and user events (file upload, download, delete, auth failures).
- **Responsive Frontend**: Seamless and modern user interface to interact with files and view logs.
- **System Metrics & Monitoring**: Built-in monitoring via Spring Boot Actuator.

## Technology Stack

- **Backend Framework**: Spring Boot 3.5.x
- **Language**: Java 21 (LTS)
- **Build Tool**: Maven
- **Database**: MySQL 8
- **ORM & Persistence**: Spring Data JPA / Hibernate
- **Security**: Spring Security, JWT (JSON Web Tokens), BCrypt
- **Mailing**: Spring Boot Starter Mail (for OTP delivery)
- **Monitoring**: Spring Boot Actuator
- **API Documentation**: OpenAPI / Swagger

## Folder Structure

```text
Secure-File-Vault/
├── .gitignore                   # Root gitignore rules
├── README.md                    # Project documentation
├── LICENSE                      # MIT License
├── .env.example                 # Environment variables template
├── database/                    # Database configurations & scripts
│   ├── schema/                  # Initial SQL schemas
│   ├── migrations/              # Flyway/Liquibase or manual SQL migrations
│   ├── seed/                    # Development/testing seed scripts
│   └── backups/                 # Database backup files
├── docs/                        # Project architectural docs and logs
│   ├── ARCHITECTURE.md          # Structural details
│   ├── API.md                   # REST API documentation
│   ├── SETUP.md                 # Setup guide
│   ├── DEPLOYMENT.md            # Production deployment guide
│   └── AI_DEVELOPMENT_LOG.md    # Development learning journal
├── postman/                     # Postman workspace exports
│   ├── collections/             # API request collections
│   ├── environments/            # Local/Staging environments config
│   └── exports/                 # Other Postman-related configuration files
├── diagrams/                    # Architecture diagrams (UML, Database, Flowcharts)
├── screenshots/                 # Application screenshots
└── backend/                     # Spring Boot project folder
    ├── pom.xml                  # Maven dependency descriptor
    ├── mvnw / mvnw.cmd          # Maven wrapper execution scripts
    ├── .mvn/                    # Maven wrapper configurations
    └── src/
        ├── main/
        │   ├── java/            # Source code
        │   └── resources/       # Configuration properties & static assets
        └── test/                # Unit & integration tests
```

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 21 (Temurin OpenJDK 21 or equivalent)
- **Database**: MySQL 8.x
- **Build tool**: Maven (wrapper included in `backend/`)

### Setup Instructions

1. Clone the repository.
2. Duplicate `.env.example` in the root directory as `.env` and configure your credentials.
3. CD into the `backend/` folder.
4. Run standard Maven goals using the wrapper.

## Running the Project

### Running Backend Locally
Navigate to the `backend/` directory:
```bash
.\mvnw clean compile
.\mvnw spring-boot:run
```
Once started, the health check will be available at:
`http://localhost:8080/actuator/health`

## Project Roadmap

- **Phase 0: Environment Setup** - Install and verify basic system prerequisites (JDK 21, MySQL 8).
- **Phase 0.5: Project Initialization** - Base structure, documentation, basic properties configuration, and Actuator setup.
- **Phase 1: Backend Foundation** - Establish base packages, utility classes, and generic error handlers.
- **Phase 2: Database Layer** - Establish database connectivity, configure tables, connection pool, and migrations.
- **Phase 3: Authentication** - Implement basic authentication, password hashing, and user registration.
- **Phase 4: Spring Security** - Integrate Spring Security with JWT token issuance, verification, and expiration.
- **Phase 5: Secure File Storage** - Implement local file upload, validation (size, MIME type checks), and safe metadata storage.
- **Phase 6: Encryption** - Integrate AES-256 database-level and file-level encryption at-rest.
- **Phase 7: Dashboard APIs** - Build dashboards, file listings, shared folders, and search capabilities.
- **Phase 8: Email OTP** - Implement Multi-Factor Authentication with time-bound OTP verification emails.
- **Phase 9: Audit Logging** - Add an event-driven audit logging database to trace security events.
- **Phase 10: Swagger** - Set up Swagger/OpenAPI UI for interactive testing and documentation.
- **Phase 11: Testing** - Write unit, integration, and mock tests with JUnit 5 and Mockito.
- **Phase 12: Frontend** - Build a modern, rich web UI (using Tailwind or raw CSS/JS) for file actions.
- **Phase 13: Deployment** - Containerize the application using Docker, configure Docker Compose, and setup staging.
- **Phase 14: Production Ready** - Perform load testing, security audits, finalize backups, and deploy to cloud.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
