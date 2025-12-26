# Exception Handling Workflow

This document outlines the architecture and logic flow of the exception handling system implemented in the LokiGame backend.

## 1. Core Concepts

The system is designed to be **modular**, **data-driven**, and **consistent**.

*   **`ErrorCodeInterface` & Enums:** Define *what* went wrong (Code, Message, HTTP Status). Examples: `SystemError`, `ValidationError`.
*   **`AppException`:** The standard runtime exception wrapper that carries the `ErrorCode` and optional context metadata.
*   **`ExceptionFactory`:** A centralized factory to *instantiate* exceptions with consistent context.
*   **`GlobalExceptionHandler`:** The centralized controller advice that *catches* exceptions and formats the HTTP response.
*   **`ExceptionHandlerInterface`:** A strategy pattern interface allowing different strategies for handling specific exception types (e.g., SQL errors vs. Validation errors).

---

## 2. ExceptionFactory vs. GlobalExceptionHandler

These two components serve opposite ends of the exception lifecycle:

| Component | Role | Location | Responsibility |
| :--- | :--- | :--- | :--- |
| **`ExceptionFactory`** | **Producer** | Service Layer / Domain Logic | **Creates and Throws** exceptions. It simplifies the code where errors occur by abstracting the complexity of building the exception object (e.g., attaching context maps). It *does not* decide the HTTP response format. |
| **`GlobalExceptionHandler`** | **Consumer** | Web Layer (`@RestControllerAdvice`) | **Catches and Processes** exceptions. It intercepts exceptions thrown from anywhere in the call stack. It decides the final HTTP status and JSON body (`ApiResponse`) sent to the client. |

**Analogy:**
*   `ExceptionFactory` is like a **Police Officer** writing a ticket (creating the specific charge/error).
*   `GlobalExceptionHandler` is like the **Judge** who reads the ticket and issues the final verdict/sentence (the response) to the citizen (the client).

---

## 3. Detailed Logic Flow

### Scenario A: Business Logic Error (e.g., "User Not Found")

1.  **Detection:** The `UserService` attempts to find a user by ID but gets `null`.
2.  **Creation (Factory):** The service calls `ExceptionFactory.createNotFoundException("User", userId, UserError.NOT_FOUND)`.
    *   *Code:* `ExceptionFactory` creates a `Map` containing `{"entityType": "User", "id": 123}`.
    *   *Code:* It constructs a new `AppException` holding the `UserError` enum and this map.
3.  **Throwing:** The service throws this `AppException`. Execution stops and bubbles up.
4.  **Catching (Global Handler):** `GlobalExceptionHandler.handleException(ex)` intercepts the `AppException`.
5.  **Routing:** The handler iterates through its list of `exceptionHandlers`.
    *   It finds `AppExceptionHandler`, which returns `true` for `canHandle(AppException)`.
6.  **Processing:**
    *   `AppExceptionHandler` extracts the `ErrorCode` (404 Not Found) and the context map.
    *   It builds an `ApiResponse` with:
        *   `code`: 1100 (from `UserError.NOT_FOUND`)
        *   `message`: "User account with ID 123 not found" (formatted if supported)
        *   `result`: `{"entityType": "User", "id": 123}`
7.  **Response:** The client receives a JSON response with HTTP 404.

### Scenario B: Input Validation Error (e.g., Invalid Email)

1.  **Detection:** A Controller receives a request. Spring's `@Valid` triggers. The `Email` field is malformed.
2.  **Throwing:** Spring Boot throws `MethodArgumentNotValidException`.
3.  **Catching (Global Handler):** `GlobalExceptionHandler.handleValidationException(ex)` intercepts it explicitly.
4.  **Mapping:**
    *   The handler iterates over Spring's `FieldErrors`.
    *   It attempts to map the error code (e.g., `Email`) to a `ValidationError` enum using `ValidationError.valueOf()`.
    *   It retrieves the user-friendly message from the enum.
5.  **Re-Packaging:** A `ValidationException` (a subclass of `AppException`) is created containing the list of field errors.
6.  **Processing:**
    *   `ValidationExceptionHandler` processes this exception.
    *   It structures the `result` field as a list of error objects: `[{ "field": "email", "message": "Wrong email format", ... }]`.
7.  **Response:** The client receives HTTP 400 with the list of validation failures.

### Scenario C: Database Constraint (e.g., Duplicate Username)

1.  **Detection:** `UserRepository.save()` is called. The database enforces a UNIQUE constraint on `username`.
2.  **Throwing:** The JDBC driver throws `SQLException`. Spring Data wraps this in `DataIntegrityViolationException`.
3.  **Catching (Global Handler):** `GlobalExceptionHandler.handleDataIntegrityViolation(ex)` intercepts it.
4.  **Delegation:** It extracts the root cause (`SQLException`) and delegates to `SqlExceptionHandler`.
5.  **Analysis:**
    *   `SqlExceptionHandler` inspects the raw SQL error message string (e.g., "Duplicate entry 'Loki' for key 'players.username'").
    *   It uses Regex or string matching to identify the type of error (Unique Constraint, Foreign Key, Null Value).
6.  **Standardization:** It maps this to a generic `SystemError` (e.g., `SystemError.SQL_CONSTRAINT_VIOLATION`).
7.  **Response:** The client receives HTTP 400 with a message like "Database constraint violation: Duplicate value found: Loki".

---

## 4. How to Extend

1.  **New Error Type:** Create a new Enum in `errorCategories` implementing `ErrorCodeInterface`.
2.  **New Logic:** In your Service, inject `ExceptionFactory` and call `createCustomException` passing your new Enum.
