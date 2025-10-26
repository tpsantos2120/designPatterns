package org.java.exporter.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.java.exporter.ReportExporter;
import org.java.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A concrete implementation of the {@link ReportExporter} interface that exports sales data to a
 * CSV format. This class is intended to generate a CSV report containing details about sales
 * transactions and save it to a specified output directory.
 */
@Component
public class CsvExporter implements ReportExporter {

  private static final Logger log = LoggerFactory.getLogger(CsvExporter.class);

  @Override
  public void export(List<Sale> sales, String outputDirectory) {
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

    } catch (IOException e) {
      log.error("Failed to export CSV", e);
      throw new RuntimeException("Failed to export CSV: " + e.getMessage(), e);
    }
  }

  @Override
  public String getFormat() {
    return "CSV";
  }

  private String escapeSpecialCharacters(String text) {
    if (text == null) {
      return "";
    }
    // Escape quotes with double quotes for CSV
    return text.replace("\"", "\"\"").contains(",") ? "\"" + text + "\"" : text;
  }
}
