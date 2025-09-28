package org.java.strategy.impl;

import java.io.FileWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.java.model.Sale;
import org.java.strategy.ExportStrategy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements the {@link ExportStrategy} interface to export sales data as an XML file.
 */
public class XmlExportStrategy implements ExportStrategy {

  private static final String SALES_REPORT_XML = "/sales_report.xml";

  /**
   * Exports a list of sales records to an XML file in the specified output directory. Each sale in
   * the list is represented as an XML element with individual properties.
   *
   * @param sales           the list of sales to be exported
   * @param outputDirectory the directory where the XML file will be created
   * @throws RuntimeException if an error occurs during the export process
   */
  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + SALES_REPORT_XML;

    try {
      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      // Root element
      Document doc = docBuilder.newDocument();
      Element rootElement = doc.createElement("Sales");
      doc.appendChild(rootElement);

      // Add each sale as an XML element
      for (Sale sale : sales) {
        Element saleElement = doc.createElement("Sale");
        rootElement.appendChild(saleElement);

        // Add sale properties
        appendTextElement(doc, saleElement, "Id", sale.id().toString());
        appendTextElement(doc, saleElement, "ProductName", sale.productName());
        appendTextElement(doc, saleElement, "Amount", String.format("%.2f", sale.amount()));
        appendTextElement(doc, saleElement, "Date", sale.date().toString());
        appendTextElement(doc, saleElement, "CustomerName", sale.customerName());
      }

      // Write the XML content to the file
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();

      // Set output properties for pretty printing
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new FileWriter(filename));

      transformer.transform(source, result);

      IO.println("XML report exported successfully to: " + filename);
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
