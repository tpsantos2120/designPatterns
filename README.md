# Observer Pattern Implementations

This project contains two implementations of the Observer design pattern, `observerPatternV1` and `observerPatternV2`.

## observerPatternV1

This version demonstrates a basic implementation of the Observer pattern.

*   **Subject:** The `ReportService` class acts as the concrete subject. It maintains a list of observers and notifies them when new sales data is available for export.
*   **Observer:** The `ReportObserver` interface defines the `update` method, which is called by the subject.
*   **Model:** This is a "push" model. The subject pushes the sales data (`List<Sale>`) and the output directory to the observers.

## observerPatternV2

This version refactors the first implementation to use a more generic and flexible approach.

*   **Subject:** It introduces a `Subject` interface, with `ReportSubject` as the concrete implementation. This decouples the observers from a concrete subject.
*   **Observer:** It uses a generic `Observer` interface.
*   **Model:** This is a "pull" model. The subject sends a reference of itself to the observers, which then "pull" the data they need from the subject. This is more flexible, as observers can decide what data they need.

## Key Differences

| Feature | observerPatternV1 | observerPatternV2 |
| :--- | :--- | :--- |
| **Coupling** | Tightly coupled. Observers are tied to the concrete `ReportService`. | Loosely coupled. Observers depend on the `Subject` interface. |
| **Data Flow** | "Push" model. Subject sends data to observers. | "Pull" model. Observers pull data from the subject. |
| **Flexibility** | Less flexible. Changes to the data require updating the observer interface. | More flexible. Observers can be changed to request different data without affecting the subject. |
