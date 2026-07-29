# Secure File Vault - AI Development Log & Learning Journal

This log tracks the chronological progression of the project, highlighting objective decisions, architecture reasoning, files created/modified, and learnings.

---

# Phase 0.5 – Project Initialization

## Objective
Establish a production-ready repository structure, initialize essential documentation logs, set up standard environment configurations, configure Spring Boot application properties, and integrate Spring Boot Actuator to expose a secure system health endpoint.

## Why this phase exists
A professional software project requires structure and governance before any functional code is written. Initializing the repository directories, documentation files, environment configuration templates, and build dependencies ensures that:
1. The development lifecycle is structured and auditable.
2. Production metrics and health checks are integrated from day one.
3. System dependencies compile cleanly on the target runtime.
4. Future developers can quickly onboard via clear setup and architecture blueprints.

## Problem Being Solved
Preventing unstructured package designs, un-tracked system modifications, environment variable leakage, and manual setup inconsistencies. Exposing a standardized, robust health check endpoint to verify system bootstrap and host platform compatibility.

## Files Created
- [README.md](file:///c:/Dev/Secure-File-Vault/README.md) - Project overview, setup, running guides, and full development roadmap.
- [LICENSE](file:///c:/Dev/Secure-File-Vault/LICENSE) - MIT License.
- [.env.example](file:///c:/Dev/Secure-File-Vault/.env.example) - Complete future environment variables template.
- [.gitignore](file:///c:/Dev/Secure-File-Vault/.gitignore) - Root-level ignore rules for workspace, tools, OS, database, and logs.
- `database/schema/.gitkeep` - Placeholder to track SQL schemas directory.
- `database/migrations/.gitkeep` - Placeholder to track database migrations directory.
- `database/seed/.gitkeep` - Placeholder to track seed scripts directory.
- `database/backups/.gitkeep` - Placeholder to track backup directory.
- `postman/collections/.gitkeep` - Placeholder to track API collections.
- `postman/environments/.gitkeep` - Placeholder to track environment files.
- `postman/exports/.gitkeep` - Placeholder to track other exports.
- `diagrams/.gitkeep` - Placeholder to track architecture diagrams.
- `screenshots/.gitkeep` - Placeholder to track application screenshots.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - This learning journal.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Layered and system architecture details.
- [docs/API.md](file:///c:/Dev/Secure-File-Vault/docs/API.md) - API specification and standards.
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Local onboarding instructions.
- [docs/DEPLOYMENT.md](file:///c:/Dev/Secure-File-Vault/docs/DEPLOYMENT.md) - Dockerization and server deployment strategies.

## Files Modified
- [backend/pom.xml](file:///c:/Dev/Secure-File-Vault/backend/pom.xml) - Added Spring Boot Actuator starter dependency.
- [backend/src/main/resources/application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/application.properties) - Configured server port, application name, logging levels, and Actuator endpoints.

## Packages Added
None (Spring Boot Actuator handles the endpoint automatically, meaning no custom packages were required in this initial stage).

## Classes Added
None.

## Dependencies Added
- `org.springframework.boot:spring-boot-starter-actuator`

## Configuration Changes
- `server.port=8080` - Binds the Web Server to port 8080.
- `spring.application.name=secure-file-vault` - Defines application identifier.
- `logging.level.root=INFO` - Configures baseline logger.
- `logging.level.com.saimanikantha.securefilevault=DEBUG` - Configures project packages to print debug logs.
- `management.endpoints.web.exposure.include=health` - Restricts web exposure to the health check endpoint exclusively.

## Request Flow
```text
[GET /actuator/health] 
      │
      ▼
[Spring DispatcherServlet]
      │
      ▼
[Actuator WebMvcEndpointHandlerMapping]
      │
      ▼
[HealthEndpoint (Invokes health indicators)]
      │
      ▼
[HealthResponse (HTTP 200: {"status": "UP"})]
```

## Security Considerations
- **Actuator Exposing Rules**: By default, Actuator can expose private system information. We explicitly set `management.endpoints.web.exposure.include=health` to restrict public visibility, keeping other metrics (`/env`, `/heapdump`, `/beans`) hidden.
- **Environment Leakage**: Standardized configurations are mapped to environment variables and kept out of version control via `.gitignore` rules targeting `.env` files.

## Performance Notes
- Actuator's `/health` check compiles state in microseconds. It runs in-memory and has zero overhead.

## Interview Questions
1. **What is Spring Boot Actuator, and why is it preferred over custom health controllers in production?**
   - Actuator is a framework-provided starter that implements standardized health and metrics monitoring endpoints. It is preferred because it integrates out-of-the-box with external orchestration tools (Kubernetes, AWS ECS, Prometheus) and automatically checks common database/cache/disk dependencies without requiring custom boilerplate.
2. **Why is it essential to restrict Actuator web exposure?**
   - Actuator endpoints (like `/env` or `/heapdump`) can dump runtime environment variables or memory contents containing private credentials, passwords, and security properties. Restricting exposure to `/health` ensures only vital operational statuses are reachable.

## Resume Value
- Standardized and initiated a Spring Boot 3.5.x enterprise application featuring robust project governance structure (comprehensive documentation, automated wrapper setups, Git constraints, and Actuator integrations).
- Configured secure system health telemetry endpoints utilizing Spring Boot Actuator, adhering to security guidelines.

## Lessons Learned
- Leveraging Spring Boot Actuator reduces Java boilerplate significantly. Instead of building controllers, custom DTOs, and writing tests for health checks, we can utilize native Spring Actuator indicators which are standard and extensible.

## Preparation for Next Phase
With compiling wrapper setups, configuration properties, documentation blueprints, and Actuator health routes active, the runtime environment is fully validated. We are ready to transition to **Phase 1: Backend Foundation**, where we will define packages (`config`, `constants`, `exception`, `util`), add utility helpers, mapper frameworks, validation constraints, and the global exception handling advice.

---

# Phase 0.5 Refinements – Entry Point Standardizing & Class Renaming

## Objective
Standardize the main application entry point to use standard Spring Boot annotation configuration without Java-level exclusions, rename the primary application class, and restrict the Actuator endpoint at the configuration properties level.

## Problem Being Solved
1. Decoupling class-level framework exclusions from code logic by moving auto-configuration exclusions to `application.properties`.
2. Standardizing package/class naming conventions by renaming the generic `BackendApplication` to `SecureFileVaultApplication`.

## Architecture Decisions
- Moved the auto-configuration exclusion rules (`DataSourceAutoConfiguration`, `HibernateJpaAutoConfiguration`) out of [SecureFileVaultApplication](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/SecureFileVaultApplication.java) code and configured them inside [application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/application.properties) using `spring.autoconfigure.exclude`. This ensures that the Java entry point remains clean and standard (`@SpringBootApplication`).

## Files Created
- [SecureFileVaultApplication.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/SecureFileVaultApplication.java) - Renamed main entry point class.
- [SecureFileVaultApplicationTests.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/SecureFileVaultApplicationTests.java) - Renamed verification test class.

## Files Modified
- [backend/src/main/resources/application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/application.properties) - Added `spring.autoconfigure.exclude` config properties.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Files Deleted
- `BackendApplication.java` (renamed and replaced by `SecureFileVaultApplication.java`).
- `BackendApplicationTests.java` (renamed and replaced by `SecureFileVaultApplicationTests.java`).

## Classes Added
- `SecureFileVaultApplication`
- `SecureFileVaultApplicationTests`

## Configuration Changes
- Added: `spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration`

## Request Flow
- `GET /actuator/health` -> Spring DispatcherServlet -> Actuator Handler -> HealthEndpoint -> Returns `{"status":"UP"}`.

## Lessons Learned
- Spring Boot allows auto-configurations to be excluded via configuration properties (`spring.autoconfigure.exclude`) as a clean alternative to the `@SpringBootApplication(exclude = ...)` annotation code. This keeps our source code standard and adaptable to different profile settings.

---

# Phase 1 – Backend Foundation

## Objective
Build a reusable, standardized backend infrastructure to handle generic responses, exceptions mappings, global configurations, validation bindings, logging, and date utilities for all future business endpoints.

## Architecture Decisions
- **Isolated Component Directories**: Structured the common elements into isolated packages organized by their responsibilities: `dto/common`, `exception`, `constants`, and `util`.
- **Standardized Response Protocol**: Defined a standard response model `ApiResponse<T>` using Lombok builder patterns to enforce consistent payload structures for both success and error responses. Using `java.time.Instant` in `ApiResponse` ensures standardized ISO 8601 formatting during JSON serialization.
- **Hierarchical Exception Design**: Formulated a standard custom exception hierarchy starting with `ApplicationException` containing `HttpStatus` to bind error contexts directly to HTTP routing.
- **Sterilized Error Responses**: Implemented `GlobalExceptionHandler` with `@RestControllerAdvice` to trap controller-level and framework-level errors (JSR-380 validation and generic exceptions), logging stack traces internally while returning sterilized `ApiResponse` responses to prevent information leakage.
- **Speculative Configuration Exclusions**: Excluded writing placeholder configurations, security settings, database connectors, or business layers to preserve project clarity and prevent unnecessary code.

## Packages Added
- `com.saimanikantha.securefilevault.dto.common`
- `com.saimanikantha.securefilevault.exception`
- `com.saimanikantha.securefilevault.constants`
- `com.saimanikantha.securefilevault.util`

## Classes Added
- [ApiResponse](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/common/ApiResponse.java)
- [ApplicationException](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ApplicationException.java)
- [ResourceNotFoundException](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ResourceNotFoundException.java)
- [ValidationException](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ValidationException.java)
- [GlobalExceptionHandler](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/GlobalExceptionHandler.java)
- [ApiPaths](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApiPaths.java)
- [ApplicationConstants](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApplicationConstants.java)
- [ErrorMessages](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ErrorMessages.java)
- [DateTimeUtil](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/util/DateTimeUtil.java)
- [ResponseUtil](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/util/ResponseUtil.java)

## Files Created
- [ApiResponse.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/common/ApiResponse.java)
- [ApplicationException.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ApplicationException.java)
- [ResourceNotFoundException.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ResourceNotFoundException.java)
- [ValidationException.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/ValidationException.java)
- [GlobalExceptionHandler.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/GlobalExceptionHandler.java)
- [ApiPaths.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApiPaths.java)
- [ApplicationConstants.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApplicationConstants.java)
- [ErrorMessages.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ErrorMessages.java)
- [DateTimeUtil.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/util/DateTimeUtil.java)
- [ResponseUtil.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/util/ResponseUtil.java)

## Files Modified
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Spring Concepts Used
- **`@RestControllerAdvice`**: Global interceptor applied to REST controller mappings to handle exceptions uniformly.
- **`@ExceptionHandler`**: Annotation mapping specific exception classes to handler methods inside the advice.
- **`MethodArgumentNotValidException`**: The framework exception thrown when validating parameters annotated with `@Valid`.

## Best Practices
- **Single Responsibility Principle (SRP)**: Each class is dedicated to a singular task: date utility format, HTTP exception responses, error messages, or custom exception types.
- **Secure Error Mapping**: Prevented the disclosure of database/system structures to clients by trapping parent exceptions and masking them as HTTP 500 while preserving logs on the backend server.
- **Standardized REST Payloads**: Implemented uniform success and error structures to allow easier integration for web and mobile clients.
- **Lombok Boilerplate Reduction**: Employed Lombok annotations (`@Data`, `@Builder`, `@Getter`, `@Slf4j`) to keep files clean and readable.

## Security Considerations
- Trapping `Exception.class` prevents leaking database dialect, connection strings, null pointers, and internal file paths to API clients, blocking trace-reconnaissance vulnerabilities.
- Left the `.env` local settings out of GitHub by matching git rules to root targets.

## Performance Notes
- `java.time.Instant` provides standard clock timestamping with minimal CPU cycles and maps directly to native machine representations.
- All constants are declared final and static to minimize allocation footprints.

## Interview Questions
1. **How does Spring's `@RestControllerAdvice` handle exception dispatching internally?**
   - When an exception occurs during controller method execution, the `DispatcherServlet` delegates exception resolution to a chain of handler resolvers, particularly `ExceptionHandlerExceptionResolver`. This resolver scans for classes annotated with `@RestControllerAdvice` containing methods with `@ExceptionHandler` corresponding to the thrown exception hierarchy, executing the closest matching type.
2. **Why is using a unified response wrapper (`ApiResponse`) beneficial in production APIs?**
   - It guarantees that clients receive a consistent payload shape (e.g., matching envelope properties like status, success, timestamp, message) regardless of whether the operation was a success or failure, simplifying client-side deserialization, logging, and error handling.

## Resume Value
- Architected the core foundation of a Spring Boot 3.5.x REST API, implementing global controller advices (`@RestControllerAdvice`) and a standardized JSON contract layout (`ApiResponse`) to handle request state reporting.
- Designed a secure exception handling protocol that maps internal runtime events (JSR-380 validation, database states, custom runtime anomalies) into standardized HTTP status codes while suppressing raw diagnostic stack traces.

## Lessons Learned
- Using default Jackson configs to serialize `java.time.Instant` objects ensures consistent, region-independent ISO 8601 formatting without custom code or formatting overrides.

## Preparation for Phase 2
The core system features generic error handlers, response structures, custom Exceptions, and standard utilities. We are now ready to transition to **Phase 2: Database Layer**, where we will install and configure MySQL connectivity, implement Flyway/Liquibase schema migrations, create datasource properties, and map JPA persistence engines.

---

# Phase 1 Refinements – Code Cleanup, Logging Split, and Validation Envelope Adjustments

## Objective
Refine the Phase 1 base structures by removing duplicate utility layers, mapping multiple messages to the validation properties payload, updating constants, and adjusting logging priorities based on error visibility.

## Problem Being Solved
1. Removing redundant abstractions (`DateTimeUtil`, `ResponseUtil`) that duplicate native Java Time and Lombok builder functionality.
2. Handling multiple validation message boundaries on a single input field to comply with JSR-380 multi-constraint mappings.
3. Separating expected client-level 4xx messages from server-side 5xx exceptions in exception handlers to prevent noise in server logs.

## Architecture Decisions
- Deleted the `DateTimeUtil` and `ResponseUtil` classes.
- Altered JSR-380 validation mapping inside [GlobalExceptionHandler](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/GlobalExceptionHandler.java) to compile errors under a `Map<String, List<String>>` signature.
- Split log handlers inside [GlobalExceptionHandler](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/GlobalExceptionHandler.java) to write client exceptions (4xx) at `WARN` level and server exceptions (500) at `ERROR` level.
- Pruned `SYSTEM_BASE` path from [ApiPaths](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApiPaths.java) and renamed [ErrorMessages](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ErrorMessages.java) keys to `RESOURCE_NOT_FOUND`, `VALIDATION_FAILED`, and `INTERNAL_SERVER_ERROR`.

## Files Deleted
- `DateTimeUtil.java` (removed duplicate abstractions)
- `ResponseUtil.java` (removed duplicate abstractions)

## Files Modified
- [GlobalExceptionHandler.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/GlobalExceptionHandler.java)
- [ApiPaths.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ApiPaths.java)
- [ErrorMessages.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/ErrorMessages.java)
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Lessons Learned
- Removing helper wrappers (like custom date/response utilities) when Java Time libraries and Lombok builder interfaces already provide minimal, expressive operations prevents developer-facing abstraction overload and keeps the codebase simple.
- Grouping constraint failures under a mapping of lists (`Map<String, List<String>>`) ensures that multiple JSR-380 constraint violations on the same variable (e.g., both `@NotNull` and `@Size`) are preserved and reported to client integrations.

---

# Phase 2 – Database Layer

## Objective
Configure and establish the relational database persistence infrastructure by integrating MySQL, Spring Data JPA, HikariCP connection pooling, and Flyway schema migrations.

## Why this phase exists
Modern, production-grade applications require robust database governance. Setting up managed database layers ensures:
1. Version-controlled, reproducible SQL migration rollouts, ensuring that schemas across environments are synchronized automatically.
2. High-performance, bounded connection pooling configurations (HikariCP) to handle application load without leaking database resources.
3. Strict JPA schema rules (Hibernate validation mode only) to prevent auto-generated Hibernate code from rewriting schemas dynamically.

## Problem Being Solved
Preventing manual database schema updates (which lead to drift across dev, staging, and production environments), optimizing database connections to prevent memory depletion, and ensuring that Hibernate behaves as a read/write mapper without executing destructive DDL migrations.

## Files Created
- [create_database.sql](file:///c:/Dev/Secure-File-Vault/database/schema/create_database.sql) - Database schema creation helper.
- [V1__init_db_health.sql](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/db/migration/V1__init_db_health.sql) - Flyway baseline migration verification table `system_health_check`.

## Files Modified
- [backend/pom.xml](file:///c:/Dev/Secure-File-Vault/backend/pom.xml) - Added Flyway dependencies.
- [backend/src/main/resources/application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/application.properties) - Configured database datasource, connection pool, JPA Hibernate validation, and Flyway triggers.
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Documented database initialization commands and environment settings.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Documented database pooling structures and migration designs.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Packages Added
None.

## Classes Added
None.

## Dependencies Added
- `org.flywaydb:flyway-core`
- `org.flywaydb:flyway-database-mysql`

## Configuration Changes
- Removed properties-level autoconfiguration exclusions.
- Configured standard placeholders for `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password`.
- Added HikariCP parameters (`maximum-pool-size=10`, `minimum-idle=2`).
- Configured JPA to validate schema: `spring.jpa.hibernate.ddl-auto=validate`.
- Configured Flyway initialization parameters: `spring.flyway.enabled=true`, `spring.flyway.baseline-on-migrate=false`.

## Request Flow
- Application Startup -> Spring Container initializes HikariCP -> Flyway scans `db/migration/` -> Executes missing SQL migrations -> Connects to MySQL -> Runs Hibernate JPA validation checks -> Exposes Web context.
- `GET /actuator/health` -> Spring Actuator -> Invokes `DataSourceHealthIndicator` -> Queries connection -> Returns `{"status": "UP"}`.

## Security Considerations
- Kept environment variable credentials out of source code by utilizing runtime property placeholders (`${DB_PASSWORD:}`).
- Set Hibernate DDL auto-generation to `validate` to prevent accidental loss of data or structure modifications in production.
- Kept local `.env` configuration out of git using root gitignore rules.

## Performance Notes
- Configured Hikari connection parameters: max pool bounds keep connections stable and prevent thread deadlock issues, while minimum idle keeps lightweight standby connections warm.

## Interview Questions
1. **Why do we use Flyway migrations instead of letting Hibernate generate tables automatically (e.g. `ddl-auto=update`)?**
   - Hibernate auto-generation (`ddl-auto=update` or `create`) is fine for prototyping but dangerous for production. It does not provide version tracking, cannot handle complex index additions or data conversions, and might drop/alter tables unexpectedly causing data loss. Flyway migrations provide exact SQL version controls, audit logs via the `flyway_schema_history` table, and guarantee identical schemas across environments.
2. **What does `spring.jpa.hibernate.ddl-auto=validate` do?**
   - It tells Hibernate to check the database schema at startup to ensure it matches the entities defined in the application. If there are mismatches, the application context fails to start. This prevents mismatch errors at runtime and protects the database against write deviations.

## Resume Value
- Implemented automated database migration control using Flyway MySQL support, ensuring version-controlled schemas across dev and staging.
- Configured a robust database persistence pool using HikariCP and Spring Data JPA, mapping variables to runtime environment environments, and securing schema rules via Hibernate `validate` restrictions.

## Lessons Learned
- Spring Boot 3.x splits Flyway database support into specific jars; adding `flyway-database-mysql` alongside `flyway-core` is required for MySQL migrations.
- Disabling auto-configuration exclusions and moving database settings to properties allows Spring Boot Actuator's `DataSourceHealthIndicator` to automatically report database health out of the box.

## Preparation for Phase 3
The database layer compiles, migrates, and validates connection contexts successfully. We are now ready to transition to **Phase 3: Authentication**, where we will define the user models, user storage interfaces, password hashing services, and verification endpoints.

---

# Phase 3 – User Management Foundation

## Objective
Establish the core User domain model and infrastructure including the `app_users` table migration, `User` entity mappings, BCrypt password hashing, registration DTO validation, user mapping layer, and `UserService` registration logic.

## Why this phase exists
A secure application requires a solid identity model. In this phase, we establish a robust user persistence and validation structure:
1. Version-controlled SQL schema layout (`app_users` table) with optimized search indexes on key unique columns (`username`, `email`).
2. Standalone, modular encryption context configuration (`spring-security-crypto`) to hash credentials without adding routing complexities.
3. Decoupled request and response models (DTOs) with JSR-380 input validation ensuring clean boundary inputs.
4. Comprehensive integration tests proving persistence operations, indexing, and uniqueness constraints.

## Problem Being Solved
Safeguarding passwords by hashing them before persistence, protecting database performance with unique indexes, standardizing DTO structures, isolating security layers from standard APIs, and verifying core database operations with unit/integration testing.

## Files Created
- [V2__create_users_table.sql](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/db/migration/V2__create_users_table.sql) - Flyway migration for user schema.
- [Role.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/entity/Role.java) - Enum for User roles (USER, ADMIN).
- [User.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/entity/User.java) - User persistence model mapping `app_users` table.
- [UserRepository.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/repository/UserRepository.java) - JPA database repository.
- [UserRegisterRequest.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/request/UserRegisterRequest.java) - Registration payload DTO.
- [UserResponse.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/response/UserResponse.java) - User summary return DTO.
- [SecurityConfig.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/config/SecurityConfig.java) - PasswordEncoder bean configuration.
- [UserMapper.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/mapper/UserMapper.java) - Entity/DTO mapper.
- [UserService.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/service/UserService.java) - Registration business layer interface.
- [UserServiceImpl.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/service/impl/UserServiceImpl.java) - Registration business implementation.
- [UserAlreadyExistsException.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/UserAlreadyExistsException.java) - Custom Conflict (409) Exception class.
- [UserRepositoryTest.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/repository/UserRepositoryTest.java) - Repository integration tests.

## Files Modified
- [backend/pom.xml](file:///c:/Dev/Secure-File-Vault/backend/pom.xml) - Added spring-security-crypto dependency.
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Noted users migrations.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Added User Domain Architecture sections.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Packages Added
- `com.saimanikantha.securefilevault.entity`
- `com.saimanikantha.securefilevault.repository`
- `com.saimanikantha.securefilevault.mapper`
- `com.saimanikantha.securefilevault.service`
- `com.saimanikantha.securefilevault.service.impl`

## Classes/Enums Added
- `User`
- `Role`
- `UserRepository`
- `UserRegisterRequest`
- `UserResponse`
- `SecurityConfig`
- `UserMapper`
- `UserService`
- `UserServiceImpl`
- `UserAlreadyExistsException`

## Dependencies Added
- `org.springframework.security:spring-security-crypto`

## Configuration Changes
- Declared a standard `PasswordEncoder` bean inside `SecurityConfig.java`.

## Request Flow
- User Registration request payload -> JSR-380 input validation annotations -> `UserService.register()` -> Check if username or email exists in `UserRepository` -> Encrypt raw password via `PasswordEncoder.encode()` -> Map to Entity -> Persist database -> Map to `UserResponse` -> Return success.

## Security Considerations
- Kept raw passwords out of the database by applying one-way BCrypt hashing with automatic salting.
- Suppressed password hashes from responses by using a clean response DTO (`UserResponse`).
- Strictly isolated user infrastructure by omitting security filter chains, session state controllers, and login endpoints.

## Performance Notes
- Placed non-clustered database index nodes (`idx_users_username` and `idx_users_email`) on user lookup attributes to optimize execution.

## Interview Questions
1. **Why is it essential to use a separate response DTO (`UserResponse`) instead of returning the `User` entity directly?**
   - Returning database entities directly is bad practice. It can leak sensitive fields (like password hashes, security tokens) or internal database structural details to clients. It also tightens coupling between the presentation layer and database schema. Using a clean DTO isolates security and enables flexible property modeling for clients.
2. **How does `@PrePersist` and `@PreUpdate` work in JPA?**
   - These are JPA lifecycle callback annotations. Methods marked with `@PrePersist` are executed automatically before the SQL `INSERT` is executed on the database, allowing us to set creation timestamps. Methods marked with `@PreUpdate` are executed before SQL `UPDATE` queries, ensuring that update timestamps are automatically maintained by Hibernate.

## Resume Value
- Designed the User Management persistence and business logic foundation for a secure SaaS application.
- Configured BCrypt cryptography workflows to secure credentials and defined custom conflict Exception Handlers to map duplicate checks into standard REST 409 envelopes.
- Programmed end-to-end repository integration test coverage using JUnit and Spring Boot integration tests.

## Lessons Learned
- Spring Security's cryptography library (`spring-security-crypto`) can be integrated as a standalone dependency, which avoids pulling in Spring Security filters or default HTTP Basic login redirects.
- Using JPA callback annotations keeps auditing attributes logic centralized within the entity itself.

## Preparation for Phase 4
The user management persistence and business layers are complete. We are now ready to transition to **Phase 4: User Registration API**, where we will expose REST registration endpoints, validation rules, and integration test coverage.

---

# Phase 4 – User Registration API

## Objective
Implement a secure, validated REST endpoint for registering users (`POST /api/v1/users/register`) that processes user input DTOs, validates constraint requirements, delegates database persistence to the service layer, and handles system conflicts and validation exceptions gracefully.

## Why this phase exists
The Presentation/Controller Layer acts as the gateway to the application. Exposing a secure endpoint separates REST transport logic (HTTP statuses, payload parsing, validation checks) from internal service layer operations. Standardizing API contracts ensures frontends and integration services consume predictable response shapes.

## Files Created
- [UserController.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/controller/UserController.java) - Exposes REST endpoints for user actions.
- [UserControllerTest.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/controller/UserControllerTest.java) - Integration tests asserting successful registration, bad validation states, and duplication conflicts.

## Files Modified
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Documented payload examples.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Noted Controller layer mappings.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Packages Added
- `com.saimanikantha.securefilevault.controller`

## Classes Added
- `UserController`
- `UserControllerTest`

## Configuration Changes
None.

## Request Flow
- `POST /api/v1/users/register` -> `UserController` -> JSR-380 input validation checks (`@Valid`) -> If invalid, throws `MethodArgumentNotValidException` -> Caught by `GlobalExceptionHandler` -> Returns HTTP 400 Bad Request with field errors map.
- If valid, passes `UserRegisterRequest` payload -> `UserService.register()` -> Repository lookup checks -> If duplicate username/email, throws `UserAlreadyExistsException` -> Caught by `GlobalExceptionHandler` -> Returns HTTP 409 Conflict.
- If unique, hashes password via BCrypt -> Persists user -> Maps to `UserResponse` DTO -> Returns HTTP 201 Created wrapped in standard `ApiResponse` envelope.

## Architecture Decisions
- Separated status transmission by using Spring's `ResponseEntity` wrapper to determine actual HTTP response headers while retaining `statusCode` properties inside the JSON `ApiResponse` payload.
- Avoided writing redundant controller code by mapping all routes under `ApiPaths.BASE_PATH` and relying strictly on constructor injection.

## Spring Concepts Used
- **`@RestController` & `@RequestMapping`**: Declares Spring MVC controllers returning raw JSON serializable payloads.
- **`@Valid` & `@RequestBody`**: Triggers Spring Validator engine to intercept invalid incoming payloads before dispatching to controllers.
- **`MockMvc` & `@AutoConfigureMockMvc`**: Performs web-layer testing with complete Spring integration context mocking without spinning up actual HTTP containers.

## Best Practices Applied
- Kept controller logic thin; delegates mapping and business logic entirely to `UserMapper` and `UserService` layers.
- Used standard HTTP verbs and status codes: registration operations return `201 Created` on success, client payload errors return `400 Bad Request`, and conflicts return `409 Conflict`.

## Security Considerations
- Kept raw credentials out of controller printouts by leveraging Lombok parameters exclusion.
- Prevented credential leakages by only transferring `UserResponse` DTO payloads, completely hiding password hashes.
- Configured REST tests under `@Transactional` to roll back changes, maintaining local test database integrity.

## Performance Notes
- Leveraged JSON serialization optimizations by standardizing Jackson serializing parameters.
- Kept memory usage low by avoiding heavy session bindings (REST routes operate strictly stateless).

## Common Mistakes Avoided
- Avoided duplicating exception advice mappings; the pre-existing `GlobalExceptionHandler` intercepts validation failures and conflicts dynamically.
- Avoided hardcoding URL prefixes by injecting centralized `ApiPaths.BASE_PATH` constants.

## How this phase prepares the next phase
The user registration presentation and domain foundations are complete. We are now ready to transition to **Phase 5: JWT Authentication Foundation**, where we will define authentication logic, configure password validation, setup JWT token properties, and execute secure JWT claims signature mappings.

---

# Phase 5 – JWT Authentication Foundation

## Objective
Establish the cryptographic JWT token generation utilities and credentials validation services (login by username or email) returning a standard, cryptographically signed Bearer JWT token on success.

## Why this phase exists
Establishing identity assurance is the core pillar of a secure cloud vault. Integrating credentials verification at this phase ensures:
1. Plaintext passwords are authenticated using one-way BCrypt verification models (never stored or matched manually).
2. JWT generation follows precise security constraints, restricting claims payloads to public metrics (`sub`, `role`, `iss`, `iat`, `exp`), preventing PII leakage.
3. Fail-fast security key validations are run at bootstrap, preventing execution using compromised, weak keys.
4. Stateless sessions are configured as default security boundaries.

## Files Created
- [JwtProperties.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/config/JwtProperties.java) - Configuration properties binding prefix `app.security.jwt`.
- [SecurityConstants.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/constants/SecurityConstants.java) - Holds static token headers.
- [LoginRequest.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/request/LoginRequest.java) - Login input fields payload.
- [LoginResponse.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/dto/response/LoginResponse.java) - Safe login result token summary.
- [InvalidCredentialsException.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/exception/InvalidCredentialsException.java) - Mapped HTTP 401 Unauthorized exception.
- [JwtService.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/JwtService.java) - Cryptographic token utility verifying structural limits and signature actions.
- [CustomUserDetailsService.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/CustomUserDetailsService.java) - Formulates spring security UserDetails from database user models.
- [AuthService.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/service/AuthService.java) - Verification contract.
- [AuthServiceImpl.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/service/impl/AuthServiceImpl.java) - Authentication service resolving credentials in single queries.
- [AuthController.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/controller/AuthController.java) - Exposes POST `/auth/login` endpoint.
- [JwtServiceTest.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/security/JwtServiceTest.java) - Unit tests for token manipulation.
- [AuthControllerTest.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/controller/AuthControllerTest.java) - Integration tests verifying authentication states.
- [application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/test/resources/application.properties) - Test config properties defining local dummy keys.

## Files Modified
- [backend/pom.xml](file:///c:/Dev/Secure-File-Vault/backend/pom.xml) - Added security and JJWT dependencies.
- [backend/src/main/resources/application.properties](file:///c:/Dev/Secure-File-Vault/backend/src/main/resources/application.properties) - Configured properties bindings.
- [SecurityConfig.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/config/SecurityConfig.java) - Set up stateless session management and endpoint permit restrictions.
- [UserRepository.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/repository/UserRepository.java) - Added `findByUsernameOrEmail`.
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Noted login endpoint payload examples.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Documented security workflows and token layout.
- [docs/AI_DEVELOPMENT_LOG.md](file:///c:/Dev/Secure-File-Vault/docs/AI_DEVELOPMENT_LOG.md) - Appended this section.

## Packages Added
- `com.saimanikantha.securefilevault.security`

## Classes Added
- `JwtProperties`
- `SecurityConstants`
- `LoginRequest`
- `LoginResponse`
- `InvalidCredentialsException`
- `JwtService`
- `CustomUserDetailsService`
- `AuthService`
- `AuthServiceImpl`
- `AuthController`
- `JwtServiceTest`
- `AuthControllerTest`

## Configuration Changes
- Switched session generation policy to stateless `SessionCreationPolicy.STATELESS`.
- Configured endpoints path filters explicitly allowing health (GET), register (POST), and login (POST), and requiring authentication for all other actions.
- Bound `app.security.jwt.secret`, `app.security.jwt.expiration-ms`, and `app.security.jwt.issuer` into `JwtProperties`.

## Request Flow
- `POST /api/v1/auth/login` -> Intercepted by `AuthController` -> Validates payload format -> Passes parameters to `AuthServiceImpl` -> Performs a single repository query `findByUsernameOrEmail` -> If not found, throws generic `InvalidCredentialsException` (401 Unauthorized) -> Compares password via `PasswordEncoder.matches()` -> Generates token through `JwtService` -> Returns `LoginResponse` (200 OK).

## Architecture Decisions
- Configured `JwtProperties` using `@ConfigurationProperties(prefix = "app.security.jwt")` to decouple key configurations.
- Excluded users name from the token payload to protect PII security.
- Validated `JWT_SECRET` key size, expiration time, and issuer configuration parameters during post-construction initialization phase to block insecure environments immediately.
- Established `CustomUserDetailsService` implementing standard Spring Security `UserDetailsService` to cleanly load user configurations for upcoming filter layers.

## Spring Concepts Used
- **`@ConfigurationProperties`**: Binds system property values into standard java object instances.
- **`SecurityFilterChain`**: Configures path access rules, stateless configurations, and authentication requirements.
- **`@PostConstruct`**: Executes code lifecycle hooks automatically after bean dependency configurations compile.
- **`UserDetailsService`**: Standard interface utilized by Spring Security components to resolve identity records.

## Best Practices Applied
- Used generic messaging in credential mismatches to protect system from user enumeration scans.
- Enforced single database roundtrips by applying SQL username OR email query matching.
- Prevented credential leakages by using distinct login request and response DTO schemas.

## Security Considerations
- Kept the signing secret key size at 256 bits or greater, ensuring cryptographic resilience.
- Ensured stateless session configurations, completely neutralizing CSRF session vulnerability types.
- Placed validation checks on token claims to prevent signature tampering.

## Performance Notes
- Bypassed session cookie validation, optimizing execution speeds.
- Combined username and email lookups into a single SELECT statement, halving DB lookup costs.

## Common Mistakes Avoided
- Avoided duplicating exception advice mappings; the pre-existing `GlobalExceptionHandler` intercepts validation failures and conflicts dynamically.
- Avoided hardcoding URL prefixes by injecting centralized `ApiPaths.BASE_PATH` constants.

## How this phase prepares the next phase
The authentication services, user details loader, token properties, and database structures are complete. We are now ready to transition to **Phase 6: Secure Routing and Filters**, where we will construct the custom `JwtAuthenticationFilter`, hook it into the Spring Security filter chain (`UsernamePasswordAuthenticationFilter`), read request authentication contexts, and secure backend REST APIs.

## Interview Questions
1. **How does HS256 protect JWT tokens from tampering?**
   - HS256 (HMAC with SHA-256) is a symmetric cryptographic algorithm. The issuer signs the payload using a secret key. When a client sends the token back, the server recalculates the signature using the same secret key and compares it to the token's signature. If any claims in the payload are altered, the signature recalculation will mismatch, allowing the server to immediately reject the token as tampered.
2. **Why must a symmetric key for HS256 be at least 256 bits (32 bytes) long?**
   - Cryptographic standards (like RFC 7518) dictate that HS256 signing keys must be at least 256 bits long to protect against brute-force attacks. If a key is too short, an attacker capturing a signed JWT could rapidly execute offline brute-force attacks to crack the secret key and begin forging valid administrative tokens.

## Summary
Phase 5 established the secure JWT Authentication foundation. It introduced stateless Spring Security configurations, implemented a standalone `JwtService` that validates key safety at startup, structured a single-query user credentials lookup mapping, defined a foundational `CustomUserDetailsService` loader, and implemented MockMvc integration tests and unit tests verifying the token workflow.

---

# Phase 6 – JWT Authentication Filter & Request Authorization

## Objective
Implement complete JWT request authentication by intercepting incoming requests, validating signatures, resolving identities, establishing spring security context mappings, and standardizing security exceptions.

## Why this phase exists
Establishing stateless request authentication restricts access to protected application API endpoints to verified clients only. This phase:
1. Validates that every incoming protected request holds a valid signature.
2. Integrates authentication checks into the servlet layer to intercept requests before executing business routing.
3. Decouples authorization rules from presentation controllers by mapping permission matches inside security configs.
4. Preserves the API JSON contract by intercepting 401 and 403 status pages and wrapping them in the standard `ApiResponse` JSON formatting schema.

## Files Created
- [JwtAuthenticationFilter.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/JwtAuthenticationFilter.java) - Filter resolving HTTP authorization header Bearer tokens.
- [CustomAuthenticationEntryPoint.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/CustomAuthenticationEntryPoint.java) - Handles 401 unauthorized errors in JSON.
- [CustomAccessDeniedHandler.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/CustomAccessDeniedHandler.java) - Handles 403 forbidden errors in JSON.
- [TestController.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/controller/TestController.java) - Helper REST controller verification endpoints located in the test source tree to prevent production leakage.
- [JwtAuthenticationFilterTest.java](file:///c:/Dev/Secure-File-Vault/backend/src/test/java/com/saimanikantha/securefilevault/security/JwtAuthenticationFilterTest.java) - MockMvc integration tests for authentication filter routing.

## Files Modified
- [SecurityConfig.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/config/SecurityConfig.java) - Registered filter order, exception entry points, and path permits.
- [JwtService.java](file:///c:/Dev/Secure-File-Vault/backend/src/main/java/com/saimanikantha/securefilevault/security/JwtService.java) - Added header parsing and UserDetails validation overload.
- [docs/ARCHITECTURE.md](file:///c:/Dev/Secure-File-Vault/docs/ARCHITECTURE.md) - Noted filter flows.
- [docs/SETUP.md](file:///c:/Dev/Secure-File-Vault/docs/SETUP.md) - Noted verification payloads.

## Packages Added
None

## Classes Added
- `JwtAuthenticationFilter`
- `CustomAuthenticationEntryPoint`
- `CustomAccessDeniedHandler`
- `TestController`
- `JwtAuthenticationFilterTest`

## Configuration Changes
- Configured stateless filter chains to run `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
- Integrated `CustomAuthenticationEntryPoint` and `CustomAccessDeniedHandler` inside the http exception handling block.
- Permitted public route `GET /api/v1/test/public` and restricted `GET /api/v1/test/protected` to authenticated users.

## Request Flow
- Incoming Request -> Intercepted by `JwtAuthenticationFilter` (skips if health/login/register/public) -> Resolves token via `Authorization` header -> Validates token validity/expiry -> Loads `UserDetails` from `CustomUserDetailsService` -> Sets authentication inside `SecurityContextHolder` -> Continues filter chain.
- If signature fails or is expired -> Intercepted in filter -> Clear context -> Delegates to `CustomAuthenticationEntryPoint` -> Writes HTTP 401 `ApiResponse` JSON output.

## Architecture Decisions
- Configured JSON error serialization directly inside entry points because servlet-layer filter errors execute before controller advisors.
- Placed validation checks on `SecurityContextHolder.getContext().getAuthentication() == null` to prevent duplicate authentications.
- Reused standard spring security authentication providers (`DaoAuthenticationProvider`) to ensure compliance with enterprise-grade framework flows.

## Spring Concepts Used
- **`OncePerRequestFilter`**: Base class ensuring single execution per request thread.
- **`SecurityContextHolder`**: Holds security principal, credentials, and authorities information.
- **`AuthenticationEntryPoint`**: Handles authentication initiation triggers when unauthenticated calls are intercepted.
- **`AccessDeniedHandler`**: Handles authorization failures when authenticated requests violate access constraints.

## Best Practices Applied
- Avoided complicating filter parsing code by isolating header extraction to a reusable `extractTokenFromHeader` utility inside `JwtService`.
- Enabled fail-fast filter termination on invalid signatures, preventing secondary system processing.
- Structured response formats to enforce consistent JSON contracts for the client application.

## Security Considerations
- Kept SecurityContext stateless, ensuring zero session state is persisted across API operations.
- Intercepted token signature tampering dynamically in the filter to block unauthorized clients immediately.

## Performance Notes
- Leveraged `shouldNotFilter` checks to bypass regex matching on common public paths.
- Avoided repeated UserDetails database loading by caching security contexts inside request threads.

## Common Mistakes Avoided
- Avoided duplicate authentications on requests that are pre-authenticated.
- Handled filter chain exceptions before they reached spring MVC dispatchers to prevent default HTML white-label error pages.

## How this phase prepares the next phase
Request authentication, security contexts, and filter mappings are complete. The project is now fully secure and prepared for subsequent phases, such as setting up file upload domains and managing secure directories.

## Interview Questions
1. **Why does JwtAuthenticationFilter extend OncePerRequestFilter instead of implementing GenericFilterBean or Filter?**
   - In Spring MVC, a single request can trigger forward, error, or include dispatches, which can cause standard servlet filters to execute multiple times during a single request lifecycle. `OncePerRequestFilter` guarantees that the filter is executed exactly once per request thread, preventing duplicate authentication processing or database queries.
2. **Why can't CustomAuthenticationEntryPoint exceptions be caught by a @ControllerAdvice class?**
   - `@ControllerAdvice` and `@ExceptionHandler` operate within Spring MVC's dispatcher servlet container. Because spring security filters execute much earlier in the servlet filter chain (before reaching the dispatcher servlet), any security exceptions thrown by filters are outside the scope of Spring MVC and must be handled directly at the servlet filter level.

## Summary
Phase 6 established secure request filtering and authorization. It implemented the `JwtAuthenticationFilter` checking header credentials, created custom JSON serializing entry points for 401 and 403 errors, and added JUnit integration tests covering missing headers, expired credentials, and tampered signatures.









