# Tech Stack - LokiGame

## Core Backend
*   **Language:** **Java 21 (LTS)** - Leveraging modern features like Virtual Threads (Project Loom) for high-concurrency simulation.
*   **Framework:** **Spring Boot 3.5.x** - Utilizing the latest stable release for Web, Data JPA, Security, and Validation.

## Data & Persistence
*   **Primary Database:** **PostgreSQL 16** - Reliable relational storage. Extensive use of `JSONB` for flexible hero stats and inventory data while maintaining relational integrity for player accounts.
*   **Caching & State:** **Redis** - Used for managing active sessions, high-frequency "Energy" timers, and request deduplication.
*   **Migrations:** **Flyway** - Ensuring version-controlled and reproducible database schema evolutions.

## Security & Infrastructure
*   **Authentication:** **Spring Security + JWT** - Stateless authentication using JSON Web Tokens. Supports standard roles (USER, MODERATOR, ADMIN).
*   **API Documentation:** **SpringDoc OpenAPI (Swagger)** - Automatically generated documentation for the REST API.
*   **Containerization:** **Docker Compose** - Orchestrating the development and production environments (Backend, Postgres, Redis).

## Development Utilities
*   **Lombok:** Reducing boilerplate code for POJOs and entities.
*   **MapStruct:** Type-safe and high-performance mapping between Entities and DTOs.
*   **Spring Boot DevTools:** Facilitating hot-reloading and faster development cycles.
