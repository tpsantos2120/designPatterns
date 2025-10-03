package org.java.observerPatternV1.subject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.util.ArrayList;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;

public class ReportService {

  private static final String DEFAULT_OUTPUT_DIR = "reports";

  // List of observers
  private final List<ReportObserver> observers = new ArrayList<>();

  /**
   * Register an observer to receive notifications when sales data is ready.
   *
   * @param observer the observer to register
   */
  public void registerObserver(ReportObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * Remove an observer from the notification list.
   *
   * @param observer the observer to remove
   */
  public void removeObserver(ReportObserver observer) {
    observers.remove(observer);
  }

  /**
   * Export sales data using all registered observers.
   *
   * @param sales the sales data to export
   */
  public void export(List<Sale> sales) {
    export(sales, DEFAULT_OUTPUT_DIR);
  }

  /**
   * Export sales data using all registered observers to the specified directory.
   *
   * @param sales           the sales data to export
   * @param outputDirectory the directory to export to
   */
  public void export(final List<Sale> sales, final String outputDirectory) {
    try {
      Path dirPath = Paths.get(outputDirectory);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create output directory: " + e.getMessage(), e);
    }

    // Notify all observers
    for (final ReportObserver observer : observers) {
      observer.update(sales, outputDirectory);
    }
  }
}
