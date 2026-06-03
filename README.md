# expense-tracker-compose-clean-architecture
Expense Tracker mobile application built with Kotlin, Jetpack Compose, MVVM, Clean Architecture, Repository Pattern, Room, Hilt, Coroutines, and Flow. Supports offline-first data access, expense filtering, category summaries, and comprehensive unit testing.
# Expense Tracker - Compose Clean Architecture

A modern Android Expense Tracker application built using Kotlin and Jetpack Compose, demonstrating Clean Architecture principles, MVVM, Repository Pattern, offline-first data handling, and testable business logic.

## Features

### Expense Management

* View all expenses
* Add new expenses
* Delete expenses
* View expense details

### Filtering

* Filter by category
* Filter by date range

### Spending Summary

* Category-wise spending totals
* Expense counts by category
* Spending percentage breakdown

### User Experience

* Loading states
* Empty states
* Error handling
* Responsive Compose UI

### Offline Support

* Room Database caching
* Offline-first architecture
* Local persistence of expenses

---

## Tech Stack

### Language

* Kotlin

### UI

* Jetpack Compose
* Material 3
* Navigation Compose

### Architecture

* MVVM
* Clean Architecture
* Repository Pattern

### Dependency Injection

* Hilt

### Asynchronous Programming

* Kotlin Coroutines
* Kotlin Flow

### Local Storage

* Room Database

### Testing

* JUnit
* MockK

---

## Architecture

The application follows Clean Architecture principles with a clear separation of concerns.

### Presentation Layer

Responsible for:

* Compose UI
* ViewModels
* UI State Management
* Navigation

### Domain Layer

Responsible for:

* Business Models
* Repository Contracts
* Use Cases
* Business Rules

### Data Layer

Responsible for:

* Remote Data Sources
* Local Database
* Repository Implementations
* Data Mapping

---

## Project Structure

```text
app/
├── data
│   ├── local
│   ├── remote
│   ├── mapper
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── presentation
│   ├── expense_list
│   ├── add_expense
│   ├── summary
│   ├── filter
│   └── components
│
├── di
│
└── tests
```

---

## Design Decisions

### MVVM

MVVM was selected to separate UI logic from business logic while improving maintainability and testability.

### Repository Pattern

Repositories abstract data sources and allow the application to switch between local and remote implementations without impacting business logic.

### Offline-First Strategy

Room Database acts as the single source of truth.

Application flow:

1. Fetch data from remote source.
2. Persist data locally.
3. Observe local database using Flow.
4. Update UI reactively.

This ensures data remains available even when network connectivity is unavailable.

---

## Testing Strategy

Unit tests focus on business logic and repository interactions.

Covered scenarios include:

* Adding expenses
* Deleting expenses
* Filtering expenses
* Category summary calculations

---

## Assumptions

* Authentication is outside the scope of this assignment.
* Expense categories are predefined.
* Remote APIs are mocked.
* Currency conversion is not implemented.
* Expense synchronization conflicts are not considered.

---

## Future Improvements

* Pagination support
* Search functionality
* Multi-currency support
* Expense editing
* Cloud synchronization
* Charts and analytics dashboard
* CI/CD pipeline
* Integration and UI testing

---

## AI Usage Disclosure

AI-assisted tools were used to support:

* Architecture brainstorming
* Documentation drafting
* Code review suggestions

All implementation decisions, architecture choices, and final code were reviewed and validated manually.

---

## Evaluation Goals

This project prioritizes:

* Maintainable architecture
* Scalability
* Testability
* Clean code practices
* Offline support
* Documentation quality

A deliberate focus was placed on architectural quality and engineering practices rather than maximizing feature count.
