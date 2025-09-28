package org.java.export;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.java.model.Sale;
import org.java.enums.ExportFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ReportService {

  private static final String DEFAULT_OUTPUT_DIR = "reports";
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final NumberFormat CURRENCY_FORMATTER =
      NumberFormat.getCurrencyInstance(Locale.UK);


  public void export(List<Sale> sales, ExportFormat format) {
    export(sales, format, DEFAULT_OUTPUT_DIR);
  }

  public void export(List<Sale> sales, ExportFormat format, String outputDirectory) {
    try {
      Path dirPath = Paths.get(outputDirectory);
      if (!Files.exists(dirPath)) {
        Files.createDirectories(dirPath);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create output directory: " + e.getMessage(), e);
    }

    switch (format) {
      case CSV -> exportCsv(sales, outputDirectory);
      case JSON -> exportJson(sales, outputDirectory);
      case XML -> exportXml(sales, outputDirectory);
      case PDF -> exportPdf(sales, outputDirectory);
      default -> throw new IllegalArgumentException("Unsupported export format: " + format);
    }
  }

  private void exportCsv(List<Sale> sales, String outputDirectory) {
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

      IO.println("CSV report exported successfully to: " + filename);
    } catch (IOException e) {
      throw new RuntimeException("Failed to export CSV: " + e.getMessage(), e);
    }
  }

  private void exportJson(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.json";

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

  private void exportXml(List<Sale> sales, String outputDirectory) {
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

  private String escapeSpecialCharacters(String text) {
    if (text == null) {
      return "";
    }
    // Escape quotes with double quotes for CSV
    return text.replace("\"", "\"\"").contains(",") ? "\"" + text + "\"" : text;
  }

  private void exportPdf(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.pdf";

    com.itextpdf.text.Document document = new com.itextpdf.text.Document();
    try {
      PdfWriter.getInstance(document, new FileOutputStream(filename));
      document.open();

      // Add title
      com.itextpdf.text.Font
          titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
      Paragraph title = new Paragraph("Sales Report", titleFont);
      title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      title.setSpacingAfter(20);
      document.add(title);

      // Create a table with 5 columns
      PdfPTable table = new PdfPTable(5);
      table.setWidthPercentage(100);

      // Set column widths (percentages of the page width)
      float[] columnWidths = {20f, 25f, 15f, 15f, 25f};
      table.setWidths(columnWidths);

      // Add table headers
      addTableHeader(table);

      // Add table rows
      for (Sale sale : sales) {
        addTableRow(table, sale);
      }

      // Add table to document
      document.add(table);

      // Add summary information
      double totalAmount = sales.stream().mapToDouble(Sale::amount).sum();
      Paragraph summary =
          new Paragraph(String.format("\nTotal Sales: %s", CURRENCY_FORMATTER.format(totalAmount)));
      summary.setSpacingBefore(10);
      document.add(summary);

      Paragraph recordCount = new Paragraph(String.format("Number of Sales: %d", sales.size()));
      document.add(recordCount);

      IO.println("PDF report exported successfully to: " + filename);
    } catch (DocumentException | IOException e) {
      throw new RuntimeException("Failed to export PDF: " + e.getMessage(), e);
    } finally {
      if (document.isOpen()) {
        document.close();
      }
    }
  }

  private void addTableRow(PdfPTable table, Sale sale) {
    Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

    // ID
    PdfPCell idCell = new PdfPCell(new Phrase(sale.id().toString(), cellFont));
    idCell.setPadding(5);
    table.addCell(idCell);

    // Product Name
    PdfPCell productCell = new PdfPCell(new Phrase(sale.productName(), cellFont));
    productCell.setPadding(5);
    table.addCell(productCell);

    // Amount
    PdfPCell amountCell =
        new PdfPCell(new Phrase(CURRENCY_FORMATTER.format(sale.amount()), cellFont));
    amountCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
    amountCell.setPadding(5);
    table.addCell(amountCell);

    // Date
    PdfPCell dateCell = new PdfPCell(new Phrase(DATE_FORMATTER.format(sale.date()), cellFont));
    dateCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
    dateCell.setPadding(5);
    table.addCell(dateCell);

    // Customer Name
    PdfPCell customerCell = new PdfPCell(new Phrase(sale.customerName(), cellFont));
    customerCell.setPadding(5);
    table.addCell(customerCell);
  }

  private void addTableHeader(PdfPTable table) {
    com.itextpdf.text.Font
        headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

    String[] headers = {"ID", "Product Name", "Amount", "Date", "Customer Name"};
    for (String header : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
      cell.setBackgroundColor(BaseColor.DARK_GRAY);
      cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }
  }
}
