package org.java.factory;

import java.util.List;
import org.java.exporter.ReportExporter;
import org.java.exporter.impl.CsvExporter;
import org.java.exporter.impl.JsonExporter;
import org.java.exporter.impl.PdfExporter;
import org.java.exporter.impl.XmlExporter;
import org.java.model.ExportFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Factory class responsible for providing instances of {@link ReportExporter} based on the desired
 * output format. Implements the Factory Pattern and supports multiple export formats including CSV,
 * JSON, XML, and PDF.
 */
@Component
public class ReportExporterFactory {

  private static final Logger log = LoggerFactory.getLogger(ReportExporterFactory.class);

  private final CsvExporter csvExporter;
  private final JsonExporter jsonExporter;
  private final XmlExporter xmlExporter;
  private final PdfExporter pdfExporter;

  public ReportExporterFactory(CsvExporter csvExporter,
                               JsonExporter jsonExporter,
                               XmlExporter xmlExporter,
                               PdfExporter pdfExporter) {
    this.csvExporter = csvExporter;
    this.jsonExporter = jsonExporter;
    this.xmlExporter = xmlExporter;
    this.pdfExporter = pdfExporter;
  }

  /**
   * Gets the appropriate ReportExporter for the specified format. This is the factory method that
   * demonstrates the Factory Pattern.
   *
   * @param format The desired export format
   * @return The ReportExporter implementation for that format
   * @throws IllegalArgumentException if the format is not supported
   */
  public ReportExporter getExporter(ExportFormat format) {
    log.debug("Creating exporter for format: {}", format);

    return switch (format) {
      case CSV -> csvExporter;
      case JSON -> jsonExporter;
      case XML -> xmlExporter;
      case PDF -> pdfExporter;
    };
  }

  /**
   * Gets list of all supported formats.
   *
   * @return List of supported export formats
   */
  public List<String> getSupportedFormats() {
    return List.of(
        csvExporter.getFormat(),
        jsonExporter.getFormat(),
        xmlExporter.getFormat(),
        pdfExporter.getFormat()
    );
  }
}
