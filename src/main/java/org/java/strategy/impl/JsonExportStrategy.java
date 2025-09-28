package org.java.strategy.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.java.model.Sale;
import org.java.strategy.ExportStrategy;

/**
 * A concrete implementation of the ExportStrategy interface to export sales data as a JSON file.
 */
public class JsonExportStrategy implements ExportStrategy {

  private static final String SALES_REPORT_JSON = "/sales_report.json";

  /**
   * Exports a list of sales records to a JSON file in the specified output directory.
   *
   * @param sales           the list of sales records to export
   * @param outputDirectory the directory where the JSON file will be created
   * @throws RuntimeException if there is an issue during the export process
   */
  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + SALES_REPORT_JSON;

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      objectMapper.writerWithDefaultPrettyPrinter().writeValue(
          Paths.get(filename).toFile(), sales);

      IO.println("JSON report exported successfully to: " + filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to export JSON: " + e.getMessage(), e);
    }
  }
}
