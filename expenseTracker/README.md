# Expense Tracker - Android Application

## Overview

A modern Android expense tracking application built with Clean Architecture and MVVM pattern. The app allows users to track their daily expenses, categorize spending, view summaries by category, and visualize spending patterns. Built as a tech lead assessment submission to demonstrate scalable architecture, best practices, and professional development standards.

## Architecture

This application follows **Clean Architecture** principles combined with **MVVM (Model-View-ViewModel)** pattern to ensure separation of concerns, testability, and maintainability.

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

- **Clean Architecture**: Separates domain, data, and presentation layers with clear boundaries
- **MVVM Pattern**: ViewModels manage UI state, Compose UI observes state via StateFlow
- **Offline-First**: Room database is the Single Source of Truth (SSOT)
- **Dependency Injection**: Hilt for compile-time verified DI
- **Reactive Streams**: Kotlin Flow for reactive data updates
- **Unidirectional Data Flow**: UI events → ViewModel → Use Cases → Repository → Database

## Tech Stack

| Technology | Choice | Rationale |
|------------|--------|-----------|
| **Language** | Kotlin | Null safety, coroutines, concise syntax, official Android language |
| **UI Framework** | Jetpack Compose + Material 3 | Declarative UI, less boilerplate, modern design system |
| **Architecture** | MVVM + Clean Architecture | Testable, scalable, separation of concerns, industry standard |
| **Dependency Injection** | Hilt | Compile-time verification, Android-first, less setup than Koin |
| **Async/Reactive** | Coroutines + Flow | Lifecycle-aware, structured concurrency, reactive streams |
| **Local Database** | Room | Offline-first SSOT, Flow support, type-safe SQL queries |
| **Navigation** | Navigation Compose | Type-safe navigation, deep linking support, Compose integration |
| **Testing** | JUnit + MockK | Kotlin-native mocking, coroutine test support |

## Project Structure

```
app/src/main/java/com/example/expensetracker/
├── data/                           # Data layer - external data sources
│   ├── local/                      # Local data source (Room)
│   │   ├── dao/                    # Data Access Objects with Flow queries
│   │   ├── database/               # Room database configuration
│   │   └── entity/                 # Room entities with TypeConverters
│   ├── remote/                     # Remote data source (API)
│   │   ├── api/                    # API interface definitions
│   │   ├── dto/                    # Data Transfer Objects
│   │   └── datasource/             # Mock API implementation (1000ms delay)
│   ├── mapper/                     # DTO ↔ Entity ↔ Domain mappers
│   └── repository/                 # Repository implementations (offline-first)
├── domain/                         # Domain layer - business logic (pure Kotlin)
│   ├── model/                      # Domain models (Expense, Category, Summary)
│   ├── repository/                 # Repository interfaces (contracts)
│   └── usecase/                    # Use cases (single responsibility business logic)
├── presentation/                   # Presentation layer - UI (Compose)
│   ├── expense_list/               # Expense list screen + ViewModel + UiState
│   ├── add_expense/                # Add expense form + ViewModel + UiState
│   ├── summary/                    # Category summary + ViewModel
│   ├── filter/                     # Filter screen (placeholder)
│   └── components/                 # Reusable UI components
├── di/                             # Hilt dependency injection modules
│   ├── DatabaseModule              # Provides Room database and DAO
│   ├── NetworkModule               # Provides API implementation
│   ├── RepositoryModule            # Binds repository interfaces
│   └── DispatcherModule            # Provides coroutine dispatchers
├── navigation/                     # Navigation graph and routes
└── ui/theme/                       # Compose theme and styling

app/src/test/java/                  # Unit tests (JUnit + MockK)
└── domain/usecase/                 # Use case tests (20 tests, 100% coverage)
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 34
- Minimum SDK 24 (Android 7.0)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd expenseTracker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `expenseTracker` directory
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click "Run" (Shift + F10) in Android Studio
   - Or use: `./gradlew installDebug`

5. **Run tests**
   ```bash
   ./gradlew test
   ```

## Assumptions Made

During development, several ambiguities in the requirements were identified and resolved with the following decisions:

| Ambiguity | Decision | Rationale |
|-----------|----------|-----------|
| **Currency not specified** | Single currency (LKR), model has currency field for future extension | Avoid scope creep while maintaining extensibility |
| **No real backend** | Mock API with artificial 1000ms delay + swappable interface | Tests real loading states, easy to swap with Retrofit |
| **Offline conflict resolution undefined** | Last-write-wins strategy | Simplest safe default for single-user app |
| **Pagination not specified** | Not implemented, LazyColumn uses stable keys | Pagination-ready architecture without premature optimization |
| **Filter logic: AND vs OR** | OR logic (category OR date range) | More useful UX, clearly documented |
| **Delete confirmation** | No confirmation dialog | Time constraint, noted as future improvement |
| **Error retry strategy** | Single retry on error state tap | Simple, user-initiated, sufficient for demo |

## Testing Strategy

### What Was Tested

- **Domain Use Cases** (20 unit tests)
  - AddExpenseUseCase: Validation logic (amount > 0)
  - DeleteExpenseUseCase: Repository interaction
  - FilterExpensesUseCase: OR logic filtering, date ranges
  - GetSummaryUseCase: Percentage calculations, sorting, edge cases

### What Was Not Tested (and Why)

- **ViewModels**: Time constraint. In production, would test state transitions and error handling.
- **Repository Implementation**: Time constraint. Would test Room + API sync logic.
- **UI Tests**: Time constraint. Would add Compose UI tests for critical flows.
- **Integration Tests**: Time constraint. Would test end-to-end flows with real database.

### Test Coverage Priorities

Given the 3.5-hour time constraint, focus was on:
1. **Domain layer** (most important business logic, pure Kotlin, easy to test)
2. **Use case validation** (critical for data integrity)
3. **Edge cases** (empty states, zero amounts, percentage calculations)

## Architecture Decision Records

See [ADR.md](./ADR.md) for detailed architectural decision records covering:
- ADR-001: MVVM + Clean Architecture
- ADR-002: Room as Offline-First Single Source of Truth
- ADR-003: Hilt for Dependency Injection

## AI Usage Disclosure

AI tools used during this assessment:

- **Claude (claude.ai)**: Used for domain model generation, boilerplate scaffolding, Compose UI components, and ADR drafting
- **Cursor**: Used for code completion and refactoring assistance

All AI-generated code was reviewed, understood, and modified where necessary. I can explain every line of this submission and justify all architectural decisions.

**AI Transcript**: [Will be added after submission]

## Known Limitations

This submission was completed under a 3.5-hour time constraint. The following limitations are acknowledged:

1. **No Delete Confirmation**: Expenses can be deleted without confirmation (UX risk)
2. **No Edit Functionality**: Cannot edit existing expenses (must delete and re-add)
3. **No Search**: Large expense lists cannot be searched
4. **Filter Not Implemented**: Placeholder screen only
5. **Single Currency**: LKR hardcoded (model supports multi-currency)
6. **No Network Error Handling**: Refresh failures could be handled more gracefully
7. **No Data Export**: Cannot export expenses to CSV/PDF
8. **No User Authentication**: Single-user app
9. **Limited Test Coverage**: Domain layer only (ViewModels, Repository, UI untested)

## Future Improvements

With additional time, the following enhancements would be prioritized:

### High Priority
- **Pagination**: Load expenses in pages for better performance
- **Search**: Full-text search across notes and categories
- **Edit Expense**: Allow editing existing expenses
- **Delete Confirmation**: Add confirmation dialog with undo option
- **Filter Implementation**: Complete filter screen with category/date filters
- **Multi-currency Support**: Currency selection and conversion rates

### Medium Priority
- **Data Export**: Export to CSV, PDF, Excel
- **Data Backup**: Cloud backup and restore
- **Budget Tracking**: Set monthly budgets per category
- **Recurring Expenses**: Support for recurring transactions
- **Charts & Graphs**: Visual spending analytics (pie charts, line graphs)
- **Dark Mode**: System-aware dark theme

### Low Priority
- **User Authentication**: Multi-user support with Firebase Auth
- **Cloud Sync**: Real backend with Firebase/Supabase
- **Notifications**: Spending alerts and budget warnings
- **Receipt Scanning**: OCR for receipt capture
- **CI/CD Pipeline**: GitHub Actions for automated testing and deployment
- **Widget**: Home screen widget for quick expense entry

## Performance Considerations

- **LazyColumn**: Efficient scrolling for large expense lists
- **Stable Keys**: Prevents unnecessary recomposition
- **StateFlow**: Efficient state management with lifecycle awareness
- **Room Indexing**: Database queries optimized with indexes (future)
- **Pagination Ready**: Architecture supports pagination without refactoring

## License

This is a technical assessment submission. All rights reserved.

---

**Built with ❤️ using Kotlin, Jetpack Compose, and Clean Architecture**
