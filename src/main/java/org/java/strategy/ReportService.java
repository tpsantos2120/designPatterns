package org.java.strategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.java.enums.ExportFormat;
import org.java.model.Sale;

/**
 * Service class responsible for exporting sales data into various formats. The export formats
 * supported are CSV, JSON, XML, and PDF. This class uses the Strategy design pattern, delegating
 * the specific export functionality to appropriate implementations of the {@link ExportStrategy}
 * interface.
 */
public class ReportService {

  private static final String DEFAULT_OUTPUT_DIR = "reports";
  private final Map<ExportFormat, ExportStrategy> strategies;

  public ReportService(Map<ExportFormat, ExportStrategy> strategies) {
    // Initialize and register all available strategies
    this.strategies = strategies;
  }

  /**
   * Exports the given list of sales data in the specified format to the default output directory.
   *
   * @param sales  the list of sales records to export
   * @param format the format in which the data should be exported (e.g., CSV, JSON, XML, PDF)
   */
  public void export(List<Sale> sales, ExportFormat format) {
    export(sales, format, DEFAULT_OUTPUT_DIR);
  }

  /**
   * Exports the provided list of sales data into the specified format and saves it in the given
   * output directory. This method selects the appropriate export strategy based on the specified
   * format and delegates the export task to the chosen strategy.
   *
   * @param sales           the list of sales records to be exported. Each record is expected to
   *                        include an ID, product name, amount, date, and customer name.
   * @param format          the desired format for the export (e.g., CSV, JSON, XML, PDF). It must
   *                        correspond to a supported export strategy.
   * @param outputDirectory the path to the directory where the exported file should be saved. If
   *                        the directory does not exist, it will be created.
   * @throws IllegalArgumentException if the specified export format is not supported.
   * @throws RuntimeException         if there is an error creating the output directory or during
   *                                  the export process.
   */
  public void export(List<Sale> sales, ExportFormat format, String outputDirectory) {
    // 1. Find the correct strategy
    ExportStrategy strategy = strategies.get(format);
    if (strategy == null) {
      throw new IllegalArgumentException("Unsupported export format: " + format);
    }

    try {
      Path dirPath = Paths.get(outputDirectory);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }
      // 2. Delegate the export task to the strategy object
      strategy.export(sales, outputDirectory);

    } catch (IOException e) {
      throw new RuntimeException("Failed to create output directory: " + e.getMessage(), e);
    }
  }
}
