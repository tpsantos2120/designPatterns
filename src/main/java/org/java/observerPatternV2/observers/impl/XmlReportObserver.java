package org.java.observerPatternV2.observers.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.model.Sale;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.util.List;

/**
 * Concrete Observer that generates XML reports. Follows pure Observer Pattern by pulling data from
 * the subject.
 */
@Component
public class XmlReportObserver implements Observer {

  public static final String REPORTS_SALES_REPORT_XML = "/sales_report.xml";
  public static final String DIR_PATH = "reports";

  @Override
  public void update(EventType eventType, List<Sale> sales) {
    createDirectoryIfNotFound();
    generateXmlReport(sales);
  }

  @Override
  public void delete(EventType eventType) {
    try {
      Files.deleteIfExists(Paths.get(DIR_PATH, REPORTS_SALES_REPORT_XML));
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
   * Generates an XML report file for the provided list of sales and writes it to the specified
   * output directory.
   *
   * @param sales the list of Sale objects containing data to be written into the XML report
   */
  private void generateXmlReport(List<Sale> sales) {
    Path path = Paths.get(DIR_PATH, REPORTS_SALES_REPORT_XML);

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // Root element
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("sales");
      doc.appendChild(rootElement);

      // Add each sale as an XML element
      for (Sale sale : sales) {
        Element saleElement = doc.createElement("sale");
        rootElement.appendChild(saleElement);

        appendTextElement(doc, saleElement, "id", sale.id().toString());
        appendTextElement(doc, saleElement, "productName", sale.productName());
        appendTextElement(doc, saleElement, "amount", String.format("%.2f", sale.amount()));
        appendTextElement(doc, saleElement, "date", sale.date().toString());
        appendTextElement(doc, saleElement, "customerName", sale.customerName());
      }

      // Write the XML content to the file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new FileWriter(path.toFile()));

      transformer.transform(source, result);

      System.out.println("XML report exported successfully to: " + path);
    } catch (Exception e) {
      throw new RuntimeException("Failed to export XML: " + e.getMessage(), e);
    }
  }

  private void appendTextElement(Document doc, Element parent, String name, String value) {
    Element element = doc.createElement(name);
    element.appendChild(doc.createTextNode(value));
    parent.appendChild(element);
  }
}
