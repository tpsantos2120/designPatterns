package org.java.exporter;

import java.util.List;
import org.java.model.Sale;

/**
 * Interface for report exporters. Part of the Factory Pattern - defines the contract for all
 * concrete exporters.
 */
public interface ReportExporter {

  /**
   * Export sales data to the format specific to the implementation.
   *
   * @param sales           List of sales to export
   * @param outputDirectory Directory where the file should be saved
   */
  void export(List<Sale> sales, String outputDirectory);

  /**
   * Get the format type this exporter handles.
   *
   * @return The export format
   */
  String getFormat();
}
