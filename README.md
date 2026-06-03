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
