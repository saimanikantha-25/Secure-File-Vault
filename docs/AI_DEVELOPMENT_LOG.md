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

