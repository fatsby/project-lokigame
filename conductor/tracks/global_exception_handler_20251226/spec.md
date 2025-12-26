# Specification: Global Exception Handling & Factory

## 1. Overview
This track establishes a robust error handling mechanism for the LokiGame backend. It involves creating a `GlobalExceptionHandler` to catch and format exceptions into a standardized API response and an `ExceptionFactory` pattern to streamline the creation of domain-specific exceptions (e.g., `EntityNotFoundException`, `InsufficientResourcesException`).

## 2. Goals
*   **Standardized Error Responses:** All API errors must return a consistent JSON structure (e.g., `timestamp`, `status`, `error`, `message`, `path`).
*   **Centralized Handling:** Use `@ControllerAdvice` to manage exceptions globally, keeping controllers clean.
*   **Scalable Exception Creation:** Implement a factory pattern to easily generate exceptions for various entities without repetitive boilerplate.
*   **Traceability:** Ensure exceptions are logged with sufficient context for debugging.

## 3. Core Components

### 3.1 Custom Exceptions
*   **`BaseException`:** Abstract parent class for all custom application exceptions.
*   **`ResourceNotFoundException`:** Thrown when an entity (Player, Hero, etc.) cannot be found.
*   **`ValidationException`:** Thrown when business rules are violated (e.g., negative stats).
*   **`UnauthorizedActionException`:** Thrown when a player attempts an action they don't have permission for.

### 3.2 Exception Factory
*   **`ExceptionFactory`:** A utility class or service with static methods or a builder pattern to instantiate exceptions.
    *   *Example:* `ExceptionFactory.resourceNotFound("Hero", heroId)`

### 3.3 Global Handler
*   **`GlobalExceptionHandler`:** A class annotated with `@RestControllerAdvice`.
    *   Handles `RuntimeExceptions` (fallback).
    *   Handles custom exceptions (`ResourceNotFoundException`, etc.).
    *   Handles Spring's `MethodArgumentNotValidException` (for DTO validation errors).

### 3.4 Error Response DTO
*   **`ApiErrorResponse`:** A POJO defining the JSON structure sent to the client.

## 4. Requirements
*   **Java 21:** Use modern switch expressions or pattern matching if applicable.
*   **Spring Boot 3.3+:** Leverage standard Spring validation and exception handling mechanisms.
*   **Lombok:** Use `@Builder`, `@Data` for DTOs and Exceptions.
