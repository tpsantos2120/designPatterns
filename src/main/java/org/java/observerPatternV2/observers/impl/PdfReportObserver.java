package org.java.observerPatternV2.observers.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.model.Sale;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer that generates PDF reports. Follows pure Observer Pattern by pulling data from
 * the subject.
 */
@Component
public class PdfReportObserver implements Observer {


  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final NumberFormat CURRENCY_FORMATTER =
      NumberFormat.getCurrencyInstance(Locale.UK);
  public static final String REPORTS_SALES_REPORT_PDF = "/sales_report.pdf";
  public static final String DIR_PATH = "reports";


  @Override
  public void update(EventType eventType, List<Sale> sales) {
    createDirectoryIfNotFound();
    generatePdfReport(sales);
  }

  @Override
  public void delete(EventType eventType) {
    try {
      Files.deleteIfExists(Paths.get(DIR_PATH, REPORTS_SALES_REPORT_PDF));
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
   * Generates a PDF report for the provided sales data and saves it to the specified output
   * directory. The report includes a tabular representation of sales transactions with headers: ID,
   * Product Name, Amount, Date, and Customer Name. Additionally, a summary section at the end of
   * the report provides the total sales amount and the number of sales records.
   *
   * @param sales           the list of sales transactions to be included in the report
   * @param outputDirectory the directory path where the PDF report should be saved
   * @throws RuntimeException if there is an error during the PDF generation process
   */
  private void generatePdfReport(List<Sale> sales) {
    Path path = Paths.get(DIR_PATH, REPORTS_SALES_REPORT_PDF);

    Document document = new Document();
    try {
      PdfWriter.getInstance(document, new FileOutputStream(path.toFile()));
      document.open();

      // Add title
      Font
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
      double totalAmount =
          sales.stream().mapToDouble(Sale::amount).sum();
      Paragraph summary =
          new Paragraph(String.format("\nTotal Sales: %s", CURRENCY_FORMATTER.format(totalAmount)));
      summary.setSpacingBefore(10);
      document.add(summary);

      Paragraph recordCount = new Paragraph(String.format("Number of Sales: %d", sales.size()));
      document.add(recordCount);

      System.out.println("PDF report exported successfully to: " + path);
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
    amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    amountCell.setPadding(5);
    table.addCell(amountCell);

    // Date
    PdfPCell dateCell = new PdfPCell(new Phrase(DATE_FORMATTER.format(sale.date()), cellFont));
    dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    dateCell.setPadding(5);
    table.addCell(dateCell);

    // Customer Name
    PdfPCell customerCell = new PdfPCell(new Phrase(sale.customerName(), cellFont));
    customerCell.setPadding(5);
    table.addCell(customerCell);
  }

  private void addTableHeader(PdfPTable table) {
    Font
        headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

    String[] headers = {"ID", "Product Name", "Amount", "Date", "Customer Name"};
    for (String header : headers) {
      PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
      cell.setBackgroundColor(BaseColor.DARK_GRAY);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setPadding(5);
      table.addCell(cell);
    }
  }
}
