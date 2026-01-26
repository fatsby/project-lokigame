# LokiGame Backend - Complete Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Authentication & Authorization](#authentication--authorization)
5. [Domain Model](#domain-model)
6. [API Endpoints](#api-endpoints)
7. [Game Systems](#game-systems)
8. [Database Schema](#database-schema)
9. [Setup & Deployment](#setup--deployment)
10. [Development Guidelines](#development-guidelines)

---

## Project Overview

**LokiGame** is a server-authoritative RPG/Gacha/Roguelike game backend built with Spring Boot. The game features procedurally generated heroes and equipment, turn-based battle simulation, and a gacha system where players roll for unique heroes.

### Core Philosophy
- **Procedural Generation**: Every hero and equipment piece is uniquely generated
- **Server-Authoritative**: All game logic runs on the server
- **Infinite Replayability**: Random generation ensures no two playthroughs are the same
- **Clean Architecture**: Separation of concerns with clear boundaries

---

## Technology Stack

### Core Framework
- **Java 21** (LTS) - Modern Java features including records, pattern matching
- **Spring Boot 3.5.9** - Web, Data JPA, Security, Validation
- **Maven** - Dependency management and build tool

### Database & Persistence
- **PostgreSQL 16+** - Primary relational database
- **Spring Data JPA** - ORM and repository abstraction
- **Hibernate** - JPA implementation
- **Flyway** - Database migrations (optional)

### Security
- **Spring Security** - Authentication and authorization framework
- **JWT (JSON Web Tokens)** - Stateless authentication
  - Access Token: 24 hours expiration
  - Refresh Token: 7 days expiration
- **BCrypt** - Password hashing

### Utilities
- **Lombok** - Reduces boilerplate code
- **Jackson** - JSON processing
- **SpringDoc OpenAPI** - API documentation (Swagger)

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **Redis** (optional) - Caching and session management

---

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controller Layer             │  REST API endpoints
├─────────────────────────────────────┤
│         Service Layer                │  Business logic
├─────────────────────────────────────┤
│      Generator & Engine Layer       │  Procedural generation, battle simulation
├─────────────────────────────────────┤
│         Domain Layer                 │  Entities, Enums, DTOs
├─────────────────────────────────────┤
│        Repository Layer              │  Data access
├─────────────────────────────────────┤
│         Database                     │  PostgreSQL
└─────────────────────────────────────┘
```

### Package Structure

```
com.theliems.lokigame
├── controller/          # REST controllers
│   ├── auth/           # Authentication endpoints
│   ├── admin/          # Admin-only endpoints
│   ├── hero/           # Hero management
│   ├── equipment/      # Equipment management
│   ├── battle/         # Battle simulation
│   ├── dungeon/        # Dungeon runs
│   └── economy/         # Currency requests
├── service/             # Business logic services
│   ├── auth/           # Authentication service
│   ├── hero/           # Hero services
│   ├── equipment/      # Equipment services
│   ├── battle/         # Battle services
│   ├── dungeon/        # Dungeon services
│   ├── economy/        # Currency request services
│   └── player/         # Player services
├── generator/          # Procedural generation
│   ├── HeroFactory     # Hero generation
│   └── EquipmentGenerator  # Equipment generation
├── engine/             # Game engines
│   └── BattleEngine    # Turn-based combat
├── model/              # Domain models
│   ├── entity/         # JPA entities
│   ├── dto/            # Data transfer objects
│   └── enums/          # Enumerations
├── repository/         # Data access repositories
├── infrastructure/     # Infrastructure concerns
│   ├── config/         # Configuration classes
│   ├── security/       # Security components
│   └── exception/      # Exception handling
└── config/             # Application configuration
```

### Design Patterns

1. **Repository Pattern**: Data access abstraction
2. **Service Layer Pattern**: Business logic encapsulation
3. **DTO Pattern**: Data transfer between layers
4. **Factory Pattern**: Hero and equipment generation
5. **Strategy Pattern**: Battle engine algorithms
6. **Dependency Injection**: Spring IoC container

---

## Authentication & Authorization

### Authentication Flow

1. **Registration**
   ```
   POST /api/auth/register
   Body: { username, email, password }
   Response: { accessToken, refreshToken, tokenType }
   ```

2. **Login**
   ```
   POST /api/auth/login
   Body: { username, password }
   Response: { accessToken, refreshToken, tokenType }
   ```

3. **Token Refresh**
   ```
   POST /api/auth/refresh
   Body: { refreshToken }
   Response: { accessToken, refreshToken }
   ```

### Authorization

**Roles:**
- `ROLE_USER` - Standard player
- `ROLE_ADMIN` - Administrator with special privileges

**Security Configuration:**
- Public endpoints: `/api/auth/**`, `/actuator/**`, `/swagger-ui/**`
- Admin-only endpoints: `/api/admin/**`
- All other endpoints require authentication

**JWT Token Structure:**
```json
{
  "sub": "user-uuid",
  "username": "player123",
  "role": "ROLE_USER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Using Authentication

All protected endpoints require the `Authorization` header:
```
Authorization: Bearer <accessToken>
```

---

## Domain Model

### Core Entities

#### Player
- `playerId` (UUID) - Primary key
- `username` (String) - Unique username
- `email` (String) - Unique email
- `passwordHash` (String) - BCrypt hashed password
- `role` (Role enum) - USER or ADMIN
- `currency` (Long) - In-game currency (gold)

#### Hero
- `heroId` (UUID) - Primary key
- `owner` (Player) - Owner of the hero
- `heroClass` (HeroClass) - Class template
- `origin` (Origin) - Origin template
- `originWorld` (World) - World template
- `firstName`, `lastName` (String) - Generated name
- `gender` (HeroGender) - MALE or FEMALE
- `star` (Integer) - Rarity (1-7 stars)
- `level` (Integer) - Current level
- `experience` (Long) - Experience points
- `randomSeed` (Long) - Uniqueness seed
- `equipment` (Map<EquipmentSlot, UUID>) - Equipped items
- `stats` (List<HeroStats>) - Hero statistics

#### HeroStats
- `id` (UUID) - Primary key
- `hero` (Hero) - Parent hero
- `statType` (StatType) - HP, ATK, DEF, etc.
- `baseValue` (Double) - Base stat value
- `finalValue` (Double) - Final value with equipment bonuses

#### Equipment
- `id` (UUID) - Primary key
- `owner` (Player) - Owner
- `equipmentType` (EquipmentType) - WEAPON, HELMET, etc.
- `rarity` (Rarity) - COMMON, RARE, EPIC, LEGENDARY
- `level` (Integer) - Equipment level
- `baseStats` (List<EquipmentStat>) - Base stat modifiers
- `randomStats` (List<EquipmentStat>) - Random stat modifiers

#### CurrencyRequest
- `id` (UUID) - Primary key
- `player` (Player) - Requesting player
- `amount` (Long) - Requested amount
- `reason` (String) - Request reason
- `status` (RequestStatus) - PENDING, APPROVED, REJECTED
- `reviewedBy` (Player) - Admin who reviewed
- `adminNotes` (String) - Admin notes

#### Monster
- `id` (UUID) - Primary key
- `name` (String) - Monster name
- `level` (Integer) - Monster level
- `stats` (Map<StatType, Double>) - Monster statistics

#### Dungeon
- `id` (UUID) - Primary key
- `name` (String) - Dungeon name
- `level` (Integer) - Dungeon level
- `monsters` (List<Monster>) - Monsters in dungeon
- `dropTable` (DropTable) - Reward configuration

### Enumerations

#### StatType
- `HP` - Health Points
- `ATK` - Attack Power
- `DEF` - Defense
- `CRIT_RATE` - Critical Hit Rate (0.0-1.0)
- `CRIT_DAMAGE` - Critical Hit Damage Multiplier
- `SPEED` - Turn order speed

#### EquipmentType
- `WEAPON`
- `HELMET`
- `ARMOR`
- `BOOTS`
- `RING`
- `NECKLACE`

#### EquipmentSlot
- `WEAPON`
- `HELMET`
- `ARMOR`
- `BOOTS`
- `RING`
- `NECKLACE`

#### Rarity
- `COMMON` - 1-2 random stats
- `RARE` - 2-3 random stats
- `EPIC` - 3-4 random stats
- `LEGENDARY` - 4-6 random stats

#### Role
- `ROLE_USER` - Standard player
- `ROLE_ADMIN` - Administrator

---

## API Endpoints

### Authentication

#### Register
```
POST /api/auth/register
Body: {
  "username": "player123",
  "email": "player@example.com",
  "password": "password123"
}
Response: {
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

#### Login
```
POST /api/auth/login
Body: {
  "username": "player123",
  "password": "password123"
}
Response: {
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer"
}
```

#### Refresh Token
```
POST /api/auth/refresh
Body: {
  "refreshToken": "eyJhbGc..."
}
Response: {
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc..."
}
```

### Heroes

#### Roll Hero (Costs 100 currency)
```
POST /api/hero/roll
Headers: Authorization: Bearer <token>
Response: {
  "heroId": "uuid",
  "firstName": "Aria",
  "lastName": "Storm",
  "heroClassName": "Mage",
  "originName": "Elf",
  "worldName": "Aetheria",
  "level": 1,
  "star": 3,
  "stats": [...],
  "equipment": {}
}
```

#### Get My Heroes
```
GET /api/hero/my-heroes
Headers: Authorization: Bearer <token>
Response: [HeroResponse, ...]
```

### Equipment

#### Generate Equipment
```
POST /api/equipment/generate
Headers: Authorization: Bearer <token>
Body: {
  "equipmentType": "WEAPON",
  "playerLevel": 5,
  "dungeonLevel": 3
}
Response: {
  "id": "uuid",
  "equipmentType": "WEAPON",
  "rarity": "EPIC",
  "level": 4,
  "baseStats": [...],
  "randomStats": [...]
}
```

#### Get My Equipment
```
GET /api/equipment/my-equipment
Headers: Authorization: Bearer <token>
Response: [EquipmentResponse, ...]
```

### Battle

#### Simulate Battle
```
POST /api/battle/simulate
Headers: Authorization: Bearer <token>
Body: {
  "heroIds": ["uuid1", "uuid2"],
  "dungeonId": "uuid"
}
Response: {
  "winner": "HEROES",
  "turns": 15,
  "logs": [
    {
      "turn": 1,
      "message": "Aria Storm attacks Goblin for 45.2 damage..."
    }
  ]
}
```

### Dungeon

#### Run Dungeon
```
POST /api/dungeon/run
Headers: Authorization: Bearer <token>
Body: {
  "dungeonId": "uuid"
}
Response: {
  "dungeonId": "uuid",
  "rewards": [
    {
      "type": "GOLD",
      "amount": 500
    },
    {
      "type": "EQUIPMENT",
      "equipmentId": "uuid"
    }
  ]
}
```

### Currency Requests (User)

#### Create Currency Request
```
POST /api/currency-requests
Headers: Authorization: Bearer <token>
Body: {
  "amount": 1000,
  "reason": "Need currency for hero rolls"
}
Response: {
  "id": "uuid",
  "amount": 1000,
  "reason": "Need currency for hero rolls",
  "status": "PENDING",
  "createdAt": "2024-01-01T00:00:00"
}
```

#### Get My Requests
```
GET /api/currency-requests/my-requests
Headers: Authorization: Bearer <token>
Response: [CurrencyRequestResponse, ...]
```

### Admin Endpoints

#### Get Pending Requests
```
GET /api/admin/currency-requests/pending
Headers: Authorization: Bearer <admin-token>
Response: [CurrencyRequestResponse, ...]
```

#### Approve Request
```
POST /api/admin/currency-requests/{requestId}/approve
Headers: Authorization: Bearer <admin-token>
Body: {
  "notes": "Approved for testing"
}
Response: CurrencyRequestResponse
```

#### Reject Request
```
POST /api/admin/currency-requests/{requestId}/reject
Headers: Authorization: Bearer <admin-token>
Body: {
  "notes": "Insufficient reason"
}
Response: CurrencyRequestResponse
```

---

## Game Systems

### Hero Generation (Gacha System)

**Process:**
1. Player calls `/api/hero/roll` (costs 100 currency)
2. System checks player has sufficient currency
3. Randomly selects HeroClass, Origin, and World templates
4. Generates unique random seed
5. Creates hero with procedurally generated stats
6. Star rarity (1-7) determined by weighted random
7. Base stats calculated from class + origin + star multiplier
8. Random variance applied (90-110%)
9. Hero saved to database

**Uniqueness Guarantees:**
- UUID for database uniqueness
- Random seed for procedural uniqueness
- Stat variance ensures no two heroes are identical

### Equipment Generation

**Process:**
1. Player calls `/api/equipment/generate`
2. Random rarity selected (weighted: COMMON 50%, RARE 30%, EPIC 15%, LEGENDARY 5%)
3. Effective level calculated from player and dungeon levels
4. Base stats generated based on equipment type
5. Random stats generated (count based on rarity)
6. Stat values scaled by level, rarity, and random variance
7. Equipment saved to database

**Rarity Rules:**
- COMMON: 1-2 random stats, 1.0x multiplier
- RARE: 2-3 random stats, 1.5x multiplier
- EPIC: 3-4 random stats, 2.5x multiplier
- LEGENDARY: 4-6 random stats, 4.0x multiplier

### Battle System

**Turn-Based Combat:**
1. Units sorted by SPEED (descending)
2. Each unit takes a turn
3. Target selection: random alive enemy
4. Damage calculation: `ATK * skillMultiplier - DEF`
5. Critical hits: if `random() < CRIT_RATE`, damage *= CRIT_DAMAGE
6. Battle continues until one team is eliminated
7. Maximum 100 turns (prevents infinite loops)

**Battle Result:**
- Winner: "HEROES", "MONSTERS", or "DRAW"
- Turn count
- Detailed log of each action

### Currency Request System

**User Flow:**
1. User creates currency request with amount and reason
2. Request status: PENDING
3. Admin reviews pending requests
4. Admin approves or rejects with notes
5. If approved, currency added to player account

**Admin Flow:**
1. Admin views all pending requests
2. Admin can approve or reject with notes
3. Status updated and player notified (via response)

---

## Database Schema

### Core Tables

#### players
```sql
CREATE TABLE players (
    player_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    currency BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### heroes
```sql
CREATE TABLE heroes (
    hero_id UUID PRIMARY KEY,
    player_id UUID REFERENCES players(player_id),
    hero_class_id UUID REFERENCES hero_classes(id),
    origin_id UUID REFERENCES origins(id),
    world_id UUID REFERENCES worlds(world_id),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    star INTEGER NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    experience BIGINT NOT NULL DEFAULT 0,
    random_seed BIGINT NOT NULL,
    equipment JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### hero_stats
```sql
CREATE TABLE hero_stats (
    id UUID PRIMARY KEY,
    hero_id UUID REFERENCES heroes(hero_id) UNIQUE,
    stat_type VARCHAR(50) NOT NULL,
    base_value DOUBLE PRECISION NOT NULL,
    final_value DOUBLE PRECISION NOT NULL
);
```

#### equipment
```sql
CREATE TABLE equipment (
    id UUID PRIMARY KEY,
    owner_id UUID REFERENCES players(player_id),
    equipment_type VARCHAR(50) NOT NULL,
    rarity VARCHAR(50) NOT NULL,
    level INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### equipment_stats
```sql
CREATE TABLE equipment_stats (
    id UUID PRIMARY KEY,
    equipment_id UUID REFERENCES equipment(id),
    stat_type VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    is_base_stat BOOLEAN NOT NULL
);
```

#### currency_requests
```sql
CREATE TABLE currency_requests (
    id UUID PRIMARY KEY,
    player_id UUID REFERENCES players(player_id) NOT NULL,
    amount BIGINT NOT NULL,
    reason VARCHAR(500),
    status VARCHAR(50) NOT NULL,
    reviewed_by UUID REFERENCES players(player_id),
    admin_notes VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### dungeons
```sql
CREATE TABLE dungeons (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    level INTEGER NOT NULL DEFAULT 1
);
```

#### monsters
```sql
CREATE TABLE monsters (
    id UUID PRIMARY KEY,
    dungeon_id UUID REFERENCES dungeons(id) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    level INTEGER NOT NULL DEFAULT 1
);
```

#### monster_stats
```sql
CREATE TABLE monster_stats (
    monster_id UUID REFERENCES monsters(id),
    stat_type VARCHAR(50),
    stat_value DOUBLE PRECISION,
    PRIMARY KEY (monster_id, stat_type)
);
```

---

## Setup & Deployment

### Prerequisites
- Java 21 JDK
- Maven 3.8+
- PostgreSQL 16+
- Docker & Docker Compose (optional)

### Local Development Setup

1. **Clone Repository**
   ```bash
   git clone <repository-url>
   cd project-lokigame
   ```

2. **Configure Database**
   Update `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/lokigame
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Create Database**
   ```sql
   CREATE DATABASE lokigame;
   ```

4. **Build Project**
   ```bash
   mvn clean install
   ```

5. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

### Docker Setup

1. **Start Services**
   ```bash
   docker-compose up --build
   ```

2. **Access Application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

### Configuration

**application.properties:**
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/lokigame
spring.datasource.username=loki
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=your-secret-key-min-256-bits
jwt.expiration=86400000  # 24 hours
jwt.refresh-expiration=604800000  # 7 days
```

---

## Development Guidelines

### Code Style
- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Keep methods focused and single-purpose
- Use meaningful variable names

### Testing
- Write unit tests for services
- Test edge cases
- Mock external dependencies
- Aim for >80% code coverage

### Security Best Practices
- Never log passwords or tokens
- Validate all user inputs
- Use parameterized queries (JPA handles this)
- Implement rate limiting (future enhancement)
- Use HTTPS in production

### Performance
- Use lazy loading for relationships
- Batch database operations
- Cache frequently accessed data
- Monitor query performance

### Error Handling
- Use custom exceptions for business logic errors
- Return appropriate HTTP status codes
- Provide meaningful error messages
- Log errors for debugging

---

## Future Enhancements

1. **Rate Limiting**: Prevent abuse of endpoints
2. **Caching**: Redis for frequently accessed data
3. **WebSocket**: Real-time battle updates
4. **Leaderboards**: Player rankings
5. **Guild System**: Multi-player features
6. **Crafting System**: Equipment enhancement
7. **Achievement System**: Player milestones
8. **Event System**: Time-limited events

---

## Support & Contact

For issues, questions, or contributions, please refer to the project repository.

---

**Last Updated**: 2024
**Version**: 1.0.0
