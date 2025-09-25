# Git Branch Naming Convention

This convention uses a **three-part structure** to immediately tell us **what** is being done and **where** in the codebase it's happening.

$$\text{type} / \text{area} / \text{description}$$

**Example:** `feat/backend-auth/add-jwt-token-hashing`

---

## 1. Type of Change ($\text{type}$)

This prefix defines the **intention** of the work.

| Prefix | Category | Purpose |
| :--- | :--- | :--- |
| **`feat`** | **Feature** | Adding a **new feature** or major user-facing functionality. |
| **`fix`** | **Bug Fix** | Correcting a bug or incorrect behavior. |
| **`refactor`** | **Restructure** | Code changes that don't fix a bug or add a feature (e.g., rewriting logic). |
| **`chore`** | **Maintenance** | Routine tasks not affecting source code (e.g., dependency updates, minor config). |
| **`docs`** | **Documentation** | Changes only to documentation files (READMEs, inline comments). |
| **`test`** | **Testing** | Adding or correcting tests only (unit, integration, E2E). |
| **`hotfix`** | **Urgent Fix** | For critical, immediate production issues. |

---

## 2. Project Area ($\text{area}$)

This is crucial for our full-stack project, combining the **stack** (`frontend` or `backend`) with the specific **module** being modified.

### 🌐 Frontend Areas (Prefix: `frontend-`)

Used for changes within the client-side application directory (e.g., React components, styles).

| Area Name | Focus/Module | Example of Use |
| :--- | :--- | :--- |
| **`frontend-auth`** | Login forms, registration, client-side session storage. | `feat/frontend-auth/add-forgot-password-link` |
| **`frontend-ui`** | Generic components, global styles, themes. | `refactor/frontend-ui/use-css-variables` |
| **`frontend-pages`** | Specific application pages (e.g., home, dashboard, cart). | `fix/frontend-pages/correct-dashboard-layout` |
| **`frontend-deps`** | Frontend package updates, build configurations. | `chore/frontend-deps/update-react-router` |

### ⚙️ Backend Areas (Prefix: `backend-`)

Used for changes within the server-side application directory (e.g., API routes, database logic).

| Area Name | Focus/Module | Example of Use |
| :--- | :--- | :--- |
| **`backend-auth`** | API routes and logic for authentication, tokens, and authorization. | `feat/backend-auth/implement-rate-limiting` |
| **`backend-db`** | Database migrations, ORM configuration, and schema changes. | `fix/backend-db/fix-foreign-key-constraint` |
| **`backend-api`** | Specific API endpoints, controllers, and services (e.g., product, order, user). | `feat/backend-api/add-product-search-endpoint` |
| **`infra`** | DevOps, cloud configurations, Docker, or server settings. | `chore/infra/setup-log-rotation` |

---

## 3. Description ($\text{description}$)

A short, precise summary of the work.

* **Format:** Must be **lowercase** and use **kebab-case** (hyphens: `-`).
* **Rule:** Keep it concise (3-5 words max).

| ✅ Good Example | ❌ Avoid |
| :--- | :--- |
| `add-user-input-validation` | `adding_user_input_validation_to_the_form` |
| `simplify-token-logic` | `SimplifyTokenLogic` |

### Final Examples

| Purpose | Branch Name |
| :--- | :--- |
| **Feature: User Profile** | `feat/backend-api/add-user-profile-endpoint` |
| **Fix: Dashboard Bug** | `fix/frontend-pages/correct-dashboard-table-sorting` |
| **Refactor: Backend Code** | `refactor/backend-api/move-logging-logic-to-interceptor` |
| **Chore: Backend Dependency** | `chore/backend-deps/update-spring-boot-to-v3` |
| **Test: User Service** | `test/backend-auth/add-unit-tests-for-user-service` |
| **Hotfix: Homepage UI** | `hotfix/frontend-pages/disable-broken-hero-carousel` |
| **Documentation: API** | `docs/backend-api/update-swagger-for-product-api` |
| **Feature: Cart Functionality** | `feat/frontend-pages/implement-checkout-page-logic` |
| **Maintenance: Infrastructure** | `chore/infra/update-dockerfile-for-new-jdk` |
| **Refactor: UI Component** | `refactor/frontend-ui/extract-card-component-from-list` |
