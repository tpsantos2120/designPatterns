package org.java.observerPatternV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Observer Pattern V2 demonstration.
 * 
 * This application demonstrates the Observer Pattern with:
 * - Subject: ReportService (maintains state and notifies observers)
 * - Observers: CsvReportListener, JsonReportListener, PdfReportListener, XmlReportListener
 * - Controller: ReportController (handles HTTP requests)
 * 
 * Observers are registered automatically via ObserverConfig.
 * When sales data is exported, all registered observers are notified and pull data from the subject.
 * 
 * API Usage:
 * POST /api/reports/export - Export sales reports in all formats
 * GET /api/reports/current - Get current sales data
 */
@SpringBootApplication
public class ObserverPatternApplication {

    void main(String[] args) {
        SpringApplication.run(ObserverPatternApplication.class, args);
    }
}
