# API Reference Guide - Secure File Vault

This document details the REST API specifications, global request/response patterns, error structures, and the health check endpoint.

## API Conventions

- **Base URL**: `/api/v1`
- **Request Payloads**: Content-Type: `application/json` (except multipart files)
- **Response Payloads**: Content-Type: `application/json`
- **Authentication**: Bearer Token via Header `Authorization: Bearer <token>` (from Phase 4 onward)

## Endpoint Reference

### System Management

#### GET `/actuator/health`

Retrieves the runtime operational health of the application.

- **URL**: `/actuator/health`
- **Method**: `GET`
- **Auth Required**: No
- **Headers**: None
- **Response Structure (JSON)**:
  ```json
  {
    "status": "UP"
  }
  ```
- **HTTP Status Codes**:
  - `200 OK`: System is operational.
  - `503 Service Unavailable`: One of the dependencies (database, storage, mailer) is unhealthy.

## Global Error Response Format

From Phase 1 onward, the application will standardize error envelopes:

```json
{
  "timestamp": "2026-07-16T19:42:55Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field: email",
  "path": "/api/v1/users/register"
}
```
