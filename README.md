# Design Patterns - Observer Pattern

A Java project demonstrating two implementations of the **Observer Pattern** for multi-format sales report generation.

## Overview

This project showcases the Observer Pattern through two different approaches for exporting sales data to multiple formats (CSV, JSON, PDF, XML). The Observer Pattern allows a one-to-many dependency between objects, where when one object changes state, all its dependents are notified and updated automatically.

## Project Structure

```
designPatterns/
├── src/main/java/org/java/
│   ├── observerPatternV1/          # Simple Observer Pattern implementation
│   │   ├── Main.java               # Standalone demo application
│   │   ├── model/
│   │   │   └── Sale.java           # Sale domain model
│   │   ├── observers/
│   │   │   ├── ReportObserver.java # Observer interface
│   │   │   └── impl/               # Concrete observers
│   │   │       ├── CsvReportObserver.java
│   │   │       ├── JsonReportObserver.java
│   │   │       ├── PdfReportObserver.java
│   │   │       └── XmlReportObserver.java
│   │   ├── subject/
│   │   │   └── ReportService.java  # Subject (Observable)
│   │   └── enums/
│   │       └── ExportFormat.java
│   │
│   └── observerPatternV2/          # Spring Boot Observer Pattern implementation
│       ├── ObserverPatternApplication.java  # Spring Boot main class
│       ├── controller/
│       │   └── ReportController.java        # REST API endpoints
│       ├── model/
│       │   └── Sale.java
│       ├── observers/
│       │   ├── Observer.java       # Observer interface
│       │   └── impl/               # Concrete observers
│       │       ├── CsvReportObserver.java
│       │       ├── JsonReportObserver.java
│       │       ├── PdfReportObserver.java
│       │       └── XmlReportObserver.java
│       ├── service/
│       │   ├── Report.java         # Subject wrapper
│       │   └── ReportService.java  # Business logic
│       ├── subject/
│       │   └── EventManager.java   # Event-based notification system
│       └── enums/
│           └── EventType.java      # Event types (UPDATE, DELETE)
└── reports/                        # Generated report files
```

## Technologies

- **Java 25**
- **Spring Boot 3.5.6** (for V2 implementation)
- **Maven** - Build and dependency management
- **Jackson** - JSON processing
- **iText PDF** - PDF generation

## Observer Pattern Implementations

### Version 1: Simple Observer Pattern

A straightforward implementation where:
- **Subject**: `ReportService` maintains a list of observers and notifies them when sales data is exported
- **Observers**: Format-specific report generators (`CsvReportObserver`, `JsonReportObserver`, etc.)
- **Push model**: Observers receive data directly in the `update()` method

**Key Features:**
- Direct observer registration/removal
- Synchronous notification
- Standalone Java application

### Version 2: Event-Driven Observer Pattern with Spring Boot

An enhanced implementation with Spring Boot integration:
- **Subject**: `Report` class with `EventManager` for event-based notifications
- **Observers**: Format-specific report generators implementing `Observer` interface
- **Event types**: `UPDATE` (create/update reports) and `DELETE` (remove reports)
- **Pull model**: Observers can retrieve data from the subject when notified

**Key Features:**
- Event-driven architecture with typed events
- Spring Boot REST API for report generation
- Dependency injection and service layer
- SLF4J logging
- More flexible subscription/unsubscription model

## Running the Project

### Version 1 (Standalone)
```bash
mvn compile
mvn exec:java -Dexec.mainClass="org.java.observerPatternV1.Main"
```

### Version 2 (Spring Boot)
```bash
mvn spring-boot:run
```

Then access the REST API:
- **POST** `/api/reports/export` - Export sales reports in all formats
- **GET** `/api/reports/current` - Get current sales data

## Generated Reports

When the application runs, it generates sales reports in the `reports/` directory:
- `sales_report.csv`
- `sales_report.json`
- `sales_report.pdf`
- `sales_report.xml`

## Key Differences Between V1 and V2

| Aspect | Version 1 | Version 2 |
|--------|-----------|-----------|
| **Architecture** | Standalone Java | Spring Boot |
| **Notification** | Direct observer list | Event-driven with EventManager |
| **Event Types** | Single type (export) | Multiple types (UPDATE, DELETE) |
| **API** | No API | REST API with controller |
| **Logging** | Console output | SLF4J logging |
| **Flexibility** | Basic observer pattern | Event-based subscriptions |

## Design Pattern Benefits

The Observer Pattern provides:
- **Loose coupling** between subject and observers
- **Dynamic subscription** - observers can be added/removed at runtime
- **Broadcast communication** - one subject notifies many observers
- **Open/Closed Principle** - easy to add new report formats without modifying existing code

## License

This project is for educational purposes to demonstrate design pattern implementations.
