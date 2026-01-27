# Project Loki - Server-Authoritative Idle Gacha Game

## 1. Project Overview
**Genre:** Idle RPG, Dungeon Crawler, Gacha
**Core Loop:**
1.  **Summon Heroes:** Players spend premium currency to roll unique heroes.
2.  **Manage Inventory:** Equip heroes with weapons and armor (Items).
3.  **Dungeon Crawling:** Heroes fight in dungeons (simulated on server) to earn Gold and Loot.
4.  **Progression:** Upgrade stats, find better gear, and conquer harder worlds.

## 2. Architecture
*   **Backend:** Java 21 + Spring Boot 3.5.9
*   **Database:** PostgreSQL (Entities), Redis (Caching/Sessions)
*   **Security:** JWT-based stateless authentication.
*   **Design Pattern:** Server-Authoritative & Data-Driven.
    *   *Static Data* (Classes, Worlds) is loaded/cached.
    *   *Dynamic Data* (Instances of heroes/items) is persisted in DB.

## 3. Key Entities & Services

### **Controller Layer**
-   **AuthController:** Registration (`POST /register`), Login (`POST /login`), Token Refresh.
-   **HeroController:** Roll heroes (`POST /roll`), View My Heroes, Get Hero Details (`GET /{id}`).
-   **DungeonController:** List Dungeons, Run Dungeon (`POST /{id}/run`).
-   **InventoryController:** Equip items, View inventory.

### **Service Layer (Business Logic)**
-   **HeroRollService:** Handles the complex logic of generating a unique hero (RNG for Class, Origin, World, Stats) and deducting Currency.
-   **DungeonService:** Simulates dungeon runs. Calculates rewards (Gold, Items) based on `DropTable` probabilities.
-   **AuthService:** Manages users, password hashing, and JWT generation.
-   **EquipmentService:** Generates unique equipment instances with randomized stats.

### **Entity Logic**
-   **Player:** The user account. Contains:
    -   `currency`: Premium currency (Gems) for summoning.
    -   `gold`: Gameplay currency for upgrades.
-   **Hero:** A unique character instance owned by a player.
    -   Has **Base Stats** (born with) and **Final Stats** (after equipment).
-   **HeroStats:** EAV (Entity-Attribute-Value) model for flexible stats (HP, ATK, etc.).
-   **Dungeon:** A location with a difficulty level and `DropTable`.

---

## 4. Equipment vs. Inventory (Why two classes?)

This distinction is crucial for a scalable RPG architecture:

### **1. Equipment (The "Definition")**
*   **What it is:** The *blueprint* or *template* of an item.
*   **Example:** "Rusty Sword" (Base Attack: 5-8).
*   **Role:** stored in `equipment` table (sometimes purely static JSON in other games, but here persisted for generated unique base items).
*   **Analogy:** The product page on Amazon.

### **2. InventoryItem (The "Instance")**
*   **What it is:** The specific object the player *owns*.
*   **Example:** "Player1's Rusty Sword" (Instance ID: 123, Tier: RARE, +2 Strength).
*   **Role:** Links a `Player` to an `Equipment` definition.
    *   Contains instance-specific data like **Enhancement Level**, **Tier** (Rarity), and current **Owner**.
*   **Analogy:** The physical box delivered to your house.

**Why split them?**
-   **Efficiency:** Thousands of players can own the same "Iron Sword" definition without duplicating all its base stats string data.
-   **Flexibility:** You can have unique instances (e.g., a "Legendary Iron Sword +5") that reference the same base definition but have different modifiers stored in `InventoryItem`.

---

## 5. Engines
-   **BattleEngine:** (Planned/Partial) Deterministic simulation of combat between Heroes and Monsters.
-   **RngService:** Centralized seeded random number generation to ensure replays are consistent.
