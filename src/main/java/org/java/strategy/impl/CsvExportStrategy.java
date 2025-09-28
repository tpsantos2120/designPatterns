package org.java.strategy.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.java.model.Sale;
import org.java.strategy.ExportStrategy;

/**
 * A strategy implementation for exporting sales data to a CSV file.
 */
public class CsvExportStrategy implements ExportStrategy {

  private static final String SALES_REPORT_CSV = "/sales_report.csv";

  /**
   * Exports a list of sales to a CSV file in the specified output directory.
   *
   * @param sales           the list of sales to export. Each sale must consist of an ID, product
   *                        name, amount, date, and customer name.
   * @param outputDirectory the directory where the CSV file will be created. It should be a valid
   *                        directory path where the application has write permissions.
   * @throws RuntimeException if an IOException occurs during the file writing process.
   */
  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + SALES_REPORT_CSV;

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

      IO.println("CSV report exported successfully to: " + filename);
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
