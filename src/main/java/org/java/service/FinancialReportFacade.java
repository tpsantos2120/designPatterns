package org.java.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java.dto.FinancialReport;
import org.java.dto.FinancialReportRequest;
import org.java.dto.ReportDeliveryConfirmation;
import org.java.dto.ReportMetadata;
import org.java.exceptions.ReportGenerationException;
import org.java.model.Resident;
import org.java.model.Transaction;
import org.java.repository.ResidentRepository;
import org.java.repository.TransactionRepository;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinancialReportFacade implements ReportFacade {

  private final TransactionRepository transactionRepository;
  private final ResidentRepository residentRepository;
  private final TaxCalculationService taxService;
  private final PdfGeneratorEngine pdfEngine;
  private final EmailServiceProvider emailProvider;
  private final AuditLogger auditLogger;
  private final CacheManager cacheManager;
  private final ReportIdGenerator reportIdGenerator;
  private final MeterRegistry meterRegistry;

  @Override
  public FinancialReport generateReport(FinancialReportRequest request) {
    Timer.Sample sample = Timer.start(meterRegistry);

    log.info("Generating financial report for condo: {}", request.getCondoId());

    try {
      // Check cache first
      Optional<FinancialReport> cachedReport = getCachedReport(
          request.getCondoId(),
          request.getEndDate()
      );

      if (cachedReport.isPresent()) {
        log.info("Returning cached report for condo: {}", request.getCondoId());
        recordMetrics(sample, "success", "cached");
        return cachedReport.get();
      }

      // Fetch data from repositories
      ReportData reportData = fetchReportData(request);

      // Calculate taxes if requested
      TaxCalculationService.TaxCalculationResult taxResult = null;
      if (request.isIncludeTaxBreakdown()) {
        taxResult = calculateTaxes(reportData.getTransactions());
      }

      // Generate PDF document
      byte[] pdfContent = generatePdfDocument(reportData, taxResult, request);

      // Build metadata
      ReportMetadata metadata = buildMetadata(reportData, taxResult);

      // Create report object
      String reportId = reportIdGenerator.generate();
      FinancialReport report = FinancialReport.builder()
          .reportId(reportId)
          .condoId(request.getCondoId())
          .generatedAt(LocalDate.now())
          .content(pdfContent)
          .format(request.getFormat())
          .metadata(metadata)
          .build();

      // Cache the report
      cacheReport(report);

      // Log audit trail
      logAuditTrail(request, reportId);

      log.info("Successfully generated report: {}", reportId);
      recordMetrics(sample, "success", "generated");

      return report;

    } catch (Exception e) {
      log.error("Failed to generate report for condo: {}", request.getCondoId(), e);
      recordMetrics(sample, "failure", "error");
      throw new ReportGenerationException("Failed to generate financial report", e);
    }
  }

  @Override
  public ReportDeliveryConfirmation generateAndEmailReport(
      FinancialReportRequest request,
      List<String> recipients) {

    log.info("Generating and emailing report to {} recipients", recipients.size());

    // Generate the report
    FinancialReport report = generateReport(request);

    // Send email to recipients
    List<String> successfulRecipients = new ArrayList<>();
    List<String> failedRecipients = new ArrayList<>();

    for (String recipient : recipients) {
      try {
        sendReportEmail(report, recipient);
        successfulRecipients.add(recipient);
      } catch (Exception e) {
        log.error("Failed to send report to: {}", recipient, e);
        failedRecipients.add(recipient);
      }
    }

    // Log delivery audit
    logDeliveryAudit(report.getReportId(), successfulRecipients, failedRecipients);

    // Record metrics
    meterRegistry.counter("report.email.sent",
        "status", "success").increment(successfulRecipients.size());
    meterRegistry.counter("report.email.sent",
        "status", "failure").increment(failedRecipients.size());

    return ReportDeliveryConfirmation.builder()
        .reportId(report.getReportId())
        .successfulRecipients(successfulRecipients)
        .failedRecipients(failedRecipients)
        .sentAt(Instant.now())
        .build();
  }

  @Override
  public Optional<FinancialReport> getCachedReport(Long condoId, LocalDate reportDate) {
    String cacheKey = buildCacheKey(condoId, reportDate);
    var cache = cacheManager.getCache("reports");
    if (cache != null) {
      FinancialReport report = cache.get(cacheKey, FinancialReport.class);
      return Optional.ofNullable(report);
    }
    return Optional.empty();
  }

  // Private helper methods to encapsulate subsystem interactions

  private ReportData fetchReportData(FinancialReportRequest request) {
    List<Transaction> transactions = transactionRepository
        .findByCondoIdAndDateRange(
            request.getCondoId(),
            request.getStartDate(),
            request.getEndDate()
        );

    List<Resident> residents = request.isIncludeResidentDetails()
        ? residentRepository.findByCondoId(request.getCondoId())
        : Collections.emptyList();

    log.debug("Fetched {} transactions and {} residents",
        transactions.size(), residents.size());

    return new ReportData(transactions, residents);
  }

  private TaxCalculationService.TaxCalculationResult calculateTaxes(
      List<Transaction> transactions) {
    TaxCalculationService.TaxCalculationRequest taxRequest =
        TaxCalculationService.TaxCalculationRequest.builder()
            .transactions(transactions)
            .jurisdiction("BR")
            .taxYear(LocalDate.now().getYear())
            .build();

    return taxService.calculate(taxRequest);
  }

  private byte[] generatePdfDocument(
      ReportData reportData,
      TaxCalculationService.TaxCalculationResult taxResult,
      FinancialReportRequest request) {

    PdfGeneratorEngine.PdfConfiguration pdfConfig =
        PdfGeneratorEngine.PdfConfiguration.builder()
            .template("financial-report-v3")
            .orientation(PdfGeneratorEngine.PageOrientation.PORTRAIT)
            .includeWatermark(false)
            .build();

    Map<String, Object> templateData = new HashMap<>();
    templateData.put("transactions", reportData.getTransactions());
    templateData.put("residents", reportData.getResidents());
    templateData.put("taxes", taxResult);
    templateData.put("period", formatPeriod(request.getStartDate(), request.getEndDate()));

    return pdfEngine.generate(templateData, pdfConfig);
  }

  private ReportMetadata buildMetadata(
      ReportData reportData,
      TaxCalculationService.TaxCalculationResult taxResult) {

    BigDecimal totalRevenue = reportData.getTransactions().stream()
        .filter(t -> t.getType() == Transaction.TransactionType.REVENUE)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalExpenses = reportData.getTransactions().stream()
        .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    LocalDate periodStart = reportData.getTransactions().stream()
        .map(Transaction::getDate)
        .min(LocalDate::compareTo)
        .orElse(LocalDate.now());

    LocalDate periodEnd = reportData.getTransactions().stream()
        .map(Transaction::getDate)
        .max(LocalDate::compareTo)
        .orElse(LocalDate.now());

    return ReportMetadata.builder()
        .totalRevenue(totalRevenue)
        .totalExpenses(totalExpenses)
        .netBalance(totalRevenue.subtract(totalExpenses))
        .taxAmount(taxResult != null ? taxResult.getTotalTax() : BigDecimal.ZERO)
        .transactionCount(reportData.getTransactions().size())
        .periodStart(periodStart)
        .periodEnd(periodEnd)
        .build();
  }

  private void cacheReport(FinancialReport report) {
    String cacheKey = buildCacheKey(report.getCondoId(), report.getGeneratedAt());
    var cache = cacheManager.getCache("reports");
    if (cache != null) {
      cache.put(cacheKey, report);
      log.debug("Cached report with key: {}", cacheKey);
    }
  }

  private void sendReportEmail(FinancialReport report, String recipient) {
    EmailServiceProvider.EmailMessage message = EmailServiceProvider.EmailMessage.builder()
        .recipient(recipient)
        .subject(String.format("Financial Report - %s", report.getGeneratedAt()))
        .body(buildEmailBody(report))
        .attachment(EmailServiceProvider.EmailAttachment.builder()
            .filename("financial-report.pdf")
            .content(report.getContent())
            .mimeType("application/pdf")
            .build())
        .build();

    emailProvider.send(message);
  }

  private void logAuditTrail(FinancialReportRequest request, String reportId) {
    AuditLogger.AuditEntry auditEntry = AuditLogger.AuditEntry.builder()
        .action("FINANCIAL_REPORT_GENERATED")
        .userId(request.getUserId())
        .resourceType("FINANCIAL_REPORT")
        .resourceId(reportId)
        .condoId(request.getCondoId())
        .timestamp(Instant.now())
        .metadata(Map.of(
            "startDate", request.getStartDate().toString(),
            "endDate", request.getEndDate().toString(),
            "includeTaxes", String.valueOf(request.isIncludeTaxBreakdown())
        ))
        .build();

    auditLogger.log(auditEntry);
  }

  private void logDeliveryAudit(
      String reportId,
      List<String> successful,
      List<String> failed) {

    AuditLogger.AuditEntry auditEntry = AuditLogger.AuditEntry.builder()
        .action("FINANCIAL_REPORT_DELIVERED")
        .resourceType("FINANCIAL_REPORT")
        .resourceId(reportId)
        .timestamp(Instant.now())
        .metadata(Map.of(
            "successfulRecipients", String.valueOf(successful.size()),
            "failedRecipients", String.valueOf(failed.size())
        ))
        .build();

    auditLogger.log(auditEntry);
  }

  private String buildCacheKey(Long condoId, LocalDate date) {
    return String.format("report:financial:%d:%s", condoId, date);
  }

  private String formatPeriod(LocalDate start, LocalDate end) {
    return String.format("%s to %s", start, end);
  }

  private String buildEmailBody(FinancialReport report) {
    return String.format(
        "Dear Administrator,\n\n" +
            "Please find attached the financial report for condominium %d.\n\n" +
            "Report Summary:\n" +
            "- Total Revenue: R$ %s\n" +
            "- Total Expenses: R$ %s\n" +
            "- Net Balance: R$ %s\n" +
            "- Transactions: %d\n\n" +
            "Best regards,\n" +
            "CondoConnect Team",
        report.getCondoId(),
        report.getMetadata().getTotalRevenue(),
        report.getMetadata().getTotalExpenses(),
        report.getMetadata().getNetBalance(),
        report.getMetadata().getTransactionCount()
    );
  }

  private void recordMetrics(Timer.Sample sample, String status, String type) {
    sample.stop(Timer.builder("report.generation.time")
        .tag("status", status)
        .tag("type", type)
        .register(meterRegistry));

    meterRegistry.counter("report.generation.count",
        "status", status,
        "type", type).increment();
  }

  @Data
  @AllArgsConstructor
  private static class ReportData {
    private List<Transaction> transactions;
    private List<Resident> residents;
  }
}