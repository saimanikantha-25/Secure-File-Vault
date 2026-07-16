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




