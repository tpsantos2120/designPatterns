package org.java.exporter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.java.exporter.ReportExporter;
import org.java.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JSON-based implementation of the {@link ReportExporter} interface. This class is responsible for
 * exporting a list of sales data into a JSON file.
 */
@Component
public class JsonExporter implements ReportExporter {

  private static final Logger log = LoggerFactory.getLogger(JsonExporter.class);
  private final ObjectMapper objectMapper;

  public JsonExporter() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.json";

    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(
          Paths.get(filename).toFile(), sales);

    } catch (IOException e) {
      log.error("Failed to export JSON", e);
      throw new RuntimeException("Failed to export JSON: " + e.getMessage(), e);
    }
  }

  @Override
  public String getFormat() {
    return "JSON";
  }
}
