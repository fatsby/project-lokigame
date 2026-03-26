# For Mr Thao

<br>
Youtube: https://youtu.be/BDUaxafeukU
<br>
Slides: https://www.canva.com/design/DAHDn_4JpcI/UJ2SpT7Wdjp6hXQYD2zqvg/edit?utm_content=DAHDn_4JpcI&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

# 🎮 LokiGame - Game Data & Balance Statistics

This document contains the core game data, base stats, and growth scaling for all entities within the LokiGame engine. This data is initialized on the first boot via `GameDataInitialize.java`.
---

## 🛡️ Hero Classes

Heroes are categorized into specific classes that define their combat role, base stats, and how they scale as they level up.

### Base Statistics & Modifiers

| Class | HP | ATK | DEF | SPD | Crit Rate | Crit DMG | Special Modifiers |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Warrior** | 150.0 | 30.0 | 20.0 | 40.0 | 5.0% | 1.2x | +20% DEF, +15% HP |
| **Mage** | 80.0 | 50.0 | 10.0 | 60.0 | 15.0% | 1.35x | +30% ATK, +10% Crit Rate |
| **Rogue** | 100.0 | 40.0 | 15.0 | 80.0 | 25.0% | 1.5x | +25% SPD, +20% Crit Rate |

### Stat Growth (Per Level)

| Class | HP | ATK | DEF | SPD | Crit Rate | Crit DMG |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Warrior** | +8.0 | +2.0 | +3.0 | +0.5 | +0.1% | +0.02 |
| **Mage** | +3.0 | +5.0 | +0.5 | +1.0 | +0.3% | +0.05 |
| **Rogue** | +4.0 | +3.5 | +1.0 | +2.0 | +0.5% | +0.08 |

---

## 🧬 Origins (Races)

Origins provide percentage-based multipliers to a hero's final statistics.

| Origin | Modifiers | Description |
| :--- | :--- | :--- |
| **Human** | +10% HP, +5% ATK | Balanced race with moderate bonuses. |
| **Elf** | +20% SPD, +10% Crit Rate | Swift and agile race. |
| **Dwarf** | +25% DEF, +15% HP | Sturdy and defensive race. |

---

## 🌌 Worlds

Worlds determine the rarity and difficulty of the dungeons, as well as the power level of the gear found within.

| World | Rarity Weight | Stat Multiplier | Difficulty Mod | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Aetheria** | 50.0% | 1.0x | 1.0x | A mystical realm of magic and wonder. |
| **Shadowlands** | 30.0% | 1.2x | 1.2x | A dark realm of chaos and danger. |
| **Celestia** | 20.0% | 1.5x | 1.5x | A heavenly realm of light and purity. |

---

## 👹 Monster Templates

Monster stats scale according to the dungeon level. These templates define their base potential.

### Base Statistics

| Monster | HP | ATK | DEF | SPD | Crit Rate | Crit DMG |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Goblin** | 100.0 | 40.0 | 5.0 | 80.0 | 10.0% | 1.5x |
| **Orc Warrior** | 250.0 | 75.0 | 15.0 | 40.0 | 5.0% | 1.8x |
| **Skeleton Archer** | 80.0 | 90.0 | 3.0 | 60.0 | 20.0% | 2.0x |
| **Forest Troll** | 400.0 | 65.0 | 25.0 | 20.0 | 2.0% | 1.5x |
| **Dark Mage** | 120.0 | 85.0 | 5.0 | 50.0 | 15.0% | 2.2x |

### Scaling (Per Monster Level)

| Monster | HP | ATK | DEF | SPD | Crit Rate | Crit DMG |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Goblin** | +35.0 | +15.0 | +2.0 | +3.0 | +0.5% | +0.02 |
| **Orc Warrior** | +50.0 | +20.0 | +5.0 | +1.0 | +0.3% | +0.03 |
| **Skeleton Archer** | +20.0 | +30.0 | +1.0 | +2.0 | +0.8% | +0.05 |
| **Forest Troll** | +100.0 | +15.0 | +15.0 | +0.5 | +0.1% | +0.01 |
| **Dark Mage** | +30.0 | +25.0 | +1.5 | +2.0 | +0.6% | +0.06 |

---

## 🏷️ Naming Systems

The game uses a procedural naming system for heroes and legendary items.

### Hero Names
*   **Female**: Aria, Luna, Zara, Nova, Ivy
*   **Male**: Kael, Thorin, Drake, Rex, Orion
*   **Surnames**: Thatcher, Blackwood, Beaumont, Sterling, Hawthorne, Garrick, Barlow, Miller, Valerius, Crowe, Hardy, Vance, Mordecai, Pendleton, Davenport, Ridley, Stallard, Granger

### Equipment Prefixes & Unique Names
*   **Godsent (Legendary)**: *King Arthur's*, *Asgardian Glory*, *Hale's Own*
*   **Common Prefixes**: Cursed, Crooked, Hallowed, Seraphic, Primordial, Malevolent, Abyssal, Blighted, Ethereal, Sanctified
