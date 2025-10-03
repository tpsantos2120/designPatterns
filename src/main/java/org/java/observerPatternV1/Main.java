package org.java.observerPatternV1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;
import org.java.observerPatternV1.observers.impl.CsvReportObserver;
import org.java.observerPatternV1.observers.impl.JsonReportObserver;
import org.java.observerPatternV1.observers.impl.PdfReportObserver;
import org.java.observerPatternV1.observers.impl.XmlReportObserver;
import org.java.observerPatternV1.subject.ReportService;

class Main {
  void main() {
    List<Sale> sales = new ArrayList<>();

    sales.add(Sale.builder()
        .id(UUID.randomUUID())
        .productName("Laptop")
        .amount(1299.99)
        .date(LocalDate.of(2025, 9, 25))
        .customerName("John Smith")
        .build());

    sales.add(Sale.builder()
        .id(UUID.randomUUID())
        .productName("Smartphone")
        .amount(799.99)
        .date(LocalDate.of(2025, 9, 26))
        .customerName("Jane Doe")
        .build());

    sales.add(Sale.builder()
        .id(UUID.randomUUID())
        .productName("Headphones")
        .amount(199.99)
        .date(LocalDate.of(2025, 9, 27))
        .customerName("Bob Johnson")
        .build());

    // Create the report service (Subject)
    ReportService reportService = new ReportService();
    
    // Register observers for each export format
    ReportObserver csvObserver = new CsvReportObserver();
    ReportObserver pdfObserver = new PdfReportObserver();
    ReportObserver jsonObserver = new JsonReportObserver();
    ReportObserver xmlObserver = new XmlReportObserver();

    reportService.registerObserver(csvObserver);
    reportService.registerObserver(pdfObserver);
    reportService.registerObserver(jsonObserver);
    reportService.registerObserver(xmlObserver);

    // Export the sales data, which will notify all observers
    IO.println("Exporting with all observers...");
    reportService.export(sales);
    
    // Remove an observer
    IO.println("\nRemoving CSV observer...");
    reportService.removeObserver(csvObserver);

    // Export again
    IO.println("\nExporting again after removing CSV observer...");
    reportService.export(sales);
  }
}
