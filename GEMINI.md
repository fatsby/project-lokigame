# GEMINI.md - Project Context & Architecture

## 1. Project Overview
**Title:** Server-Authoritative Idle Gacha Game
**Genre:** Online Live-Service, Dungeon Crawler, Idle RPG
**Core Mechanic:** 
* **Zero-Player Game:** Players select teams; the server simulates battles deterministically.
* **Idle Progression:** Heroes gain XP/Loot while the player is offline (Math-on-Read).
* **Gacha:** Summoning system for Heroes and Cosmetics.

## 2. Technology Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.9 (Web, Data JPA, Security, Validation)
* **Database:** PostgreSQL 16+ (Primary), Redis (Session/Cache)
* **Build Tool:** Maven
* **Auth:** Spring Security + JWT (Stateless)
* **Utilities:** 
    * `Lombok` (Boilerplate)
    * `Mapstruct`
    * `Jackson` (JSON processing)
    * `Flyway` (Database Migrations)

## 3. Architecture Pattern
* **Server-Authoritative:** All logic (RNG, Battle outcomes, Loot generation) happens on the backend. The frontend is a "dumb" visualizer.
* **Procedural Generation:** Equipment and Heroes are generated on-the-fly using `ThreadLocalRandom`. There are no static definition files for Equipment stats; they are calculated algorithmically.
* **Math-on-Read Progression:** Offline progress is calculated via Redis session tracking. When a player logs in, the server calculates `(Now - LastActivity) * Rate` to award XP.
* **Deterministic RNG:** Battles are simulated using known states to ensure consistency.

---

## 4. Key Systems & Logic

### 4.1. Authentication & Session
* **JWT:** Stateless authentication for API requests.
* **Redis Session:** 
    *   Used to track `Online` status via heartbeats (every 30s).
    *   Used to calculate `Offline Duration` for idle rewards.
    *   **Flow:** Login -> Record Timestamp -> Client sends Heartbeats -> Logout/Timeout -> Calc Offline Duration.

### 4.2. Hero System (The "Unique" Logic)
Heroes are unique instances generated via RNG. There are no pre-defined "Character Cards".

#### A. Hero Entity
* **ID:** UUID
* **Owner:** Player UUID
* **Class & Origin:** Defines base stat ranges and growth potential.
* **Rarity:** 1-7 Stars (1-5 Common-Leg, 6-7 Godsent). 
* **Stats:** `Health`, `Armour`, `AbilityPower`, `Willpower`, `ExpPerSecond`.
* **Visuals:** JSONB mapping of cosmetic IDs (`hair_id`, `face_id`).

#### B. Generation Logic (`HeroRollService`)
1.  **Roll Template:** Select Random Class, Origin, and World.
2.  **Generate Base:** Create Hero entity with seeded random stats.
3.  **Equip Starter Set:** Automatically generates a full set of equipment (Weapon, Armor, etc.) using `EquipmentGenerator` and equips it.

### 4.3. Equipment & Inventory System
**Architecture:** Procedural Generation + Split-Entity Model.
**Contrast to Old Docs:** *There is no `items.json` for equipment.* All equipment is unique.

#### A. Entities
*   **`Equipment` (The Item):** 
    *   A standalone entity containing the **Physical Stats** (Attack, Def, Crit).
    *   **Generated Procedurally:** Stats are derived from `ItemLevel`, `Rarity`, and `EquipmentType` using math-based scaling.
    *   **Data:** `baseStats` (Fixed per type) + `randomStats` (Bonus rolls based on rarity).
*   **`InventoryItem` (The Ownership):**
    *   Links a `Player` to an `Equipment` instance.
    *   Acts as the "Inventory Slot" wrapper.
    *   **Key Field:** `itemId` stores the UUID of the linked `Equipment`.

#### B. Linkage
`Player` -> `InventoryItem` (Owner) -> `Equipment` (Stats)
`Hero` -> Map<Slot, UUID> (Link to `InventoryItem`)

### 4.4. Battle & Dungeon System
*   **Dungeon Run:** managed by `DungeonService`.
    *   **Rewards:** Guaranteed Gold + Chance for Equipment Drop.
    *   **Drop Logic:** If equipment drops, `EquipmentService` generates a new unique item scaled to the Dungeon Level.
*   **Battle Simulation:** `BattleService` uses `BattleEngine`.
    *   **Process:** Fetch Heroes -> Fetch Dungeon Monsters -> Simulate Turn-by-Turn -> Return Log & Result.
    *   **XP:** Awarded to all participating heroes on victory.

---

## 5. Coding Standards & Guidelines

**CRITICAL:** All code contributions must strictly adhere to the following principles.

### 5.1. SOLID Principles
*   **Single Responsibility:** Classes should have one job. Separate "Calculation" (Logic) from "Persistence" (Repository) and "Orchestration" (Service).
    *   *Bad:* A Service calculating stats, saving to DB, and formatting JSON.
    *   *Good:* `XpCalculatorService` (Math only) -> `LevelingService` (Applies Math) -> `HeroRepository` (Saves).
*   **Open/Closed:** Entities and Systems should be open for extension but closed for modification. Use Enums, Strategies, or Interfaces for variable logic (e.g., `StatType` enums instead of hardcoded fields where possible).
*   **Liskov Substitution:** Subtypes must be substitutable for base types.
*   **Interface Segregation:** Keep interfaces small and focused.
*   **Dependency Injection:** Always fetch dependencies via Constructor Injection (`@RequiredArgsConstructor`). Never use field injection (`@Autowired`).

### 5.2. Clean Code & Scalability
*   **Service Layering:** 
    *   **Controllers:** Handle HTTP, DTO mapping. Delegate to Services immediately.
    *   **Services:** Business Logic, Transaction Management (`@Transactional`).
    *   **Repositories:** Database access only.
*   **DTOs:** Always use DTOs for API I/O. Never expose Entities directly in Controllers.
*   **Hardcoding:** Avoid "Magic Numbers". Use Constants classes (e.g., `LevelingConstants`).
*   **Error Handling:** Use `ExceptionFactory` for consistent error responses.
*   **Logging:** Use `@Slf4j` and log distinct business events (e.g., "Player X leveled up", "Battle Y started").

---

## 6. Infrastructure (Docker)
* **Orchestration:** `docker-compose.yml` (Backend, Postgres, Redis).
* **Run:** `docker-compose up --build`
