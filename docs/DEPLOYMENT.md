# Deployment Guide - Secure File Vault

This document explains the production deployment strategies for Secure File Vault.

## Production Build

To generate a standalone executable JAR, navigate to the `backend/` folder and build:

```bash
.\mvnw clean package -DskipTests
```

The compiled output will be generated at `backend/target/backend-0.0.1-SNAPSHOT.jar`. Run it directly via:

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Dockerization

In future phases, the project will include a `Dockerfile` and `docker-compose.yml` to spin up both Spring Boot and MySQL containers in a secure subnet.

### Dockerfile Strategy
- Use multi-stage builds.
- Stage 1: Build the JAR using Maven and OpenJDK 21.
- Stage 2: Distribute using a lightweight JRE runtime (e.g., Eclipse Temurin JRE Alpine) to minimize package footprint and security vulnerability exposure.

## Security Hardening for Production

1. **Disable Unused Actuator Endpoints**: Keep `management.endpoints.web.exposure.include=health` to only expose safe states.
2. **Environment Variable Injection**: Never store secrets in codebase configuration files. Pass production variables (`DB_PASSWORD`, `JWT_SECRET`) securely through container environments or systems secrets (e.g., Kubernetes Secrets, AWS Secrets Manager).
3. **HTTPS Enforcement**: Always route API calls through an Nginx proxy or load balancer configured with SSL certificates.
