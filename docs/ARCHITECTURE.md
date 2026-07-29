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
3. **Registration REST Endpoint & Flow**:
   - The presentation layer exposes `POST /api/v1/users/register` via `UserController`.
   - The request body is bound to `UserRegisterRequest` and validated via Spring's `@Valid` annotation to enforce constraints (username size, email format, minimum password length).
   - If validation fails, `GlobalExceptionHandler` intercepts `MethodArgumentNotValidException` and returns a 400 Bad Request with a field validation map.
   - `UserServiceImpl` validates unique database constraints, throwing `UserAlreadyExistsException` (409 Conflict) on violations, which is caught and converted by the exception advice.
   - Credentials are BCrypt hashed, the user is saved with `Role.USER`, and mapped to a `UserResponse` DTO returned inside a standard `ApiResponse` envelope within a `201 Created` HTTP response wrapper.

## JWT Authentication Foundation

The authentication subsystem validates user identities against credentials and issues cryptographically signed JWT tokens:

1. **Security Filters Setup**:
   - The system utilizes a stateless `SecurityFilterChain` permitting only actuator health (`GET /actuator/health`), registration (`POST /api/v1/users/register`), login (`POST /api/v1/auth/login`), and test public (`GET /api/v1/test/public`) routes publicly.
   - Registers `JwtAuthenticationFilter` (extending `OncePerRequestFilter`) preceding the default `UsernamePasswordAuthenticationFilter`.
   - Incoming requests are stateless (CSRF is disabled, session creation is stateless).
2. **Single-Query Lookup**:
   - Instead of checking the database twice for usernames and emails separately, `AuthServiceImpl` invokes `UserRepository.findByUsernameOrEmail(loginIdentifier, loginIdentifier)` to complete identity lookup in a single query.
3. **Password Security Verification**:
   - Password verification compares raw input against stored BCrypt hashes using Spring Security's `PasswordEncoder.matches`. No manual hashing or raw password comparison is performed.
4. **JWT Service & Properties Layout**:
   - Configuration is bound to the `JwtProperties` bean (`app.security.jwt` prefix).
   - `JwtService` executes startup checks to ensure the `JWT_SECRET` key is at least 256 bits (32 bytes) long, and `expirationMs` and `issuer` are defined. It fails fast by throwing an `IllegalStateException` on bootstrap if key requirements are unmet.
   - Generated tokens contain ONLY:
     - `sub`: User's `username`.
     - `role`: User's current privilege role.
     - `iss`: Configured issuer resolved from `app.security.jwt.issuer` (e.g. `"SecureFileVault"`).
     - `iat`: Timestamp indicating generation.
     - `exp`: Calculated expiration limit based on configuration.
   - Credentials returned in `LoginResponse` contain the parsed token prefix string `"Bearer"` mapped from `SecurityConstants.TOKEN_TYPE`.
5. **Spring Security UserDetails Loading**:
   - Integrated `CustomUserDetailsService` implementing Spring Security's native `UserDetailsService`.
   - Handles resolving user profiles using a single database query `findByUsernameOrEmail` and mapping attributes to spring security `UserDetails` objects to support standard authentication filters.
6. **Stateless Security Exceptions Handling**:
   - Configured custom `AuthenticationEntryPoint` (`CustomAuthenticationEntryPoint`) and `AccessDeniedHandler` (`CustomAccessDeniedHandler`) to prevent Spring Security from returning default HTML error pages on unauthorized (401) or forbidden (403) requests.
   - These components directly serialize standardized `ApiResponse` JSON envelopes, providing a consistent API contract for the client.
7. **JWT Authentication Filter Execution Flow**:
   - Read `Authorization` header. If missing or not starting with `Bearer `, ignore request and continue filter chain unauthenticated.
   - Extract JWT. If valid and `SecurityContextHolder` is empty, parse username from the token, load `UserDetails`, and validate the token's signature, issuer, and expiration.
   - Populate `UsernamePasswordAuthenticationToken` using `UserDetails` and authorities, and store inside `SecurityContextHolder`.
   - Any `JwtException` or `IllegalArgumentException` thrown during JWT processing is intercepted by the filter, which immediately clears the security context and delegates to `CustomAuthenticationEntryPoint` to yield a standard `401 Unauthorized` JSON response.
   - Skip filtering using `shouldNotFilter()` for actuator health, login, register, and test public paths.






