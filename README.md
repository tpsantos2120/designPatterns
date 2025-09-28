# Sales Report Generator With Strategy Pattern

## Overview

This project demonstrates the implementation of the Strategy Design Pattern in Java for generating sales reports in multiple formats. 
The application provides a flexible framework for exporting sales data to various file formats including CSV, JSON, XML, and PDF. 
The intent here is to demonstrate the use of the strategy pattern in Java.

## Project Structure

- `org.java.enums`: Contains enum definitions, including `ExportFormat` which defines supported export formats
- `org.java.model`: Contains data model classes, particularly the `Sale` class representing sales records
- `org.java.strategy`: Contains the strategy pattern implementation
  - `ExportStrategy`: Interface defining the export behavior
  - `impl`: Package containing specific implementations for each export format
    - `CsvExportStrategy`: CSV export implementation
    - `JsonExportStrategy`: JSON export implementation
    - `XmlExportStrategy`: XML export implementation
    - `PdfExportStrategy`: PDF export implementation
  - `ReportService`: Service class orchestrating the export process using the appropriate strategy

## Design Pattern

This project demonstrates the **Strategy Design Pattern**:
- The pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable
- It lets the algorithm vary independently from clients that use it
- In this project, each export format strategy encapsulates the specific logic for that format

## Features

- Export sales data to multiple formats (CSV, JSON, XML, PDF)
- Easily extendable to support additional export formats
- Configurable output directory
- Clean separation of concerns through strategy pattern implementation
