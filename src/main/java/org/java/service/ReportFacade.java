package org.java.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.java.dto.FinancialReport;
import org.java.dto.FinancialReportRequest;
import org.java.dto.ReportDeliveryConfirmation;

/**
 * Facade for financial report operations. Simplifies complex report generation by coordinating
 * multiple subsystems.
 */
public interface ReportFacade {

  /**
   * Generates a financial report based on the provided request parameters. This method processes
   * and compiles financial data to produce a report in the requested format, including optional
   * details such as tax breakdown and resident details if specified.
   *
   * @param request the request object containing parameters for report generation, including
   *                condominium ID, date range, requesting user ID, report format, and optional
   *                configurations.
   * @return a FinancialReport object containing the generated report details, including metadata
   * and content.
   */
  FinancialReport generateReport(FinancialReportRequest request);

  /**
   * Generates a financial report based on the provided request and sends it to the specified
   * recipients via email. The method compiles financial data according to the requested parameters,
   * generates the report in the appropriate format, and delivers it to the recipients.
   *
   * @param request    the request object containing parameters for report generation, such as
   *                   condominium ID, date range, user ID, report format, and optional
   *                   configurations like tax breakdown or resident details.
   * @param recipients a list of email addresses to which the generated report will be sent.
   * @return a ReportDeliveryConfirmation object containing details about the delivery status,
   * including the report identifier, a list of successful recipients, a list of failed recipients,
   * and the timestamp of when the email was sent.
   */
  ReportDeliveryConfirmation generateAndEmailReport(
      FinancialReportRequest request,
      List<String> recipients
  );

  /**
   * Retrieves a previously generated report from cache if available.
   *
   * @param condoId    the condominium identifier
   * @param reportDate the report date
   * @return the cached report, or empty if not found
   */
  Optional<FinancialReport> getCachedReport(Long condoId, LocalDate reportDate);
}
