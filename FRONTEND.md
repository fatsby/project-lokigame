# FRONTEND.md - Front-End Architecture & Technology Stack

## 1. Overview

**Purpose:** A visual-only front-end client that renders server-calculated game state, including battle animations, hero summoning effects, and UI interactions.

**Philosophy:** The front-end is a **"dumb" visualizer**. All game logic (RNG, battle outcomes, loot generation) is computed server-side. The client receives deterministic results and plays them back visually.

**UI Theme:** Medieval Fantasy with rich textures, parchment-style elements, and sword & sorcery aesthetics.

---

## 2. Technology Stack

### 2.1 Core Framework

| Layer | Technology | Rationale |
|-------|------------|-----------|
| **Framework** | **Next.js 14+** (App Router) | Server-side rendering for SEO (landing pages), excellent TypeScript support, API route proxying, and built-in optimization. Aligns with your existing knowledge. |
| **Language** | **TypeScript** | Type safety for complex game state, hero definitions, and API contracts. Reduces runtime errors. |
| **Styling** | **CSS Modules + CSS Variables** | Scoped styling with a medieval design system using CSS custom properties for theming. Optional: Tailwind CSS if preferred. |

### 2.2 Animation & Graphics

| Library | Purpose | Rationale |
|---------|---------|-----------|
| **Framer Motion** | UI Animations | Declarative animations for page transitions, hero card reveals, and UI micro-interactions. React-native integration. |
| **GSAP (GreenSock)** | Battle Animations | Industry-standard for complex timeline animations. Precise control over sequenced battle events (attacks, damage, effects). |
| **Lottie React** | Vector Animations | For lightweight, scalable visual effects (spell particles, summoning circles, rarity glows). Exported from After Effects. |
| **(Optional) PixiJS** | 2D Canvas Rendering | If battle scenes require intensive sprite-based rendering with many simultaneous effects. |

### 2.3 State Management & Data Fetching

| Library | Purpose | Rationale |
|---------|---------|-----------|
| **TanStack Query (React Query)** | Server State | Caching, background refetch, optimistic updates. Perfect for API-driven game state (heroes, inventory). |
| **Zustand** | Client State | Lightweight global state for UI-only concerns (modals, animation playback state, theme). No Redux boilerplate. |
| **Axios** | HTTP Client | Interceptors for JWT token injection, request/response typing, error handling. |

### 2.4 Authentication

| Library | Purpose | Rationale |
|---------|---------|-----------|
| **NextAuth.js** or Custom JWT | Auth Flow | Handles JWT token storage, refresh logic, and protected route guards. Integrates with your Spring Security backend. |

### 2.5 Development Tools

| Tool | Purpose |
|------|---------|
| **ESLint + Prettier** | Code quality and consistent formatting |
| **Storybook** | Component development in isolation (hero cards, buttons, modals) |
| **Vitest** | Unit testing for utilities and hooks |
| **Playwright** | E2E testing for critical flows (login, summoning) |

---

## 3. Architecture Pattern

### 3.1 Visualization-Only Client

```
┌─────────────────────────────────────────────────────────────────┐
│                        BACKEND (Spring Boot)                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │ Auth (JWT)  │  │ Hero Summon │  │ Battle Simulation       │  │
│  │             │  │ (RNG Logic) │  │ (Deterministic Results) │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │ REST API (JSON)
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                     FRONTEND (Next.js)                          │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    API Client Layer                      │   │
│  │  • TanStack Query for caching & fetching                │   │
│  │  • Axios with JWT interceptors                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                   State Management                       │   │
│  │  • Server State: React Query (heroes, inventory)        │   │
│  │  • UI State: Zustand (modals, animation controls)       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                 Visualization Layer                      │   │
│  │  • React Components (UI)                                │   │
│  │  • Framer Motion (UI transitions)                       │   │
│  │  • GSAP (Battle timeline playback)                      │   │
│  │  • Lottie (VFX: particles, glows)                       │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Key Principle: Server-Authoritative

| Concern | Server (Backend) | Client (Frontend) |
|---------|------------------|-------------------|
| Battle Logic | Calculates all outcomes | Receives result, plays animation |
| Hero Stats | Generates, validates, stores | Displays only (read-only) |
| RNG | Seeded, deterministic | Replays visually using seed |
| Inventory | Validates ownership & equips | Shows UI, sends requests |

---

## 4. Phase 1 Goals: Authentication & Hero Summoning

### 4.1 Authentication UI

#### Screens

| Screen | Description | Animations |
|--------|-------------|------------|
| **Login Page** | Email/password form with medieval styling | Parchment fade-in, quill cursor effects |
| **Register Page** | Account creation form | Similar medieval transitions |
| **Auth Guard** | Redirect to login if unauthenticated | Fade transition |

#### Technical Implementation

```
Authentication Flow:
1. User submits credentials
2. Backend returns { accessToken, refreshToken }
3. Store tokens securely (httpOnly cookie preferred, or localStorage with encryption)
4. Axios interceptor adds `Authorization: Bearer <token>` to all requests
5. On 401 response, attempt token refresh or redirect to login
```

#### Key Components

- `<LoginForm />` - Email, password inputs with validation
- `<RegisterForm />` - Account creation
- `<AuthProvider />` - Context provider for auth state
- `<ProtectedRoute />` - Route guard HOC

---

### 4.2 Hero Summoning UI

#### The Summoning Experience

This is the core gacha experience and must feel **magical and rewarding**.

```
Summoning Flow:
1. Player clicks "Summon" button
2. Show anticipation animation (portal opening, magic circle spinning)
3. Send POST /api/v1/heroes/summon to backend
4. Backend returns generated Hero object
5. Play reveal animation based on hero rarity
6. Display hero card with stats and visuals
7. Hero added to collection (React Query invalidation)
```

#### Rarity-Based Animations

| Rarity | Animation Style | Duration |
|--------|-----------------|----------|
| ⭐ (1-Star) | Simple glow, fast reveal | 1.5s |
| ⭐⭐ (2-Star) | Gentle sparkles | 2s |
| ⭐⭐⭐ (3-Star) | Moderate light burst | 2.5s |
| ⭐⭐⭐⭐ (4-Star) | Golden rays, screen shake | 3s |
| ⭐⭐⭐⭐⭐ (5-Star) | Rainbow burst, dramatic slowdown | 4s |
| ⭐⭐⭐⭐⭐⭐ (6-Star) | Celestial explosion, full-screen effects | 5s |
| ⭐⭐⭐⭐⭐⭐⭐ (7-Star) | Legendary cutscene-style reveal, world-origin showcase | 6-8s |

#### Key Components

- `<SummonPortal />` - The interactive summoning circle
- `<SummonAnimation />` - Pre-reveal anticipation animation
- `<HeroReveal />` - Rarity-appropriate reveal animation
- `<HeroCard />` - Displays hero portrait, stats, class, world origin
- `<HeroCollection />` - Grid/list of owned heroes

#### World Origin Display

Each hero has a `worldOrigin` from `worlds.json`. Display this prominently:

- **Midgard**: Earth tones, human realm aesthetic
- **Niflheim**: Ice blue, frozen mist effects
- **Asgard**: Golden divine glow, celestial backdrop

---

## 5. Medieval UI/UX Design System

### 5.1 Color Palette

```css
:root {
  /* Primary Colors */
  --color-parchment: #f4e4bc;
  --color-ink-dark: #2c1810;
  --color-ink-light: #5c4033;
  
  /* Accent Colors */
  --color-gold: #d4af37;
  --color-bronze: #cd7f32;
  --color-crimson: #8b0000;
  --color-forest: #228b22;
  
  /* Rarity Colors */
  --rarity-common: #9ca3af;
  --rarity-uncommon: #22c55e;
  --rarity-rare: #3b82f6;
  --rarity-epic: #a855f7;
  --rarity-legendary: #f59e0b;
  --rarity-mythic: #ef4444;
  --rarity-divine: linear-gradient(90deg, #ff6b6b, #ffd93d, #6bcb77, #4d96ff, #c471ed);
  
  /* UI Elements */
  --color-panel-bg: rgba(44, 24, 16, 0.9);
  --color-border-dark: #1a0f0a;
  --color-border-gold: #b8860b;
}
```

### 5.2 Typography

```css
/* Medieval-inspired fonts */
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;700&family=Crimson+Text:ital,wght@0,400;0,600;1,400&display=swap');

:root {
  --font-display: 'Cinzel', serif;       /* Headers, titles */
  --font-body: 'Crimson+Text', serif;   /* Body text, descriptions */
  --font-ui: 'Inter', sans-serif;       /* Numbers, stats, UI labels */
}
```

### 5.3 UI Component Styles

| Component | Medieval Treatment |
|-----------|-------------------|
| **Buttons** | Wooden plank texture, iron nail accents, hover glow |
| **Cards** | Parchment background, torn edges, wax seal decorations |
| **Modals** | Stone frame borders, torch-lit shadows |
| **Inputs** | Scroll-style text fields, quill cursor |
| **Panels** | Dark wood or stone texture, riveted metal corners |
| **Borders** | Celtic knot patterns, ornate corners |

### 5.4 Iconography

- Use pixel-art or hand-drawn style icons
- Classes: Swords (Warrior), Staff (Mage), Shield (Paladin), etc.
- UI: Scroll (Inventory), Crossed Swords (Battle), Portal (Summon)

---

## 6. Animation Strategy

### 6.1 Animation Library Responsibilities

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANIMATION ARCHITECTURE                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  FRAMER MOTION (React Integration)                              │
│  ├── Page transitions (fade, slide)                            │
│  ├── Component mount/unmount animations                         │
│  ├── Hover states and micro-interactions                        │
│  └── Layout animations (hero grid reordering)                   │
│                                                                 │
│  GSAP (Complex Timelines)                                       │
│  ├── Battle sequence playback                                   │
│  ├── Multi-step summoning animations                            │
│  ├── Synced audio-visual events                                 │
│  └── Precise timing control for reveals                         │
│                                                                 │
│  LOTTIE (Vector Effects)                                        │
│  ├── Particle effects (sparkles, flames)                        │
│  ├── Magic circles and runes                                    │
│  ├── Stat increase indicators                                   │
│  └── Loading spinners (medieval themed)                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 Battle Visualization (Future Phase)

When battles are implemented, the flow will be:

```
1. Backend simulates battle with seeded RNG
2. Backend returns BattleResult object:
   {
     seed: 12345,
     events: [
       { turn: 1, actor: "hero_uuid", action: "ATTACK", target: "enemy_1", damage: 45 },
       { turn: 1, actor: "enemy_1", action: "DEFEND", ... },
       ...
     ],
     outcome: "VICTORY"
   }
3. Frontend receives result
4. GSAP timeline plays each event sequentially:
   - Move hero sprite
   - Play attack animation
   - Show damage number
   - Update health bar
5. Show victory/defeat screen
```

---

## 7. API Integration

### 7.1 API Client Structure

```typescript
// Example: src/lib/api/client.ts

import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// JWT Token Interceptor
apiClient.interceptors.request.use((config) => {
  const token = getAccessToken(); // From auth store
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 401 Handler
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Attempt token refresh or redirect to login
    }
    return Promise.reject(error);
  }
);
```

### 7.2 API Endpoints (Phase 1)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/auth/login` | POST | Authenticate, receive tokens |
| `/api/v1/auth/register` | POST | Create new account |
| `/api/v1/auth/refresh` | POST | Refresh access token |
| `/api/v1/heroes/summon` | POST | Summon a new hero |
| `/api/v1/heroes` | GET | List player's heroes |
| `/api/v1/heroes/{id}` | GET | Get hero details |

### 7.3 React Query Hooks

```typescript
// Example: src/hooks/useHeroes.ts

export function useHeroes() {
  return useQuery({
    queryKey: ['heroes'],
    queryFn: () => apiClient.get('/heroes').then(res => res.data),
  });
}

export function useSummonHero() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: () => apiClient.post('/heroes/summon'),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['heroes'] });
    },
  });
}
```

---

## 8. Project Structure

```
lokigame-frontend/
├── public/
│   ├── assets/
│   │   ├── images/          # Static images (backgrounds, icons)
│   │   ├── lottie/          # Lottie JSON animations
│   │   └── audio/           # Sound effects, music
│   └── fonts/
│
├── src/
│   ├── app/                  # Next.js App Router
│   │   ├── (auth)/          # Auth route group
│   │   │   ├── login/
│   │   │   └── register/
│   │   ├── (game)/          # Protected game routes
│   │   │   ├── summon/
│   │   │   ├── heroes/
│   │   │   └── inventory/
│   │   ├── layout.tsx
│   │   └── page.tsx         # Landing page
│   │
│   ├── components/
│   │   ├── ui/              # Base UI components
│   │   │   ├── Button/
│   │   │   ├── Card/
│   │   │   ├── Modal/
│   │   │   └── Input/
│   │   ├── hero/            # Hero-related components
│   │   │   ├── HeroCard/
│   │   │   ├── HeroReveal/
│   │   │   └── HeroStats/
│   │   ├── summon/          # Summoning components
│   │   │   ├── SummonPortal/
│   │   │   └── SummonAnimation/
│   │   └── layout/          # Layout components
│   │       ├── Navbar/
│   │       └── Sidebar/
│   │
│   ├── hooks/               # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useHeroes.ts
│   │   └── useSummon.ts
│   │
│   ├── lib/                 # Utilities and configurations
│   │   ├── api/
│   │   │   ├── client.ts
│   │   │   └── endpoints.ts
│   │   ├── animations/      # GSAP timelines
│   │   └── utils/
│   │
│   ├── stores/              # Zustand stores
│   │   ├── authStore.ts
│   │   └── uiStore.ts
│   │
│   ├── styles/              # Global styles
│   │   ├── globals.css
│   │   ├── variables.css
│   │   └── medieval-theme.css
│   │
│   └── types/               # TypeScript types
│       ├── hero.ts
│       ├── auth.ts
│       └── api.ts
│
├── .env.local               # Environment variables
├── next.config.js
├── package.json
└── tsconfig.json
```

---

## 9. Development Workflow

### 9.1 Getting Started

```bash
# Create Next.js project
npx create-next-app@latest lokigame-frontend --typescript --tailwind --app --src-dir

# Install dependencies
npm install @tanstack/react-query zustand axios framer-motion
npm install gsap lottie-react
npm install -D vitest @testing-library/react playwright

# Run development server
npm run dev
```

### 9.2 Environment Variables

```env
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

### 9.3 Development Phases

| Phase | Focus | Deliverables |
|-------|-------|--------------|
| **1.1** | Auth UI | Login, Register, Token handling |
| **1.2** | Basic Layout | Navbar, medieval theme, responsive design |
| **1.3** | Hero Summoning | Summon button, basic reveal |
| **1.4** | Animations | Full summoning experience with rarity effects |
| **1.5** | Hero Collection | Display owned heroes, hero details |

---

## 10. Future Considerations (Post Phase 1)

- **Battle Visualization**: GSAP-powered replay of server-simulated battles
- **Inventory UI**: Equipment management with drag-and-drop
- **Real-time Updates**: WebSocket for multiplayer features
- **Offline PWA**: Service workers for asset caching
- **Localization**: i18n support for multiple languages
- **Accessibility**: WCAG compliance, screen reader support

---

## 11. Summary

| Aspect | Decision |
|--------|----------|
| **Framework** | Next.js 14+ with App Router |
| **Language** | TypeScript |
| **State** | TanStack Query (server) + Zustand (UI) |
| **Animations** | Framer Motion + GSAP + Lottie |
| **Styling** | CSS Modules with medieval design system |
| **Auth** | JWT with Axios interceptors |
| **Testing** | Vitest + Playwright |

This stack leverages your existing React/Next.js expertise while providing the animation capabilities needed for an engaging gacha experience. The medieval theme can be achieved through careful CSS design, custom fonts, and themed assets.
