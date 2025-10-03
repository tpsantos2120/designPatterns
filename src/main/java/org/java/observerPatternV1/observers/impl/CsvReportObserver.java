package org.java.observerPatternV1.observers.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;

/**
 * Observer that exports sales data to CSV format.
 */
public class CsvReportObserver implements ReportObserver {

  @Override
  public void update(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.csv";

    try (FileWriter writer = new FileWriter(filename)) {
      // Write CSV header
      writer.write("ID,Product Name,Amount,Date,Customer Name\n");

      // Write each sale as a CSV line
      for (Sale sale : sales) {
        String line = String.format("%s,%s,%.2f,%s,%s\n",
            sale.id().toString(),
            escapeSpecialCharacters(sale.productName()),
            sale.amount(),
            sale.date().toString(),
            escapeSpecialCharacters(sale.customerName()));
        writer.write(line);
      }

      System.out.println("CSV report exported successfully to: " + filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to export CSV: " + e.getMessage(), e);
    }
  }

  private String escapeSpecialCharacters(String text) {
    if (text == null) {
      return "";
    }
    // Escape quotes with double quotes for CSV
    return text.replace("\"", "\"\"").contains(",") ? "\"" + text + "\"" : text;
  }
}
