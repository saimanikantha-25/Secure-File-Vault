# Architecture Blueprint - Secure File Vault

This document outlines the architectural patterns and packages designed for the Secure File Vault application.

## Architectural Pattern

Secure File Vault follows a standard, modular Spring Boot layered architecture organized by responsibility. This structure separates cross-cutting concerns, presentation routes, business services, and database entities to maximize maintainability, scalability, and testability.

```text
       ┌────────────────────────────────────────────────────────┐
       │                  Presentation Layer                    │
       │  (Controller, DTO - Requests/Responses, Validation)     │
       └───────────────────────────┬────────────────────────────┘
                                   │
                                   ▼
       ┌────────────────────────────────────────────────────────┐
       │                    Service Layer                       │
       │  (Service Interfaces & Implementations, Mappers, DTOs)  │
       └───────────────────────────┬────────────────────────────┘
                                   │
                                   ▼
       ┌────────────────────────────────────────────────────────┐
       │                   Persistence Layer                    │
       │  (Repository Interfaces, Entities, Database Seeds)      │
       └────────────────────────────────────────────────────────┘
```

## Packages Layout

The Java package structure under `com.saimanikantha.securefilevault` is organized by responsibility:

- **`config`**: Holds configuration classes (e.g., SecurityConfig, MailConfig, WebConfig).
- **`constants`**: Global application constants, error codes, and message definitions.
- **`controller`**: REST APIs exposing endpoints to clients. Receives requests and delegates to the service layer.
- **`dto`**: Data Transfer Objects representing payloads. Divided into:
  - `dto/request`: Client request payloads.
  - `dto/response`: Server responses.
  - `dto/common`: Reusable response containers (e.g., paginated wrappers, generic status API).
- **`entity`**: JPA model representation mapping to the MySQL database tables.
- **`exception`**: Custom exceptions (e.g., FileStorageException, UserAlreadyExistsException) and the global REST exception handler.
- **`repository`**: Spring Data JPA repositories interfacing with the database.
- **`service`**: Core business services. Organized into interfaces (`service/interfaces`) and implementations (`service/impl`).
- **`security`**: JWT handlers, security filters, custom UserDetailsServices, password encoders, OTP services.
- **`util`**: Static helpers (e.g., EncryptionUtil, FileSystemUtil).
- **`mapper`**: Conversion tools to map entities to DTOs and vice-versa (e.g., MapStruct or manual wrappers).
- **`validation`**: Custom validation annotations and validators.

## Design Principles

1. **Separation of Concerns**: Each layer is strictly decoupled. Controllers only manage request parsing/validation and return DTO response envelopes. Services execute transaction-bound business steps. Persistence layers manage database interactions.
2. **Coding to Interfaces**: Services are defined as interfaces to decouple implementations and facilitate unit testing through mock dependencies.
3. **Stateless Operations**: The REST controllers are stateless. Client state is managed via secure, cryptographically-signed JWT tokens.

## Persistence and Database Layer

Secure File Vault integrates MySQL 8 as its primary relational database. Data persistence operations are designed with safety, traceablity, and strict configuration governance in mind:

1. **JPA & Spring Data JPA**: The persistence layer uses Spring Data JPA built on top of Hibernate. For security and database integrity, Hibernate's automatic schema alterations are disabled:
   - `spring.jpa.hibernate.ddl-auto` is configured strictly to `validate`. Hibernate validates schemas during bootstrap but will not write to or execute DDL changes on the database.
2. **Flyway Migration Engine**: All schema changes and table structures are managed strictly and chronologically using Flyway migrations.
   - SQL scripts are maintained under `backend/src/main/resources/db/migration/`.
   - On application startup, Flyway automatically reads the `flyway_schema_history` table to run pending schema updates.
3. **HikariCP Connection Pool**: High-performance database connection pooling is configured with:
   - Max pool size of 10 connections.
   - Eager minimum idle configuration of 2 connections.
   - Connection timeouts set to prevent resource starvation.

## User Management Foundation

The user management subsystem handles registration, security credentials storage, role classification, and user domain mappings:

1. **User Schema**: The user persistence model is stored in the `app_users` table with unique indexes on both `username` and `email` to accelerate profile retrievals.
2. **Password Cryptography**: Passwords are never stored in plaintext. We isolate a standalone `BCryptPasswordEncoder` bean which produces secure, randomly salted one-way hashes using the standard BCrypt algorithm.
3. **Registration Flow**:
   - The request is serialized through `UserRegisterRequest` validating field constraints (such as minimum password length of 8, valid email shape, etc.).
   - `UserServiceImpl` validates unique constraints via repository checks. In case of conflicts, a `UserAlreadyExistsException` (409 Conflict) is thrown.
   - Raw credentials are encrypted using `PasswordEncoder`.
   - The user record is stored under default `Role.USER`.
   - The resulting record is mapped to a secure `UserResponse` DTO, which omits credential hashes and details only client-safe metadata.


