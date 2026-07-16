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
