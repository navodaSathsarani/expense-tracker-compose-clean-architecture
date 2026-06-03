# Expense Tracker - Android Application

## Overview

A modern Android expense tracking application built with Clean Architecture and MVVM. The app allows users to track daily expenses, filter by category and date range, view category summaries, and work offline with Room as the single source of truth. Built as a tech lead assessment submission.

## Repository layout

| Path | Description |
|------|-------------|
| [`expenseTracker/`](expenseTracker/) | Android Gradle project — **open this folder in Android Studio** |
| [`ADR.md`](ADR.md) | Architecture Decision Records |
| [`.github/workflows/ci.yml`](.github/workflows/ci.yml) | GitHub Actions (unit tests + debug build) |

## Architecture

This application follows **Clean Architecture** combined with **MVVM** for separation of concerns, testability, and maintainability.

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Screen     │  │  ViewModel   │  │   UiState    │      │
│  │  (Compose)   │←→│   (Hilt)     │←→│   (Sealed)   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Use Case   │  │  Repository  │  │    Model     │      │
│  │   (Logic)    │→ │  (Interface) │  │   (Pure)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Repository   │  │  Room (DB)   │  │  Mock API    │      │
│  │     Impl     │→ │    (SSOT)    │  │  (Remote)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

- **Clean Architecture**: Domain, data, and presentation layers with clear boundaries
- **MVVM**: ViewModels + sealed `UiState`; Compose collects via `StateFlow`
- **Offline-first**: Room is the single source of truth (SSOT)
- **Swappable API**: `ExpenseApi` mirrors the REST contract (filter query params, summary endpoint)
- **Hilt**: Compile-time verified dependency injection
- **Coroutines + Flow**: Reactive data from Room to UI

## Tech Stack

| Technology | Choice | Rationale |
|------------|--------|-----------|
| **Language** | Kotlin | Null safety, coroutines, official Android language |
| **UI** | Jetpack Compose + Material 3 | Declarative UI, Material 3 |
| **Architecture** | MVVM + Clean Architecture | Testable, scalable, industry standard |
| **DI** | Hilt | Compile-time verification, ViewModel support |
| **Async** | Coroutines + Flow | Lifecycle-aware reactive streams |
| **Local DB** | Room | Offline-first SSOT, Flow queries |
| **Navigation** | Navigation Compose | Compose navigation + Hilt ViewModels |
| **Testing** | JUnit + MockK | Domain unit tests with coroutine support |

## Project Structure

```
expenseTracker/
├── app/src/main/java/com/example/expensetracker/
│   ├── data/                       # Room, mock API, mappers, repository impl
│   ├── domain/                     # Models, repository contract, use cases
│   ├── presentation/               # Compose screens, ViewModels, components
│   ├── di/                         # Hilt modules
│   ├── navigation/                 # NavGraph
│   └── ui/theme/
├── app/src/test/java/.../domain/usecase/   # Domain unit tests
├── ADR.md                          # Pointer → ../ADR.md (canonical copy at repo root)
└── build.gradle.kts
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 36 (`compileSdk`; `targetSdk` 34)
- Minimum SDK 24 (Android 7.0)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd expense-tracker-compose-clean-architecture
   ```

2. **Open in Android Studio**
   - Select **Open an Existing Project**
   - Choose the **`expenseTracker`** directory (not the repo root)
   - Wait for Gradle sync to finish

3. **Build the project**
   ```bash
   cd expenseTracker
   ./gradlew build
   ```

4. **Run the app**
   - Use an emulator or device, then **Run** in Android Studio  
   - Or: `./gradlew installDebug`

5. **Run tests**
   ```bash
   cd expenseTracker
   ./gradlew test
   ```

## Assumptions Made

### Product and scope

| Ambiguity | Decision | Rationale |
|-----------|----------|-----------|
| **Assessment time box (~2–4 hours)** | Ship a complete **Android-only** app with docs and domain tests | Meets brief deliverables; KMP multi-module setup is a separate project phase |
| **Platforms in the brief** | Android target only for this submission | Brief does not require iOS/desktop; domain is written in pure Kotlin to ease a later KMP extract |
| **Single user / device** | No accounts, no multi-device sync semantics | Simplifies conflict model and auth |
| **Currency not specified** | Single currency (LKR) in UI; `currency` on domain model | Multi-currency UI deferred |
| **Filter semantics (unspecified)** | OR when category **and** date range are both active; otherwise single-dimension filter | Documented in `FilterExpensesUseCase`; would confirm with PM in production |

### Technical

| Ambiguity | Decision | Rationale |
|-----------|----------|-----------|
| **No real backend** | Mock API (~1000 ms delay) behind `ExpenseApi` | Exercises loading/error UX; contract matches filter + summary query params for Retrofit/Ktor later |
| **Offline conflict resolution** | Last-write-wins on refresh/insert | Acceptable for single-user; not suitable for collaborative editing without server timestamps |
| **Local persistence** | Room on Android as SSOT | Best Jetpack fit today; KMP path would use SQLDelight or Room KMP in a `shared` data layer |
| **DI** | Hilt (Android) | Fast, compile-safe on Android; shared KMP code would use constructor injection + Koin or manual graph |
| **Pagination** | Not implemented; `LazyColumn` uses stable keys | `PagingSource` when list size grows |
| **Delete confirmation** | None | Speed of implementation; listed under “with more time” |
| **Error retry** | User-initiated retry on error state | No exponential backoff / WorkManager queue in scope |
| **UI copy** | `res/values/strings.xml` + `stringResource()` | Android localization; KMP would move to Compose Resources or `shared` string APIs |
| **Kotlin Multiplatform** | **Not used in this repo** | Would add `:shared`, expect/actual, and CI matrix; chosen to prioritize feature completeness and ADR/test quality within the time box |

## Testing Strategy

### What Was Tested

Domain use cases (`expenseTracker/app/src/test/.../domain/usecase/`) — **20 unit tests**:

- `AddExpenseUseCase` — validation (amount > 0)
- `DeleteExpenseUseCase` — repository interaction
- `FilterExpensesUseCase` — category, date range, OR logic
- `GetSummaryUseCase` — percentages, sorting, edge cases
- `RefreshExpensesUseCase` — sync invokes repository

### What Was Not Tested (and Why)

- ViewModels, repository integration, UI/Compose tests — time-boxed to domain layer

## Architecture Decision Records

See **[ADR.md](ADR.md)** for decision records:

- ADR-001: MVVM + Clean Architecture
- ADR-002: Room offline-first SSOT
- ADR-003: Hilt for DI
- ADR-004: Android-only for assessment; KMP deferred (migration plan in README below)

## CI

GitHub Actions on push/PR to `main` ([`.github/workflows/ci.yml`](.github/workflows/ci.yml)):

- `./gradlew test`
- `./gradlew assembleDebug`

## AI Usage Disclosure

- **Cursor**: Primary tool for implementation, refactoring, and documentation
- **Claude (claude.ai)**: Early scaffolding for domain models, UI, and ADRs

All AI-generated code was reviewed and understood. I can explain architectural decisions and implementation choices.

**AI Transcript**: Add your Cursor export link or transcript file here before submission.

## Known Limitations

1. No delete confirmation dialog
2. No edit expense flow
3. No search
4. Single currency (LKR) in UI
5. Limited network error handling on refresh
6. No export (CSV/PDF)
7. No authentication
8. Test coverage limited to domain layer

## What I Would Do Differently With More Time

This submission is **native Android** on purpose for the assessment window. With a longer timeline (or a “build for Android + iOS” product mandate), I would evolve the same Clean Architecture boundaries using **Kotlin Multiplatform (KMP)** rather than bolting on a second codebase.

### 1. Kotlin Multiplatform module layout

```
:shared (commonMain)
  ├── domain/          # Expense, Category, use cases (already pure Kotlin today)
  ├── data/            # Repository interfaces, DTOs, mappers
  └── di/              # Expect declarations / factory interfaces

:shared (androidMain / iosMain)
  └── data/            # Platform DB, HTTP client, date/locale helpers (actual)

:androidApp            # Compose UI, Hilt, Room or SQLDelight driver
:iosApp (optional)     # SwiftUI or Compose Multiplatform UI
```

**Share in `commonMain`:** domain models, all use cases (`AddExpense`, `FilterExpenses`, `GetSummary`, `RefreshExpenses`), repository contracts, filter/summary rules, and **common unit tests** (`commonTest` with kotlinx-coroutines-test).

**Keep platform-specific:** persistence (Room today → **SQLDelight** or **Room KMP** in `androidMain`/`iosMain`), networking (**Ktor** client with shared serializers), secure storage, and UI toolkits.

**Why not KMP here:** Gradle structure, expect/actual for DB/network, and dual-platform CI typically need **days**, not hours. The current `domain` package is deliberately framework-free so it can move into `:shared` with minimal rewrites.

### 2. UI strategy on KMP

| Approach | When I’d choose it |
|----------|-------------------|
| **Compose Multiplatform** | One UI codebase for Android + desktop; team already strong in Compose |
| **Shared ViewModels + native UI** | iOS uses SwiftUI; Android keeps Compose; shared presentation logic via KMP ViewModel pattern or MVI in `commonMain` |
| **This assessment** | Android Compose only — fastest path to a polished Material 3 app |

### 3. Data and sync (beyond the mock)

- Replace `MockExpenseDataSource` with **Ktor** (shared) + platform TLS/logging
- **WorkManager** (Android) / background tasks (iOS) for retryable sync queue
- Server-authoritative timestamps and explicit conflict policy (not last-write-wins)
- **Paging** (`Paging3` / shared paging abstraction) for large expense lists

### 4. Testing and quality

- **`commonTest`**: all use case tests run on JVM without Robolectric
- **Android**: ViewModel tests, Room integration tests, Compose UI tests (Navigation + semantics)
- **iOS**: XCTest against shared framework if iOS target exists
- **CI matrix**: `shared` JVM tests + `androidApp` instrumented/unit + optional `iosSimulatorArm64` link

### 5. Product polish (same priorities on any platform)

- Edit expense, delete confirmation, search
- Multi-currency formatting (`kotlinx-datetime` + locale APIs per platform)
- Charts/analytics screen
- Accessibility audit (TalkBack / VoiceOver) and performance profiling (Macrobenchmark)

### 6. Documentation

- **ADR-004**: KMP boundary (what lives in `commonMain` vs `actual`)
- OpenAPI-backed API contract checked into repo for client generation

See **[ADR.md](ADR.md)** — ADR-001 already notes the domain layer is structured for eventual KMP sharing; this section is the concrete migration plan.

## Future Improvements (near term, Android-only)

- Pagination and search
- Edit expense and delete confirmation
- Multi-currency UI
- Retrofit or Ktor client replacing mock datasource
- ViewModel and Compose UI tests
- Charts for spending analytics

## Performance Considerations

- `LazyColumn` with stable `key = { it.id }`
- Summary computed in domain layer (`CategorySummaryCalculator` / `GetSummaryUseCase`)
- Room `Flow` + `collectAsStateWithLifecycle`
- User-facing strings in `strings.xml`

## License

Technical assessment submission. All rights reserved.
