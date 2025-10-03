
package org.java.observerPatternV1.observers.impl;

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
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.java.observerPatternV1.model.Sale;
import org.java.observerPatternV1.observers.ReportObserver;

/**
 * Observer that exports sales data to PDF format.
 */
public class PdfReportObserver implements ReportObserver {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final NumberFormat CURRENCY_FORMATTER =
      NumberFormat.getCurrencyInstance(Locale.UK);

  @Override
  public void update(List<Sale> sales, String outputDirectory) {
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

      System.out.println("PDF report exported successfully to: " + filename);
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
