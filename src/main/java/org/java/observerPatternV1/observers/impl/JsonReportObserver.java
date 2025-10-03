
package org.java.observerPatternV1.observers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;

/**
 * Observer that exports sales data to JSON format.
 */
public class JsonReportObserver implements ReportObserver {

  @Override
  public void update(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.json";

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      objectMapper.writerWithDefaultPrettyPrinter().writeValue(
          Paths.get(filename).toFile(), sales);

      System.out.println("JSON report exported successfully to: " + filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to export JSON: " + e.getMessage(), e);
    }
  }
}
