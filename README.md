# Design Patterns Demo - Spring Boot Application

A Spring Boot application demonstrating three classic Gang of Four design patterns: **Builder**, **Factory**, and **Singleton**.

## Design Patterns Implemented

### 1. Builder Pattern 🏗️
**Location**: `org.java.model.Sale.Builder`

**Purpose**: Provides a fluent, readable way to construct complex `Sale` objects step-by-step.

**Example**:
```java
Sale sale = Sale.builder()
    .id(UUID.randomUUID())
    .productName("Laptop")
    .amount(1299.99)
    .date(LocalDate.now())
    .customerName("John Smith")
    .build();
```

**Benefits**:
- Readable, self-documenting code
- Optional parameters without constructor overloading
- Immutable objects (when used with records)

### 2. Factory Pattern 🏭
**Location**: `org.java.factory.ReportExporterFactory`

**Purpose**: Creates the appropriate `ReportExporter` implementation based on the requested export format.

**Products**:
- `CsvExporter` - Exports to CSV format
- `JsonExporter` - Exports to JSON format
- `XmlExporter` - Exports to XML format
- `PdfExporter` - Exports to PDF format

**Example**:
```java
ReportExporter exporter = factory.getExporter(ExportFormat.CSV);
exporter.export(sales, "reports/");
```

**Benefits**:
- Loose coupling between client code and concrete implementations
- Easy to add new export formats
- Single Responsibility Principle - each exporter handles one format

### 3. Singleton Pattern 🎯
**Location**: `org.java.singleton.ReportMetricsCollector`

**Purpose**: Ensures a single instance tracks export metrics across the entire application lifecycle.

**Implementation**: Enum Singleton (Best Practice)
- Thread-safe by JVM guarantee
- Prevents reflection attacks
- Serialization for free
- No synchronization overhead

**Example**:
```java
// Access the single instance
ReportMetricsCollector.INSTANCE.recordExport("CSV", 100);

// Get metrics from anywhere in the application
MetricsSummary summary = ReportMetricsCollector.INSTANCE.getSummary();
```

**Why Enum Singleton?**
- Simplest and most robust implementation
- Thread-safe without explicit synchronization
- Protected against reflection attacks
- Handles serialization automatically

**Benefits**:
- Single centralized metrics tracking
- Thread-safe for concurrent access
- Accessible from anywhere in the application
- No performance overhead

## Project Structure

```
src/main/java/org/java/
├── controller/
│   └── SalesController.java           # REST endpoints
├── exporter/
│   ├── ReportExporter.java            # Factory Product interface
│   └── impl/
│       ├── CsvExporter.java           # Concrete product
│       ├── JsonExporter.java          # Concrete product
│       ├── XmlExporter.java           # Concrete product
│       └── PdfExporter.java           # Concrete product
├── factory/
│   └── ReportExporterFactory.java     # Factory Pattern
├── model/
│   ├── Sale.java                      # Builder Pattern
│   ├── ExportFormat.java              # Enum for export formats
│   └── ExportRequest.java             # Request DTO
├── service/
│   └── ReportService.java             # Business logic
├── singleton/
│   └── ReportMetricsCollector.java    # Singleton Pattern (Enum)
├── exception/
│   └── GlobalExceptionHandler.java    # Error handling (RFC 7807)
└── DesignPatternsApplication.java     # Main class
```

## API Endpoints

### 1. Export Sales Report (Demonstrates Factory Pattern)
**POST** `/api/sales/export`

Uses the **Factory Pattern** to select the appropriate exporter based on the format.

Request body:
```json
{
  "sales": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "productName": "Laptop",
      "amount": 1299.99,
      "date": "2024-10-19",
      "customerName": "John Smith"
    }
  ],
  "format": "CSV"
}
```

Response:
```json
{
  "status": "success",
  "message": "Sales report exported successfully to CSV format",
  "format": "CSV"
}
```

### 2. Get Supported Formats
**GET** `/api/sales/formats`

Response:
```json
{
  "formats": ["CSV", "JSON", "XML", "PDF"],
  "count": 4
}
```

### 3. Get Sample Sales Data (Demonstrates Builder Pattern)
**GET** `/api/sales/sample`

Returns sample sales data constructed using the **Builder Pattern** (`Sale.builder()`).

Response:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "productName": "Laptop",
    "amount": 1299.99,
    "date": "2024-10-19",
    "customerName": "John Smith"
  },
  {
    "id": "351425b9-7050-4c4b-8126-389541e2a5d6",
    "productName": "Wireless Mouse",
    "amount": 29.99,
    "date": "2025-10-26",
    "customerName": "Jane Doe"
  },
  {
    "id": "e444d2f5-ab37-419b-919c-c351049c15a4",
    "productName": "USB-C Cable",
    "amount": 15.5,
    "date": "2025-10-25",
    "customerName": "Bob Johnson"
  }
]
```

### 4. Get Export Metrics (Demonstrates Singleton Pattern)
**GET** `/api/sales/metrics`

Response:
```json
{
  "totalExports": 42,
  "successfulExports": 40,
  "failedExports": 2,
  "successRate": 95.24,
  "formatStatistics": {
    "CSV": 15,
    "JSON": 12,
    "XML": 8,
    "PDF": 5
  },
  "firstExportTime": "2024-10-19T10:00:00",
  "lastExportTime": "2024-10-19T15:30:00"
}
```

## Running the Application

### Prerequisites
- Java 21
- Maven 3.6+

### Build and Run
```bash
# Clean and build
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR
java -jar target/designPatterns-1.0.0.jar
```

The application will start on `http://localhost:8080`

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Output directory for reports
app.reports.output-directory=reports

# Logging Configuration
logging.level.root=INFO
logging.level.org.java=DEBUG

# Jackson Configuration (JSON formatting)
spring.jackson.serialization.indent-output=true
spring.jackson.serialization.write-dates-as-timestamps=false
```

## Testing with cURL

### Export to CSV
```bash
curl -X POST http://localhost:8080/api/sales/export \
  -H "Content-Type: application/json" \
  -d '{
    "sales": [
      {
        "id": "123e4567-e89b-12d3-a456-426614174000",
        "productName": "Laptop",
        "amount": 1299.99,
        "date": "2024-10-19",
        "customerName": "John Smith"
      }
    ],
    "format": "CSV"
  }'
```

### Get Formats
```bash
curl http://localhost:8080/api/sales/formats
```

### Get Sample Data
```bash
curl http://localhost:8080/api/sales/sample
```

### Get Metrics (Singleton Pattern)
```bash
curl http://localhost:8080/api/sales/metrics
```

## Technologies Used

- **Spring Boot 3.5.6** - Application framework
- **Java 21** - Programming language
- **Jackson** - JSON processing
- **iText 5.5.13.3** - PDF generation
- **SLF4J** - Logging
- **Spring Validation** - Request validation

## Design Pattern Benefits Summary

| Pattern | Problem Solved | Benefit |
|---------|---------------|---------|
| Builder | Complex object construction | Readable, flexible object creation |
| Factory | Object creation logic | Loose coupling, easy extensibility |
| Singleton | Global state management | Single source of truth, controlled access |

## License

This is a demonstration project for educational purposes.
