# Sales Report Generator

## Overview
This is a Java application that demonstrates the implementation report generation. The application allows for the export of sales data into multiple formats (CSV, JSON, XML, PDF) using a common class and format-specific implementations.

## Features
- Export sales data to multiple file formats:
  - CSV (Comma-Separated Values)
  - JSON (JavaScript Object Notation)
  - XML (eXtensible Markup Language)
  - PDF (Portable Document Format)

## Project Structure
The project is organized into several key parts:
- `model`: Contains the data model for sales records
- `enums`: Contains enumeration types for supported export formats
- `export`: Contains the report generation service and format-specific exporters

## Technology Stack
- Java 25
- Maven for dependency management
- Libraries:
  - Jackson for JSON processing
  - iText for PDF generation

## Output Files
The application generates the following report files:
- `sales_report.csv`: A comma-separated values file
- `sales_report.json`: A JSON formatted file
- `sales_report.xml`: An XML formatted file
- `sales_report.pdf`: A PDF document

## Requirements
- Java 25 or higher
- Maven 3.6+
