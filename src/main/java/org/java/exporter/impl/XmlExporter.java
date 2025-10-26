package org.java.exporter.impl;

import java.io.FileWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.java.exporter.ReportExporter;
import org.java.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Concrete implementation of the {@link ReportExporter} interface for exporting sales data in XML
 * format. This class uses the DOM (Document Object Model) API to construct and write an XML file
 * containing the list of sales.
 */
@Component
public class XmlExporter implements ReportExporter {

  private static final Logger log = LoggerFactory.getLogger(XmlExporter.class);

  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.xml";

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

        // Add sale properties
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
      StreamResult result = new StreamResult(new FileWriter(filename));

      transformer.transform(source, result);

    } catch (Exception e) {
      log.error("Failed to export XML", e);
      throw new RuntimeException("Failed to export XML: " + e.getMessage(), e);
    }
  }

  @Override
  public String getFormat() {
    return "XML";
  }

  private void appendTextElement(Document doc, Element parent, String name, String value) {
    Element element = doc.createElement(name);
    element.appendChild(doc.createTextNode(value));
    parent.appendChild(element);
  }
}
