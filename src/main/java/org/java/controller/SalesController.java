package org.java.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.java.model.ExportRequest;
import org.java.model.Sale;
import org.java.service.ReportService;
import org.java.singleton.ReportMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The SalesController class provides REST endpoints for managing sales-related operations such as
 * exporting sales data, retrieving supported export formats, and accessing system metrics. It
 * interacts with the {@link ReportService} for processing the requests and handles the flow of
 * exporting sales reports into various formats.
 */
@RestController
@RequestMapping("/api/sales")
public class SalesController {

  private static final Logger log = LoggerFactory.getLogger(SalesController.class);

  private final ReportService reportService;

  public SalesController(ReportService reportService) {
    this.reportService = reportService;
  }

  /**
   * Exports sales data in the specified format. The request contains the sales data to export along
   * with the export format (e.g., CSV, JSON, XML, PDF). Upon successful export, the method returns
   * a response indicating success along with the specified format. In case of errors (e.g., invalid
   * format or internal issues), an appropriate error response is returned.
   *
   * @param request the export request containing the sales data and desired export format; must not
   *                be null, must contain non-empty sales data, and must specify a valid export
   *                format
   * @return a ResponseEntity containing a map with the export status and message; will include
   * success or error details based on the outcome of the operation
   */
  @PostMapping("/export")
  public ResponseEntity<Map<String, String>> exportSales(
      @Valid @RequestBody ExportRequest request) {
    log.info("Received export request for format: {}", request.format());

    try {
      reportService.export(request.sales(), request.format());
      return ResponseEntity.ok(Map.of(
          "status", "success",
          "message", "Sales report exported successfully to " + request.format() + " format",
          "format", request.format().toString()
      ));
    } catch (IllegalArgumentException e) {
      log.error("Invalid export format: {}", request.format(), e);
      return ResponseEntity.badRequest().body(Map.of(
          "status", "error",
          "message", "Invalid format: " + e.getMessage()
      ));
    } catch (Exception e) {
      log.error("Error exporting sales", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
          "status", "error",
          "message", "Failed to export: " + e.getMessage()
      ));
    }
  }

  /**
   * Retrieves a list of supported export formats. The response contains a map with the list of
   * format names under the key "formats" and the total count of formats under the key "count".
   *
   * @return a ResponseEntity containing a map with supported formats and their count
   */
  @GetMapping("/formats")
  public ResponseEntity<Map<String, Object>> getSupportedFormats() {
    List<String> formats = reportService.getSupportedFormats();
    return ResponseEntity.ok(Map.of(
        "formats", formats,
        "count", formats.size()
    ));
  }

  /**
   * Retrieves a summary of metrics from the Singleton instance of ReportMetricsCollector. This
   * method provides insight into the system's reporting metrics.
   *
   * @return a ResponseEntity containing the metrics summary as an instance of
   * ReportMetricsCollector.MetricsSummary.
   */
  @GetMapping("/metrics")
  public ResponseEntity<ReportMetricsCollector.MetricsSummary> getMetrics() {
    log.info("Fetching metrics from Singleton ReportMetricsCollector");

    // Access the Singleton instance - ReportMetricsCollector.INSTANCE
    // This demonstrates the Singleton Pattern in action
    ReportMetricsCollector.MetricsSummary metrics =
        ReportMetricsCollector.INSTANCE.getSummary();

    return ResponseEntity.ok(metrics);
  }

  /**
   * Returns sample sales data for testing purposes. This endpoint demonstrates the BUILDER PATTERN
   * by using Sale.builder() to construct Sale objects with a fluent interface.
   *
   * @return a ResponseEntity containing a list of sample Sale objects
   */
  @GetMapping("/sample")
  public ResponseEntity<List<Sale>> getSampleSales() {
    log.info("Generating sample sales data using Builder Pattern");

    // Demonstrate BUILDER PATTERN - constructing Sale objects using the builder
    List<Sale> sampleSales = List.of(
        Sale.builder()
            .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
            .productName("Laptop")
            .amount(1299.99)
            .date(LocalDate.of(2024, 10, 19))
            .customerName("John Smith")
            .build(),

        Sale.builder()
            .id(UUID.randomUUID())
            .productName("Wireless Mouse")
            .amount(29.99)
            .date(LocalDate.now())
            .customerName("Jane Doe")
            .build(),

        Sale.builder()
            .id(UUID.randomUUID())
            .productName("USB-C Cable")
            .amount(15.50)
            .date(LocalDate.now().minusDays(1))
            .customerName("Bob Johnson")
            .build()
    );

    return ResponseEntity.ok(sampleSales);
  }
}
