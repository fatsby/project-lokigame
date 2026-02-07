# 🏰 LokiGame Backend

> **Server-Authoritative Idle Gacha Game Engine**
> Built with Spring Boot 3.3, Java 21, and Docker.

## 📋 Project Overview
**LokiGame** is an online live-service backend for an idle RPG. It features:
* **Zero-Player Gameplay:** Battles are simulated deterministically on the server (Headless).
* **Idle Progression:** Offline rewards are calculated using "Math-on-Read" (Lazy Evaluation).
* **Gacha System:** RNG-based hero summoning with unique stats and cosmetics.
* **Data-Driven Design:** Game balance data (classes, base stats) is loaded from JSON, while player data lives in PostgreSQL.

---

## 🛠 Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 21 | Using Virtual Threads (Project Loom) for high concurrency. |
| **Framework** | Spring Boot 3.3+ | Web, Data JPA, Security, Validation. |
| **Database** | PostgreSQL 16 | Stores Players, Heroes (JSONB), and Inventory. |
| **Cache** | Redis | Stores active sessions, "Energy" timers, and deduplication. |
| **Auth** | Spring Security + JWT | Stateless authentication. |
| **Ops** | Docker Compose | Orchestrates the Backend, DB, and Cache. |

---

## 🚀 Getting Started (Onboarding Guide)

**Prerequisites:**
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Must be running)
* Git
* Java IDE (IntelliJ IDEA Recommended)

### 1. First-Time Setup
1.  **Clone the repository:**
    ```bash
    git clone <your-repo-url>
    cd lokigame-backend
    ```

2.  **Start the Environment:**
    Open your terminal in the project root and run:
    ```bash
    docker-compose up --build
    ```
    * *Note:* The first run downloads Maven dependencies and may take 5+ minutes.
    * Wait until you see: `Started LokigameApplication in X.XXX seconds`.

### 2. Verify Installation
* **API Health Check:** Open [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) (if enabled) or try to hit a public endpoint.
* **Database:** Connect via DBeaver using the credentials below.

---

## 💻 Development Workflow (Dev Mode)

We use a **Hot Reload** configuration in Docker to avoid rebuilding images constantly.

### How to apply code changes:
1.  **Edit Code:** Make changes to your `.java` files in your IDE.
2.  **Restart Container:**
    * Open **Docker Desktop Dashboard**.
    * Find the `backend` container.
    * Click the **Restart** (🔄) button.
3.  **Wait ~5 Seconds:** The container uses `mvn spring-boot:run` to compile *only* your changes and restart the app.

### How to apply Config/Docker changes:
If you modify `application.properties`, `pom.xml`, or `docker-compose.yml`, you must rebuild:
```bash
docker-compose up --build
```

## 🔐 Credentials & Ports

| Service  | Host                                      | Internal Docker Host | Port | Username | Password          |
|----------|-------------------------------------------|----------------------|------|----------|-------------------|
| API      | http://localhost:8080                     | backend              | 8080 | -        | -                 |
| Postgres | jdbc:postgresql://localhost:5432/lokigame | db                   | 5432 | loki     | SecureP@ssword123 |
| Redis    | localhost:6379                            | redis                | 6379 | -        | -                 |

## 🏛 Architecture Patterns
### 1. Authentication (JWT)
    Login: POST /api/auth/login returns an accessToken (24h) and refreshToken (7 days).

    Protected Routes: Add header Authorization: Bearer <token> to requests.

    Roles: ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN.

### 2. Static vs. Dynamic Data
    Static (Game Design): stored in src/main/resources/data/*.json.

    Examples: Hero Classes, Base Stats, Item Definitions.

    Loaded: On server startup into memory (Singleton Service).

    Dynamic (Player Progress): stored in PostgreSQL.

    Examples: Player entity, Hero entity (UUID, Owner, rolled stats).

### 3. RNG & Simulation
    Deterministic: Battles use a seeded RNG (SplittableRandom).

    Flow: Client sends Start Dungeon -> Server simulates result instantly -> Server returns Seed + Loot -> Client plays back the animation using the seed.