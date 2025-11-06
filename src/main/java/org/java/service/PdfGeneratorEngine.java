package org.java.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java.model.Resident;
import org.java.model.Transaction;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PdfGeneratorEngine {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public byte[] generate(Map<String, Object> templateData, PdfConfiguration config) {
    log.info("Generating PDF with template: {}", config.getTemplate());

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      PdfWriter writer = new PdfWriter(baos);
      PdfDocument pdfDoc = new PdfDocument(writer);
      Document document = new Document(pdfDoc);

      // Title
      document.add(new Paragraph("CondoConnect - Financial Report")
          .setFontSize(20)
          .setBold()
          .setTextAlignment(TextAlignment.CENTER));

      document.add(new Paragraph("\n"));

      // Period information
      String period = (String) templateData.get("period");
      document.add(new Paragraph("Period: " + period)
          .setFontSize(12));

      document.add(new Paragraph("\n"));

      // Summary section
      addSummarySection(document, templateData);

      // Transaction table
      @SuppressWarnings("unchecked")
      List<Transaction> transactions = (List<Transaction>) templateData.get("transactions");
      addTransactionsTable(document, transactions);

      // Tax information if present
      TaxCalculationService.TaxCalculationResult taxes =
          (TaxCalculationService.TaxCalculationResult) templateData.get("taxes");
      if (taxes != null) {
        addTaxSection(document, taxes);
      }

      // Residents if present
      @SuppressWarnings("unchecked")
      List<Resident> residents = (List<Resident>) templateData.get("residents");
      if (residents != null && !residents.isEmpty()) {
        addResidentsSection(document, residents);
      }

      // Footer
      document.add(new Paragraph("\n\n"));
      document.add(new Paragraph("Generated on: " + LocalDate.now().format(DATE_FORMATTER))
          .setFontSize(10)
          .setTextAlignment(TextAlignment.RIGHT));

      document.close();

      log.info("PDF generated successfully, size: {} bytes", baos.size());
      return baos.toByteArray();

    } catch (Exception e) {
      log.error("Failed to generate PDF", e);
      throw new PdfGenerationException("Failed to generate PDF document", e);
    }
  }

  private void addSummarySection(Document document, Map<String, Object> templateData) {
    document.add(new Paragraph("Financial Summary")
        .setFontSize(16)
        .setBold());

    // Calculate summary from transactions
    @SuppressWarnings("unchecked")
    List<Transaction> transactions = (List<Transaction>) templateData.get("transactions");

    BigDecimal totalRevenue = transactions.stream()
        .filter(t -> t.getType() == Transaction.TransactionType.REVENUE)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalExpenses = transactions.stream()
        .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal netBalance = totalRevenue.subtract(totalExpenses);

    document.add(new Paragraph("Total Revenue: R$ " + totalRevenue));
    document.add(new Paragraph("Total Expenses: R$ " + totalExpenses));
    document.add(new Paragraph("Net Balance: R$ " + netBalance)
        .setBold());

    document.add(new Paragraph("\n"));
  }

  private void addTransactionsTable(Document document, List<Transaction> transactions) {
    document.add(new Paragraph("Transactions")
        .setFontSize(16)
        .setBold());

    Table table = new Table(new float[] {2, 3, 2, 2, 2});
    table.setWidth(500);

    // Headers
    table.addHeaderCell("Date");
    table.addHeaderCell("Description");
    table.addHeaderCell("Type");
    table.addHeaderCell("Category");
    table.addHeaderCell("Amount (R$)");

    // Data
    for (Transaction t : transactions) {
      table.addCell(t.getDate().format(DATE_FORMATTER));
      table.addCell(t.getDescription());
      table.addCell(t.getType().toString());
      table.addCell(t.getCategory() != null ? t.getCategory() : "-");
      table.addCell(t.getAmount().toString());
    }

    document.add(table);
    document.add(new Paragraph("\n"));
  }

  private void addTaxSection(Document document, TaxCalculationService.TaxCalculationResult taxes) {
    document.add(new Paragraph("Tax Information")
        .setFontSize(16)
        .setBold());

    document.add(new Paragraph("Taxable Amount: R$ " + taxes.getTaxableAmount()));
    document.add(
        new Paragraph("Tax Rate: " + taxes.getTaxRate().multiply(new BigDecimal("100")) + "%"));
    document.add(new Paragraph("Total Tax: R$ " + taxes.getTotalTax())
        .setBold());
    document.add(new Paragraph("Jurisdiction: " + taxes.getJurisdiction()));

    document.add(new Paragraph("\n"));
  }

  private void addResidentsSection(Document document, List<Resident> residents) {
    document.add(new Paragraph("Active Residents")
        .setFontSize(16)
        .setBold());

    Table table = new Table(new float[] {3, 3, 2, 2});
    table.setWidth(400);

    // Headers
    table.addHeaderCell("Name");
    table.addHeaderCell("Email");
    table.addHeaderCell("Unit");
    table.addHeaderCell("Status");

    // Data
    for (Resident r : residents) {
      table.addCell(r.getName());
      table.addCell(r.getEmail());
      table.addCell(r.getUnitNumber());
      table.addCell(r.getStatus().toString());
    }

    document.add(table);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PdfConfiguration {
    private String template;
    private PageOrientation orientation;
    private boolean includeWatermark;
  }

  public enum PageOrientation {
    PORTRAIT, LANDSCAPE
  }

  public static class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
