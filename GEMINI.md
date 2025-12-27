# GEMINI.md - Project Context & Architecture

## 1. Project Overview
**Title:** Server-Authoritative Idle Gacha Game
**Genre:** Online Live-Service, Dungeon Crawler, Idle RPG
**Core Mechanic:** * **Zero-Player Game:** Players select teams; the server simulates battles deterministically.
* **Idle Progression:** Heroes gain XP/Loot while the player is offline (Math-on-Read).
* **Gacha:** Summoning system for Heroes and Cosmetics.

## 2. Technology Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.3+ (Web, Data JPA, Security, Validation)
* **Database:** PostgreSQL 16+ (Primary), Redis (Session/Cache)
* **Build Tool:** Maven or Gradle
* **Auth:** Spring Security + JWT (Stateless)
* **Utilities:** * `Lombok` (Boilerplate)
    * `Mapstruct`
    * `Jackson` (JSON processing)
    * `Flyway` (Database Migrations)

## 3. Architecture Pattern
* **Server-Authoritative:** All logic (RNG, Battle outcomes, Loot generation) happens on the backend. The frontend is a "dumb" visualizer.
* **Data-Driven Design:** * **Static Data** (Class definitions, Base stats, Asset paths) is stored in **JSON** files loaded into memory at startup.
    * **Dynamic Data** (Player ownership, Generated Hero Instances) is stored in **PostgreSQL**.
* **Deterministic RNG:** Battles are simulated using a seeded RNG so results can be replayed on the client identically.

---

## 4. Phase One Goals: Foundation & Summoning
**Objective:** Functional Auth, Hero Generation, and Persistence.

### 4.1. Authentication
* **Requirement:** Users must log in to play.
* **Implementation:** JWT (JSON Web Tokens).
* **Flow:** Login -> Receive Access Token -> Send Token in Header (`Authorization: Bearer <token>`) for all game requests.

### 4.2. Hero System (The "Unique" Logic)
Heroes are unique instances generated via RNG. There are no pre-defined "Character Cards" (e.g., no generic "Arthur" card). Every summoned hero is a unique combination of stats and visuals.

#### A. Hero Structure (Entity)
Stored in PostgreSQL `heroes` table.
* **ID:** UUID (Unique Identifier)
* **Owner:** Player UUID
* **Class:** Enum/String (e.g., `MAGE`, `PALADIN`)
* **Rarity:** 1-7 Stars (Determines Stat Multipliers/Floors)
* **Stats (JSONB):** * `health`, `armour`, `abilityPower`, `expPerSecond`
    * *Note:* Stats are rolled based on Class + Rarity + World.
* **Visuals (JSONB):**
    * `hair_id`, `face_id`, `top_id`, `bottom_id`
    * *Note:* Visuals are strictly cosmetic but persisted to maintain identity.
* **World Orgin**: World ID

#### B. Generation Logic (Algorithm)
When a player summons a hero:

1.  **Roll Rarity:** Weighted RNG (e.g., 1% chance for 7-Star).
2.  **Roll Class:** Random selection (e.g., `MAGE`).
3.  **Fetch Constraints:** Lookup `ClassDefinition` from memory (JSON).
    * *Example:* Mage has `min_ap: 10`, Paladin has `min_ap: 0`.
4.  **Calculate Stats:** * `BaseStat = Random(ClassMin, ClassMax)`
    * `FinalStat = BaseStat * RarityMultiplier` (or Rarity adds a flat bonus to the Min).
5.  **Roll Visuals:** * Select random `visual_id` from available pools for that Class/Gender.
    * Ensure valid combinations (e.g., Mage robes don't mix with Plate legs if restricted).
6. **Roll World:** Select a random id from `worlds.json`, each `world` has `stat_multiplier` which will increase the hero base attributes. The rarer the world, the higher the stats.

### 4.3. Inventory System
**Objective:** Manage ownership of Items (Visuals, Weapons) and validation for equipping them.

#### A. Architecture: Split-Data Model
*   **Static Definition (JSON):** Defines *what* an item is (Name, Base Stats, Class Restrictions).
    *   *Items:* `items.json` (Weapons, Armor, Accessories).
    *   *Visuals:* `visuals.json` (Body Features like Hair, Faces).
*   **Dynamic Ownership (PostgreSQL):** Defines *who* owns an instance of an item.
    *   Stored in `inventory_items` table.

#### B. Inventory Item Entity
*   **ID:** UUID (Unique Instance ID).
*   **Player ID:** Owner UUID.
*   **Item ID:** String (Reference to JSON ID, e.g., `robe_mage_rare_01`).
*   **Item Type:** Enum (`VISUAL`, `WEAPON`).
*   **Item Tier:** Enum (`NORMAL`, `RARE`, `EPIC`, `LEGENDARY`, `GODSENT`).
*   **Metadata (JSONB):** Stores instance-specific data (e.g., specific rolled stats for weapons, or `null` for static visuals).

#### C. Equipment Logic
1.  **Request:** Player requests to equip Item X (Instance ID) to Hero Y.
2.  **Validation:**
    *   **Ownership:** Does Player own Item X?
    *   **Availability:** Is Item X already equipped on another Hero? (Items are unique instances; you need 2 copies to equip 2 heroes).
    *   **Compatibility:** Does Item X's static definition allow Hero Y's Class?
3.  **Action:** Update Hero Y's `visuals` or `equipment` field.

#### D. Item Tiers
Items follow a 5-tier rarity system that determines their power ceiling and generation logic:
*   **Multipliers:** `NORMAL` (1.0x), `RARE` (1.2x), `EPIC` (1.5x), `LEGENDARY` (2.0x), `GODSENT` (5.0x).
*   **Godsent Logic:** These items are uniquely powerful and have **hard-coded** stats defined in metadata, bypassing standard RNG generation.
*   **Crafting Integration:** Future crafting systems will use player skill levels to influence the probability of hitting higher tiers.

### 4.4. RNG Architecture
*   **Unified Service:** `WeightedRngService` centralizes all weighted probability logic (e.g., picking a World, rolling Item Tiers).
*   **Strategy:** Uses a generic `WeightedSelector<T>` implementation (TreeMap-based cumulative weighting) to ensure O(log N) performance and testability.
*   **Usage:** Registries (like `WorldRegistry`) delegate to this service rather than implementing their own random logic.

---

## 5. Data Structures (JSON Configuration)

### 5.1 `classes.json` (Static Game Data)

Defines the boundaries for RNG generation.
```json
[
  {
    "id": "mage",
    "name": "Arcane Mage",
    "base_stats": {
      "health": { "min": 50, "max": 80 },
      "ability_power": { "min": 10, "max": 25 },
      "armour": { "min": 0, "max": 5 }
    },
    "allowed_weapon_types": ["STAFF", "WAND"]
  },
  {
    "id": "paladin",
    "name": "Light Paladin",
    "base_stats": {
      "health": { "min": 90, "max": 120 },
      "ability_power": { "min": 0, "max": 10 },
      "armour": { "min": 10, "max": 20 }
    }
  }
]
```

### 5.2 visuals.json (Static Asset Registry)
Defines cosmetic body features.
```json
{
  "hair": ["hair_short_01", "hair_long_02", "hair_punk_03"],
  "faces": ["face_angry", "face_calm", "face_old"]
}
```

### 5.3 worlds.json
```json
[
  {
    "id": "midgard",
    "name": "Midgard",
    "description": "The realm of humans. Standard power levels.",
    "rarity_weight": 100,
    "stat_multiplier": 1.0,
    "dungeon_difficulty_mod": 1.0
  },
  {
    "id": "niflheim",
    "name": "Niflheim",
    "description": "The frozen realm. Heroes forged here are hardened.",
    "rarity_weight": 20,
    "stat_multiplier": 1.2,
    "dungeon_difficulty_mod": 1.5
  },
  {
    "id": "asgard",
    "name": "Asgard",
    "description": "The realm of gods. Immense power flows here.",
    "rarity_weight": 5,
    "stat_multiplier": 1.5,
    "dungeon_difficulty_mod": 3.0
  }
]
```

### 5.4 items.json (Equipment Definitions)
Defines equippable items with stat ranges.
```json
[
  {
    "id": "robe_mage_basic",
    "name": "Apprentice Robe",
    "type": "ARMOR",
    "slot": "BODY",
    "classRestriction": ["mage"],
    "isDefault": true,
    "baseStats": { "armour": {"min": 1.0, "max": 2.0} }
  }
]
```

## 6. Infrastructure (Docker)
* **Orchestration:** `docker-compose.yml` manages Backend, Postgres, and Redis.
* **Build:** Multi-stage Dockerfile (Maven Build -> JRE Run).
* **Run Command:** `docker-compose up --build`
* **Environment Variables:**
    * `SPRING_DATASOURCE_URL` -> Internal Docker network (`jdbc:postgresql://db:5432...`)
    * `SPRING_DATA_REDIS_HOST` -> `redis`