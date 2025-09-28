package org.java;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.java.enums.ExportFormat;
import org.java.export.ReportService;
import org.java.model.Sale;

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

    ReportService reportService = new ReportService();

    reportService.export(sales, ExportFormat.CSV);
    reportService.export(sales, ExportFormat.JSON);
    reportService.export(sales, ExportFormat.XML);
    reportService.export(sales, ExportFormat.PDF);
  }
}
