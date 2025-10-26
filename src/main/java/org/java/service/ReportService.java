package org.java.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.java.exporter.ReportExporter;
import org.java.factory.ReportExporterFactory;
import org.java.model.ExportFormat;
import org.java.model.Sale;
import org.java.singleton.ReportMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for exporting sales reports. Demonstrates usage of: - FACTORY PATTERN: Uses
 * ReportExporterFactory to get the appropriate exporter - SINGLETON PATTERN: Uses
 * ReportMetricsCollector.INSTANCE to track metrics - Dependency Injection: Spring dependencies
 * injected via constructor
 */
@Service
public class ReportService {

  private static final Logger log = LoggerFactory.getLogger(ReportService.class);

  private final ReportExporterFactory exporterFactory;

  @Value("${app.reports.output-directory:reports}")
  private String defaultOutputDirectory;

  public ReportService(ReportExporterFactory exporterFactory) {
    this.exporterFactory = exporterFactory;
  }

  /**
   * Export sales data using the default output directory.
   *
   * @param sales  List of sales to export
   * @param format Format for export (CSV, JSON, XML, PDF)
   */
  public void export(List<Sale> sales, ExportFormat format) {
    export(sales, format, defaultOutputDirectory);
  }

  /**
   * Export sales data to a specific directory.
   * Demonstrates:
   * - FACTORY PATTERN: Gets the appropriate exporter from factory
   * - SINGLETON PATTERN: Uses ReportMetricsCollector.INSTANCE to track metrics
   *
   * @param sales           List of sales to export
   * @param format          Format for export
   * @param outputDirectory Directory where the file should be saved
   */
  public void export(List<Sale> sales, ExportFormat format, String outputDirectory) {
    log.info("Exporting {} sales to {} format in directory: {}",
        sales.size(), format, outputDirectory);

    try {
      // Ensure output directory exists
      ensureOutputDirectoryExists(outputDirectory);

      // Use Factory Pattern to get the appropriate exporter
      ReportExporter exporter = exporterFactory.getExporter(format);

      // Export using the selected exporter
      exporter.export(sales, outputDirectory);

      // Use Singleton Pattern - Record successful export metrics
      ReportMetricsCollector.INSTANCE.recordExport(format.toString(), sales.size());

      log.info("Export completed successfully");
    } catch (Exception e) {
      // Use Singleton Pattern - Record failed export
      ReportMetricsCollector.INSTANCE.recordFailure(format.toString(), e.getMessage());
      log.error("Export failed", e);
      throw e;
    }
  }

  /**
   * Ensures the output directory exists, creating it if necessary.
   *
   * @param outputDirectory The directory path to ensure exists
   */
  private void ensureOutputDirectoryExists(String outputDirectory) {
    try {
      Path dirPath = Paths.get(outputDirectory);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
        log.info("Created output directory: {}", outputDirectory);
      }
    } catch (IOException e) {
      log.error("Failed to create output directory: {}", outputDirectory, e);
      throw new RuntimeException("Failed to create output directory: " + e.getMessage(), e);
    }
  }

  /**
   * Get list of supported export formats.
   *
   * @return List of format names
   */
  public List<String> getSupportedFormats() {
    return exporterFactory.getSupportedFormats();
  }
}
