package org.java.exporter.impl;

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
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.java.exporter.ReportExporter;
import org.java.model.ExportFormat;
import org.java.model.Sale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link ReportExporter} interface that generates sales reports in PDF
 * format. This class is responsible for formatting the sales data into a table with multiple
 * columns and adding summary statistics to the document.
 */
@Component
public class PdfExporter implements ReportExporter {

  private static final Logger log = LoggerFactory.getLogger(PdfExporter.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final NumberFormat CURRENCY_FORMATTER =
      NumberFormat.getCurrencyInstance(Locale.UK);

  @Override
  public void export(List<Sale> sales, String outputDirectory) {
    String filename = outputDirectory + "/sales_report.pdf";

    Document document = new Document();
    try {
      PdfWriter.getInstance(document, new FileOutputStream(filename));
      document.open();

      // Add title
      Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
      Paragraph title = new Paragraph("Sales Report", titleFont);
      title.setAlignment(Element.ALIGN_CENTER);
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

    } catch (DocumentException | IOException e) {
      log.error("Failed to export PDF", e);
      throw new RuntimeException("Failed to export PDF: " + e.getMessage(), e);
    } finally {
      if (document.isOpen()) {
        document.close();
      }
    }
  }

  @Override
  public ExportFormat getFormat() {
    return ExportFormat.PDF;
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
    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

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
