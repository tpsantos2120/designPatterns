# Facade Pattern Demo - Financial Report System

A Spring Boot application demonstrating the **Facade Design Pattern** through a condominium financial report generation system. This project showcases how the Facade pattern simplifies complex subsystem interactions by providing a unified, high-level interface.

## Table of Contents

- [Overview](#overview)
- [Design Pattern: Facade](#design-pattern-facade)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Examples](#examples)
- [Configuration](#configuration)

## Overview

This application manages financial reporting for condominiums, providing a simplified interface for complex operations including:
- Transaction data retrieval
- Tax calculations
- PDF generation
- Email delivery
- Audit logging
- Report caching

The **Facade Pattern** hides the complexity of multiple subsystems behind a single, clean API.

## Design Pattern: Facade

The Facade pattern provides a unified interface to a set of interfaces in a subsystem. It defines a higher-level interface that makes the subsystem easier to use.

### Implementation

The `FinancialReportFacade` class serves as the facade, orchestrating interactions between:
- **TransactionRepository** - Data retrieval
- **TaxCalculationService** - Tax computations
- **PdfGeneratorEngine** - PDF document creation
- **EmailServiceProvider** - Email delivery
- **AuditLogger** - Audit trail logging
- **CacheManager** - Report caching
- **MeterRegistry** - Metrics collection

```java
@Service
public class FinancialReportFacade implements ReportFacade {

  // Complex subsystems
  private final TransactionRepository transactionRepository;
  private final TaxCalculationService taxService;
  private final PdfGeneratorEngine pdfEngine;
  private final EmailServiceProvider emailProvider;
  private final AuditLogger auditLogger;
  private final CacheManager cacheManager;

  // Simple public interface
  public FinancialReport generateReport(FinancialReportRequest request) {
    // Coordinates all subsystems transparently
  }
}
```

### Benefits

- **Simplified Interface** - Clients only interact with the facade
- **Loose Coupling** - Subsystem changes don't affect clients
- **Better Organization** - Clear separation of concerns
- **Improved Testability** - Mock the facade for testing

## Features

- Generate comprehensive financial reports with transaction summaries
- Calculate tax breakdowns for Brazilian jurisdiction
- Export reports as PDF documents
- Email reports to multiple recipients
- Cache reports for improved performance
- Audit logging for compliance
- Metrics collection with Micrometer
- RESTful API with validation
- PostgreSQL database with JPA/Hibernate
- Docker Compose for local development

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL 17** - Database
- **Hibernate** - ORM
- **Lombok** - Boilerplate reduction
- **iText 7** - PDF generation
- **Micrometer** - Metrics collection
- **Docker Compose** - Container orchestration
- **Maven** - Build tool

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd designPatterns
```

### 2. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start PostgreSQL on port 5432 with:
- Database: `condoconnect`
- Username: `admin`
- Password: `admin123`

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Verify the Application

```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

## API Endpoints

### Generate Financial Report (JSON)

**Endpoint:** `POST /api/reports/financial/{condoId}`

**Request:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "includeTaxes": true,
  "includeResidents": true
}
```

**Response:**
```json
{
  "reportId": "550e8400-e29b-41d4-a716-446655440000",
  "condoId": 1,
  "generatedAt": "2024-11-06",
  "contentBase64": "JVBERi0xLjcK...",
  "format": "PDF",
  "metadata": {
    "totalRevenue": 15000.00,
    "totalExpenses": 8000.00,
    "netBalance": 7000.00,
    "taxAmount": 1050.00,
    "transactionCount": 25
  }
}
```

### Download Financial Report (PDF)

**Endpoint:** `POST /api/reports/financial/{condoId}/download`

**Request:** Same as above

**Response:** Binary PDF file with appropriate headers

```bash
curl -X POST http://localhost:8080/api/reports/financial/1/download \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "includeTaxes": true,
    "includeResidents": true
  }' \
  --output report.pdf
```

### Generate and Email Report

**Endpoint:** `POST /api/reports/financial/{condoId}/email`

**Request:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "recipients": [
    "admin@example.com",
    "manager@example.com"
  ]
}
```

**Response:**
```json
{
  "reportId": "550e8400-e29b-41d4-a716-446655440000",
  "successfulRecipients": ["admin@example.com", "manager@example.com"],
  "failedRecipients": [],
  "sentAt": "2024-11-06T10:30:00Z"
}
```

### Get Cached Report

**Endpoint:** `GET /api/reports/financial/{condoId}/cached?date=2024-11-06`

**Response:** Returns cached report if available, 404 otherwise

### Download Cached Report (PDF)

**Endpoint:** `GET /api/reports/financial/{condoId}/cached/download?date=2024-11-06`

**Response:** Binary PDF file if cached report exists

## Project Structure

```
src/main/java/org/java/
├── FacadePattern.java              # Spring Boot main class
├── config/
│   └── CacheConfiguration.java     # Cache configuration
├── controller/
│   └── FinancialReportController.java  # REST API endpoints
├── dto/
│   ├── FinancialReport.java        # Report domain object
│   ├── FinancialReportRequest.java # Request object
│   ├── ReportRequestDTO.java       # API request DTO
│   ├── ReportResponseDTO.java      # API response DTO
│   ├── ReportMetadata.java         # Report metadata
│   └── ...
├── enums/
│   └── ReportFormat.java           # Report format enum
├── exceptions/
│   ├── ReportGenerationException.java
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Condominium.java            # Condo entity
│   ├── Transaction.java            # Transaction entity
│   └── Resident.java               # Resident entity
├── repository/
│   ├── CondominiumRepository.java
│   ├── TransactionRepository.java
│   └── ResidentRepository.java
├── service/
│   ├── ReportFacade.java           # Facade interface
│   ├── FinancialReportFacade.java  # Facade implementation ⭐
│   ├── TaxCalculationService.java  # Tax subsystem
│   ├── PdfGeneratorEngine.java     # PDF subsystem
│   ├── EmailServiceProvider.java   # Email subsystem
│   ├── AuditLogger.java            # Audit subsystem
│   └── ReportIdGenerator.java      # ID generation
└── DataInitializer.java            # Sample data loader
```

## Examples

### Using cURL

```bash
# Generate and download report
curl -X POST http://localhost:8080/api/reports/financial/1/download \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "includeTaxes": true,
    "includeResidents": false
  }' \
  -o financial-report.pdf

# Get cached report
curl -X GET "http://localhost:8080/api/reports/financial/1/cached?date=2024-11-06"

# Generate and email report
curl -X POST http://localhost:8080/api/reports/financial/1/email \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "recipients": ["admin@condoconnect.com"]
  }'
```

### Using HTTP Client (IntelliJ)

Create a `.http` file:

```http
### Generate Financial Report
POST http://localhost:8080/api/reports/financial/1
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "includeTaxes": true,
  "includeResidents": true
}

### Download Financial Report
POST http://localhost:8080/api/reports/financial/1/download
Content-Type: application/json

{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "includeTaxes": true,
  "includeResidents": false
}
```

## Configuration

### Application Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: facade-pattern-demo

  datasource:
    url: jdbc:postgresql://localhost:5432/condoconnect
    username: admin
    password: admin123

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

server:
  port: 8080

# Custom configuration
condoconnect:
  reports:
    cache-expiration: 24h
    pdf-template: financial-report-v3
    enable-email-notifications: true
    max-retries: 3
```

### Docker Compose

```yaml
services:
  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: condoconnect
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - data:/var/lib/postgresql/data
```

## Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

### Available Metrics

- `report.generation.time` - Report generation duration
- `report.generation.count` - Number of reports generated
- `report.email.sent` - Email delivery statistics

## Development

### Database Access

The PostgreSQL database can be accessed at:
- **Host:** localhost
- **Port:** 5432
- **Database:** condoconnect
- **Username:** admin
- **Password:** admin123

### Sample Data

The application automatically loads sample data on startup via `DataInitializer.java`, including:
- 3 condominiums
- Multiple residents
- Sample transactions (revenue and expenses)

## Design Pattern Benefits in Practice

### Without Facade

```java
// Client needs to manage all subsystem complexity
List<Transaction> transactions = transactionRepository.findByCondoId(condoId);
TaxResult taxes = taxService.calculate(transactions);
byte[] pdf = pdfEngine.generate(transactions, taxes);
emailProvider.send(pdf, recipients);
auditLogger.log("REPORT_GENERATED", condoId);
cacheManager.put(cacheKey, pdf);
```

### With Facade

```java
// Client uses simple, high-level interface
FinancialReport report = reportFacade.generateReport(request);
```

The facade handles all subsystem coordination internally!

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is for educational purposes demonstrating the Facade design pattern.

## Contact

For questions or feedback, please open an issue in the repository.