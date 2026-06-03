# Architecture Decision Records (ADR)

This document records the key architectural decisions made during the development of the Expense Tracker application, including the context, alternatives considered, and consequences of each decision.

---

## ADR-001: MVVM + Clean Architecture

**Status:** Accepted

**Date:** 2026-06-03

### Context

The application needed a clear architectural pattern that would support:
- Separation of concerns between UI, business logic, and data access
- Testability of business logic without Android framework dependencies
- Scalability for potential future features (multi-user, cloud sync, complex analytics)
- Industry-standard patterns that future developers would recognize

**Platform:** Native Android with Kotlin — chosen because Room, Compose, Hilt, and Flow are first-class on this stack and align with offline-first assessment requirements.

The choice was between:
- Simple MVVM (ViewModel + Repository)
- MVVM + Clean Architecture (Domain + Data + Presentation layers)
- MVI (Model-View-Intent) pattern

### Decision

Implement **MVVM + Clean Architecture** with three distinct layers:
- **Presentation Layer**: Compose UI + ViewModels + UiState
- **Domain Layer**: Use Cases + Repository Interfaces + Domain Models (pure Kotlin)
- **Data Layer**: Repository Implementations + Room + Mock API + Mappers

### Alternatives Considered

**1. Simple MVVM (ViewModel → Repository → Database)**
- **Pros**: Less boilerplate, faster initial development, sufficient for small apps
- **Cons**: Business logic mixed with data access, harder to test, poor scalability
- **Why Rejected**: Doesn't demonstrate architectural thinking for a lead-level assessment

**2. MVI (Model-View-Intent)**
- **Pros**: Unidirectional data flow, predictable state management, great for complex UI state
- **Cons**: Steeper learning curve, more boilerplate, overkill for simple CRUD operations
- **Why Rejected**: Unnecessary complexity for this app's requirements, would slow development

### Consequences

**What Becomes Easier:**
- **Testing**: Domain use cases are pure Kotlin functions, easy to test without Android framework
- **Maintenance**: Clear boundaries between layers make it obvious where to add new features
- **Scalability**: Adding new data sources (e.g., real REST API) only requires changing Data layer
- **Team Collaboration**: Standard pattern that any Android developer would understand
- **Code Reuse**: Domain layer is pure Kotlin and could be shared via KMP later; this submission is Android-only

**What Becomes Harder:**
- **Initial Setup**: More files and folders to create upfront (10+ files vs 3-4 in simple MVVM)
- **Boilerplate**: Requires mappers between Entity/DTO/Domain models (3x the data classes)
- **Learning Curve**: Junior developers need to understand layer boundaries and data flow
- **Over-Engineering Risk**: Clean Architecture is overkill for a single-feature CRUD app
- **Layer Leakage**: Resolved via `RefreshExpensesUseCase`; list refresh no longer calls repository directly from ViewModel

### Honest Assessment

**This is intentionally over-engineered for the current requirements.** A simple ViewModel → Repository → Room architecture would be sufficient for this app's scope. However, this decision demonstrates:
- Ability to architect for scale (not just current requirements)
- Understanding of enterprise Android patterns
- Forward-thinking about future requirements (multi-user, cloud sync, analytics)

In a real-world scenario with a 3-month timeline and team of 3-5 developers, this architecture would pay dividends. For a solo 4-hour assessment, it's demonstrative rather than pragmatic.

---

## ADR-002: Room as Offline-First Single Source of Truth

**Status:** Accepted

**Date:** 2026-06-03

### Context

The application needed a local data persistence strategy that would:
- Support offline-first operation (users can add expenses without internet)
- Provide reactive data updates (UI updates automatically when data changes)
- Serve as a reliable cache for remote data
- Handle potential future synchronization with a backend

The choice was between:
- Room database as SSOT with API sync
- In-memory-only storage
- SharedPreferences for small payloads
- Direct API calls with Room as secondary cache

### Decision

Implement **Room as the Single Source of Truth (SSOT)** with offline-first architecture:
- All UI reads from Room via Flow (reactive)
- Writes go to Room first, then sync to API (fire-and-forget)
- API refreshes populate Room, UI automatically updates
- Room survives app restarts, in-memory cache does not
- **Conflict resolution:** last-write-wins (documented limitation for single-user scope)
- **Spending summary:** computed in `GetSummaryUseCase` via `ExpenseRepository.getSummary()` from Room data (aligned with offline-first SSOT)

### Alternatives Considered

**1. In-Memory Only**
- **Pros**: Simpler setup, faster reads (no database queries)
- **Cons**: Data lost on app kill, no Flow persistence, poor offline UX
- **Why Rejected**: Fails offline-first requirement

**2. SharedPreferences for Expense Lists**
- **Pros**: Simpler setup, faster reads (no database queries), less dependency overhead
- **Cons**: Data lost on app kill, SharedPreferences limited to small datasets, no Flow support
- **Why Rejected**: Poor UX (data loss), doesn't scale to 1000+ expenses, harder to implement pagination

**3. API-First (direct calls, Room as secondary cache)**
- **Pros**: Always fresh data, simpler conflict resolution, less sync logic
- **Cons**: Requires internet, poor offline experience, slower UI updates (network latency)
- **Why Rejected**: Fails core requirement of offline-first operation

**4. Realm Database**
- **Pros**: Better performance than Room, built-in sync with Realm Cloud, reactive queries
- **Cons**: Less mature in Kotlin ecosystem, vendor lock-in, larger binary size
- **Why Rejected**: Room is official Google solution, better Jetpack integration, no vendor lock-in

### Consequences

**What Becomes Easier:**
- **Offline Operation**: App fully functional without internet (add, delete, view expenses)
- **Reactive UI**: Flow emissions automatically update UI when data changes
- **Data Consistency**: Room is atomic, transactions ensure data integrity
- **Testing**: Can test repository with in-memory Room database (no mocking needed)
- **Pagination**: Room's PagingSource makes pagination trivial to add later
- **Migration**: Room's auto-migration handles schema changes gracefully

**What Becomes Harder:**
- **Sync Conflicts**: Last-write-wins is naive; real sync needs conflict resolution (CRDTs, vector clocks)
- **Initial Setup**: Room requires entities, DAOs, database class, TypeConverters (more boilerplate)
- **Debugging**: SQL queries are abstracted; need to inspect database with Android Studio inspector
- **Binary Size**: Room + SQLite adds ~1MB to APK size

### Honest Assessment

**The offline-first approach is correct for a finance app.** Users need to track expenses even without internet (e.g., traveling, poor connectivity). However, the fire-and-forget sync strategy is **intentionally simplified** due to time constraints.

**In production, this would require:**
- Conflict resolution strategy (operational transforms, last-write-wins with timestamps)
- Sync queue with retry logic (WorkManager for background sync)
- Optimistic UI updates with rollback on failure
- Server-side timestamps for conflict detection

The current implementation demonstrates understanding of offline-first principles but lacks production-grade sync reliability.

---

## ADR-003: Hilt for Dependency Injection

**Status:** Accepted

**Date:** 2026-06-03

### Context

The application needed a dependency injection framework to:
- Provide dependencies to ViewModels (use cases, repository)
- Manage singleton lifecycles (database, repository)
- Support compile-time verification (no runtime DI failures)
- Integrate with Jetpack Compose and ViewModel

The choice was between:
- Hilt (Google's official DI for Android)
- Koin (lightweight Kotlin DSL)
- Manual DI (constructor injection)

### Decision

Implement **Hilt** as the dependency injection framework with:
- `@HiltAndroidApp` on Application class
- `@HiltViewModel` for ViewModel injection
- `@Singleton` scoped modules for database and repository
- Compile-time verification via annotation processing (kapt)

### Alternatives Considered

**1. Koin (Service Locator Pattern)**
- **Pros**: Simpler setup, no annotation processing (faster builds), Kotlin DSL is concise, easier to learn
- **Cons**: Runtime crashes if dependencies not configured, no compile-time checks, slower at runtime (reflection)
- **Why Rejected**: Lack of compile-time safety is a dealbreaker for production apps; fails fast is better than runtime crashes

**2. Manual Dependency Injection**
- **Pros**: Zero dependencies, complete control, fastest runtime performance, no magic
- **Cons**: Boilerplate factory classes, manual lifecycle management, ViewModel injection requires custom factory
- **Why Rejected**: Not scalable beyond 5-10 classes; ViewModel injection is painful without framework support

**3. Dagger 2 (Hilt's predecessor)**
- **Pros**: Most powerful DI framework, compile-time verification, fastest runtime performance
- **Cons**: Steep learning curve, verbose boilerplate, Android-specific setup is complex
- **Why Rejected**: Hilt is Dagger under the hood with Android-specific conveniences; no reason to use raw Dagger

### Consequences

**What Becomes Easier:**
- **ViewModel Injection**: `@HiltViewModel` + `hiltViewModel()` composable is one line
- **Compile-Time Safety**: Missing dependencies fail at build time, not runtime
- **Testing**: Hilt test modules can replace production dependencies (though not used in this project)
- **Lifecycle Management**: `@Singleton` scope ensures database is created once
- **Android Integration**: Built-in support for Activity, Fragment, ViewModel, WorkManager
- **Scalability**: Adding new dependencies is straightforward (create module, provide function)

**What Becomes Harder:**
- **Build Time**: Kapt annotation processing adds 10-15 seconds to clean builds
- **Learning Curve**: Understanding `@Provides`, `@Binds`, `@InstallIn`, scopes takes time
- **Debugging**: Compile errors from Hilt can be cryptic (missing modules, wrong scopes)
- **Binary Size**: Hilt adds ~50KB to APK (negligible but worth noting)
- **Migration**: Moving from Hilt to Koin later would require refactoring all modules

### Honest Assessment

**Hilt is the correct choice for any production Android app.** The compile-time safety and ViewModel integration are non-negotiable for team environments. However, for a solo 4-hour project, Koin would have been faster to set up (no kapt, simpler syntax).

**Tradeoff Analysis:**
- Hilt setup: ~15 minutes (annotation processing, modules)
- Koin setup: ~5 minutes (DSL definition)
- Time saved: 10 minutes (not significant in 4-hour timeline)
- Benefit gained: Compile-time safety (worth the 10 minutes)

**The decision prioritizes correctness over speed.** In a lead-level assessment, demonstrating knowledge of Hilt (the official Google solution) shows alignment with industry best practices, even if Koin would have been pragmatically faster.

---

## ADR-004: Android-Only for Assessment; Kotlin Multiplatform Deferred

**Status:** Accepted (for this submission)

**Date:** 2026-06-03

### Context

The domain layer is already **pure Kotlin** (no Android imports in use cases or models). A natural next step for a multi-platform product is **Kotlin Multiplatform (KMP)** to share domain, repository contracts, and tests across Android and iOS.

The assessment was time-boxed (~2–4 hours). KMP introduces:

- Multi-module Gradle structure (`:shared`, `:androidApp`, optional `:iosApp`)
- `expect`/`actual` for database and HTTP
- Separate CI jobs and tooling (Xcode, simulators)
- DI split (Hilt on Android vs Koin/manual wiring in `commonMain`)

### Decision

Deliver **native Android** (Compose + Room + Hilt) for this repo. **Do not** introduce KMP modules in the assessment submission.

Document the KMP migration path in the root **README** (“What I Would Do Differently With More Time”) and keep domain code extractable into `commonMain`.

### Alternatives Considered

**1. KMP from day one**
- **Pros**: Maximum code reuse, strong lead signal for mobile platform strategy
- **Cons**: High setup cost; risks incomplete features/docs within the time box
- **Why Rejected for assessment**: Brief targets a working Android app with README, ADRs, and tests—not a skeleton multi-platform workspace

**2. Duplicate Swift/Kotlin codebases**
- **Pros**: Fast per-platform UI
- **Cons**: Business rules drift; double test maintenance
- **Why Rejected**: Conflicts with Clean Architecture goal; KMP is the preferred long-term fix

### Consequences

**What Becomes Easier (now):**
- Faster Gradle sync, single-platform CI, full Jetpack stack (Room, Hilt, Compose)

**What Is Deferred:**
- Shared `commonTest` for use cases on JVM
- iOS shipping path
- SQLDelight/Ktor in `shared` (see README for module diagram)

**Migration trigger:** Product requires iOS (or desktop) with the same expense rules and offline-first behavior—extract `domain/` into `:shared` first, then add platform `actual` for persistence and network.

---

## ADR-005: Domain-Only Unit Testing Strategy

**Status:** Accepted

**Date:** 2026-06-03

### Context

The assessment rewards testable architecture but is time-boxed. Options ranged from no tests, domain-only unit tests, repository integration tests, ViewModel tests, and Compose UI tests.

### Decision

Implement **20 unit tests** in `expenseTracker/app/src/test/.../domain/usecase/` covering all use cases with JUnit + MockK + `kotlinx-coroutines-test`. Mock `ExpenseRepository` at the use-case boundary.

**State management (presentation):** sealed `UiState` + `StateFlow` in ViewModels; not separately ADR’d beyond ADR-001.

**Validation:** Client-side rules in domain (`AddExpenseUseCase` — amount > 0, required fields); no server validation in mock API scope.

### Alternatives Considered

- **ViewModel / UI tests**: Rejected for time box; high setup (Hilt test rules, Compose test harness).
- **In-memory Room tests**: Valuable but deferred; domain tests already prove business rules.

### Consequences

- Fast, deterministic CI (`./gradlew test`).
- Gaps documented in README; future work adds `androidTest` and `commonTest` (KMP).

---

## Summary

These decisions form the architectural foundation of the Expense Tracker app:

1. **Clean Architecture**: Demonstrates scalable thinking, even if over-engineered for current scope
2. **Offline-First with Room**: Correct for finance apps, though sync logic is simplified
3. **Hilt DI**: Industry standard, compile-time safe, worth the setup overhead
4. **Android-only now, KMP later**: Time-boxed delivery; domain ready to move to `commonMain`
5. **Domain-only tests**: Maximum ROI within time box; integration/UI tests deferred

All decisions prioritize **long-term maintainability and team scalability** over short-term development speed, which is appropriate for a tech lead assessment demonstrating architectural judgment.
