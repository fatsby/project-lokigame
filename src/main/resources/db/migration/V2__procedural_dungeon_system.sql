-- Procedural Dungeon System Migration
-- Creates monster_templates, dungeon_seeds tables
-- Adds highest_cleared_level to players
-- Drops old static dungeon tables

-- 1. Create monster_templates table
CREATE TABLE monster_templates (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500)
);

-- 2. Create monster_template_base_stats table  
CREATE TABLE monster_template_base_stats (
    template_id UUID NOT NULL REFERENCES monster_templates(id) ON DELETE CASCADE,
    stat_type VARCHAR(50) NOT NULL,
    stat_value DOUBLE PRECISION,
    PRIMARY KEY (template_id, stat_type)
);

-- 3. Create monster_template_stat_growth table
CREATE TABLE monster_template_stat_growth (
    template_id UUID NOT NULL REFERENCES monster_templates(id) ON DELETE CASCADE,
    stat_type VARCHAR(50) NOT NULL,
    growth_value DOUBLE PRECISION,
    PRIMARY KEY (template_id, stat_type)
);

-- 4. Create dungeon_seeds table
CREATE TABLE dungeon_seeds (
    id UUID PRIMARY KEY,
    player_id UUID NOT NULL,
    world_id UUID NOT NULL,
    dungeon_level INTEGER NOT NULL,
    seed BIGINT NOT NULL,
    cleared BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP,
    modified_by VARCHAR(255),
    CONSTRAINT uk_dungeon_seed_player_world_level UNIQUE (player_id, world_id, dungeon_level)
);

-- 5. Add highest_cleared_level to players
ALTER TABLE players ADD COLUMN IF NOT EXISTS highest_cleared_level INTEGER NOT NULL DEFAULT 0;

-- 6. Drop old static dungeon tables (if they exist)
DROP TABLE IF EXISTS drop_tables CASCADE;
DROP TABLE IF EXISTS monsters CASCADE;
DROP TABLE IF EXISTS dungeons CASCADE;
