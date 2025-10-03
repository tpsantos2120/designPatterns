package org.java.observerPatternV2.observers.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.subject.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Concrete Observer that generates CSV reports. Follows pure Observer Pattern by pulling data from
 * the subject.
 */
@Component
public class CsvReportObserver implements Observer {

  public static final String SALES_REPORT_CSV = "/sales_report.csv";
  public static final String DIR_PATH = "reports";

  @Override
  public void update(EventType eventType, List<Sale> sales) {
    createDirectoryIfNotFound();
    generateCsvReport(sales);
  }

  @Override
  public void delete(EventType eventType) {
    try {
      Files.deleteIfExists(Paths.get(DIR_PATH, SALES_REPORT_CSV));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void createDirectoryIfNotFound() {
    try {
      Path dirPath = Paths.get(DIR_PATH);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create output directory: " + e.getMessage(), e);
    }
  }

  /**
   * Generates a CSV report file for the provided list of sales and writes it to the specified
   * output directory.
   *
   * @param sales the list of Sale objects containing data to be written into the CSV report
   */
  private void generateCsvReport(List<Sale> sales) {
    Path path = Paths.get(DIR_PATH, SALES_REPORT_CSV);
    try (FileWriter writer = new FileWriter(path.toFile())) {
      // Write CSV header
      writer.append("ID,Product Name,Amount,Date,Customer Name\n");

      // Write sales data
      for (Sale sale : sales) {
        writer.append(String.valueOf(sale.id())).append(",")
            .append(sale.productName()).append(",")
            .append(String.format("%.2f", sale.amount())).append(",")
            .append(sale.date().toString()).append(",")
            .append(sale.customerName()).append("\n");
      }

    } catch (IOException e) {
      throw new RuntimeException("Failed to export CSV: " + e.getMessage(), e);
    }
  }
}
