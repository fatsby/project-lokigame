# Plan: Global Exception Handling & Factory

## Phase 1: Core Exception Structure
This phase sets up the base classes and the standard error response format.

- [ ] Task: Create Error Response DTO
    - [ ] Sub-task: Define `ApiErrorResponse` class with fields (`timestamp`, `status`, `error`, `message`, `path`).
    - [ ] Sub-task: Add Lombok annotations for Builder and Data access.
- [ ] Task: Create Base Application Exception
    - [ ] Sub-task: Create abstract `LokiGameException` extending `RuntimeException`.
    - [ ] Sub-task: Define constructors accepting `message` and `HttpStatus`.
- [ ] Task: Implement Specific Custom Exceptions
    - [ ] Sub-task: Create `ResourceNotFoundException` extending `LokiGameException` (404 Not Found).
    - [ ] Sub-task: Create `ValidationException` extending `LokiGameException` (400 Bad Request).
    - [ ] Sub-task: Create `UnauthorizedActionException` extending `LokiGameException` (403 Forbidden).

## Phase 2: Exception Factory
This phase implements the factory pattern to simplify exception generation.

- [ ] Task: Implement ExceptionFactory
    - [ ] Sub-task: Create `ExceptionFactory` utility class.
    - [ ] Sub-task: Add static method `resourceNotFound(String entityName, Object id)` returning `ResourceNotFoundException`.
    - [ ] Sub-task: Add static method `validationError(String message)` returning `ValidationException`.
    - [ ] Sub-task: Add Unit Tests for `ExceptionFactory` to ensure correct message formatting.

## Phase 3: Global Handler Implementation
This phase connects the exceptions to the Spring Boot framework.

- [ ] Task: Create GlobalExceptionHandler
    - [ ] Sub-task: Create class annotated with `@RestControllerAdvice`.
    - [ ] Sub-task: Implement `@ExceptionHandler` for `LokiGameException`.
    - [ ] Sub-task: Implement `@ExceptionHandler` for `MethodArgumentNotValidException` (Validation errors).
    - [ ] Sub-task: Implement `@ExceptionHandler` for `Exception` (Generic fallback).
    - [ ] Sub-task: Ensure all handlers return `ResponseEntity<ApiErrorResponse>`.
- [ ] Task: Integration Testing
    - [ ] Sub-task: Create a dummy controller that throws these exceptions.
    - [ ] Sub-task: Write `MockMvc` tests to verify the JSON response structure and HTTP status codes for each exception type.

## Phase 4: Finalization
- [ ] Task: Conductor - User Manual Verification 'Finalization' (Protocol in workflow.md)
