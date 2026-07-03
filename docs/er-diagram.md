# LokiGame Entity Relationship Diagram

This Mermaid ER diagram covers the domain models under `src/main/java/com/theliems/lokigame/model/entity`, including:

- persisted JPA entities
- transient runtime-only domain entities
- the abstract parent-child inheritance chain
- the embedded audit value object reused across entities

```mermaid
erDiagram
    Player {
        UUID playerId PK
        string email
        string username
        string passwordHash
        Role role
        long currency
        long gold
        int highestClearedLevel
    }

    Hero {
        UUID heroId PK
        HeroGender gender
        string firstName
        string lastName
        boolean alive
        int level
        int star
        long experience
        double willPower
        long expPerSecond
        json equipment
        long randomSeed
    }

    HeroClass {
        UUID id PK
        string name
        string description
        map baseStats
        map statModifiers
        map statGrowthPerLevel
    }

    HeroStats {
        UUID id PK
        StatType statType
        double baseValue
        double finalValue
    }

    Origin {
        UUID id PK
        string name
        string description
        map statModifiers
    }

    World {
        UUID worldId PK
        string name
        string description
        double rarityWeight
        double statMultiplier
        double dungeonDifficultyMod
    }

    Name {
        UUID id PK
        string name
        NameType type
    }

    CurrencyRequest {
        UUID id PK
        long amount
        string reason
        RequestStatus status
        string adminNotes
    }

    CurrencyTransaction {
        UUID id PK
        long amount
        long balanceAfter
        TransactionType type
        TransactionSource source
        datetime timestamp
    }

    DungeonSeed {
        UUID id PK
        UUID playerId
        UUID worldId
        int dungeonLevel
        long seed
        boolean cleared
        string dungeonName
    }

    MonsterTemplate {
        UUID id PK
        string name
        string description
        map baseStats
        map statGrowthPerLevel
    }

    InventoryItem_ABSTRACT {
        UUID id PK
        Rarity rarity
        json metadata
        string itemName
    }

    EquipmentItem {
        EquipmentType equipmentType
        int level
    }

    EquipmentStat {
        UUID id PK
        StatType statType
        double value
        boolean isBaseStat
    }

    AuditMetaData_EMBEDDED {
        boolean active
        boolean deleted
        datetime createdAt
        datetime updatedAt
        string createdBy
        string updatedBy
    }

    Dungeon_TRANSIENT {
        UUID id PK
        string name
        string description
        int level
        UUID worldId
        long seed
    }

    Monster_TRANSIENT {
        UUID id PK
        string name
        string description
        UUID templateId
        int level
        map stats
    }

    DropTable_TRANSIENT {
        long baseGold
        double goldMultiplier
        double equipmentDropChance
        double materialDropChance
        long baseXp
        double xpMultiplier
    }

    Player ||--o{ Hero : owns
    Player ||--o{ InventoryItem_ABSTRACT : owns
    Player ||--o{ CurrencyRequest : submits
    Player ||--o{ CurrencyRequest : reviews
    Player ||--o{ CurrencyTransaction : has
    Player ||--o{ DungeonSeed : playerId_ref

    HeroClass ||--o{ Hero : classifies
    World ||--o{ Hero : originWorld
    World ||--o{ DungeonSeed : worldId_ref
    Origin ||--o{ Hero : origin
    Hero ||--o{ HeroStats : has

    InventoryItem_ABSTRACT ||--|| EquipmentItem : inherits
    EquipmentItem ||--o{ EquipmentStat : has_base_or_random_stat

    World ||--o{ Dungeon_TRANSIENT : belongs_to
    Dungeon_TRANSIENT ||--o{ Monster_TRANSIENT : contains
    Dungeon_TRANSIENT ||--|| DropTable_TRANSIENT : rewards
    MonsterTemplate ||--o{ Monster_TRANSIENT : scales_into

    AuditMetaData_EMBEDDED ||--|| Player : embedded_in
    AuditMetaData_EMBEDDED ||--|| Hero : embedded_in
    AuditMetaData_EMBEDDED ||--|| InventoryItem_ABSTRACT : embedded_in
    AuditMetaData_EMBEDDED ||--|| Name : embedded_in
    AuditMetaData_EMBEDDED ||--|| CurrencyRequest : embedded_in
    AuditMetaData_EMBEDDED ||--|| DungeonSeed : embedded_in
```
