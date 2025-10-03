package org.java.observerPatternV2.controller;

import java.util.Objects;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.observers.impl.CsvReportObserver;
import org.java.observerPatternV2.observers.impl.JsonReportObserver;
import org.java.observerPatternV2.observers.impl.PdfReportObserver;
import org.java.observerPatternV2.observers.impl.XmlReportObserver;
import org.java.observerPatternV2.service.Report;
import org.java.observerPatternV2.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for report generation. Delegates to ReportService which notifies observers.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

  private final ReportService service;

  public ReportController(ReportService service) {
    this.service = service;
  }

  /**
   * Updates the sales reports by processing the provided sales data and exporting them
   * through the observer-based reporting mechanism.
   *
   * @param sales the list of {@link Sale} objects containing the sales data to be processed
   *              for report updates. Must not be null or empty.
   * @return a ResponseEntity containing a message indicating the result of the operation.
   *         Returns a bad request response if the input sales data is invalid.
   */
  @PostMapping("/export")
  public ResponseEntity<String> updateReports(@RequestBody List<Sale> sales) {
    if (Objects.isNull(sales) || sales.isEmpty()) {
      return ResponseEntity.badRequest().body("Sales data cannot be empty");
    }

    service.updateReportsV1(sales);
    service.updateReportsV2(sales);
    service.deleteReports();

    return ResponseEntity.ok("Reports exported successfully. Check the reports directory.");
  }
}
