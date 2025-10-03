package org.java.observerPatternV2.observers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Concrete Observer that generates JSON reports. Follows pure Observer Pattern by pulling data from
 * the subject.
 */
@Component
public class JsonReportObserver implements Observer {

  public static final String REPORTS_SALES_REPORT_JSON = "/sales_report.json";
  public static final String DIR_PATH = "reports";

  @Override
  public void update(EventType eventType, List<Sale> sales) {
    createDirectoryIfNotFound();
    generateJsonReport(sales);
  }

  @Override
  public void delete(EventType eventType) {
    try {
      Files.deleteIfExists(Paths.get(DIR_PATH, REPORTS_SALES_REPORT_JSON));
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
   * Generates a JSON report of sales data and writes it to the specified output directory.
   *
   * @param sales           the list of sales records to be included in the report
   * @param outputDirectory the directory where the JSON report will be saved
   */
  private void generateJsonReport(List<Sale> sales) {
    Path path = Paths.get(DIR_PATH, REPORTS_SALES_REPORT_JSON);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

      objectMapper.writerWithDefaultPrettyPrinter()
          .writeValue(path.toFile(), sales);

    } catch (IOException e) {
      throw new RuntimeException("Failed to export JSON: " + e.getMessage(), e);
    }
  }
}
