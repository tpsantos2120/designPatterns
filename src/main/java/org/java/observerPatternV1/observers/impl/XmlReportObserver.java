package org.java.observerPatternV1.observers.impl;

import java.io.FileWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Observer that exports sales data to XML format.
 */
public class XmlReportObserver implements ReportObserver {

  @Override
  public void update(List<Sale> sales, String outputDirectory) {
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

      System.out.println("XML report exported successfully to: " + filename);
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
