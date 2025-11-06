package org.java.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java.dto.DeliveryResponseDTO;
import org.java.dto.EmailReportRequestDTO;
import org.java.dto.FinancialReport;
import org.java.dto.FinancialReportRequest;
import org.java.dto.ReportDeliveryConfirmation;
import org.java.dto.ReportRequestDTO;
import org.java.dto.ReportResponseDTO;
import org.java.enums.ReportFormat;
import org.java.service.ReportFacade;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class FinancialReportController {

  private final ReportFacade reportFacade;

  @PostMapping("/financial/{condoId}")
  public ResponseEntity<ReportResponseDTO> generateFinancialReport(
      @PathVariable Long condoId,
      @Valid @RequestBody ReportRequestDTO requestDTO) {

    log.info("Received request to generate report for condo: {}", condoId);

    FinancialReportRequest request = FinancialReportRequest.builder()
        .condoId(condoId)
        .startDate(requestDTO.getStartDate())
        .endDate(requestDTO.getEndDate())
        .userId(UUID.randomUUID().toString())
        .format(ReportFormat.PDF)
        .includeTaxBreakdown(requestDTO.isIncludeTaxes())
        .includeResidentDetails(requestDTO.isIncludeResidents())
        .build();

    FinancialReport report = reportFacade.generateReport(request);

    return ResponseEntity.ok(ReportResponseDTO.fromReport(report));
  }

  @PostMapping("/financial/{condoId}/email")
  public ResponseEntity<DeliveryResponseDTO> generateAndEmailReport(
      @PathVariable Long condoId,
      @Valid @RequestBody EmailReportRequestDTO requestDTO) {

    log.info("Received request to generate and email report for condo: {}", condoId);

    FinancialReportRequest request = FinancialReportRequest.builder()
        .condoId(condoId)
        .startDate(requestDTO.getStartDate())
        .endDate(requestDTO.getEndDate())
        .userId(UUID.randomUUID().toString())
        .format(ReportFormat.PDF)
        .includeTaxBreakdown(true)
        .includeResidentDetails(true)
        .build();

    ReportDeliveryConfirmation confirmation = reportFacade.generateAndEmailReport(
        request,
        requestDTO.getRecipients()
    );

    return ResponseEntity.ok(DeliveryResponseDTO.fromConfirmation(confirmation));
  }

  @GetMapping("/financial/{condoId}/cached")
  public ResponseEntity<ReportResponseDTO> getCachedReport(
      @PathVariable Long condoId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    log.info("Checking for cached report for condo: {} on date: {}", condoId, date);

    return reportFacade.getCachedReport(condoId, date)
        .map(report -> ResponseEntity.ok(ReportResponseDTO.fromReport(report)))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/financial/{condoId}/download")
  public ResponseEntity<byte[]> downloadFinancialReport(
      @PathVariable Long condoId,
      @Valid @RequestBody ReportRequestDTO requestDTO) {

    log.info("Received request to download report for condo: {}", condoId);

    FinancialReportRequest request = FinancialReportRequest.builder()
        .condoId(condoId)
        .startDate(requestDTO.getStartDate())
        .endDate(requestDTO.getEndDate())
        .userId(UUID.randomUUID().toString())
        .format(ReportFormat.PDF)
        .includeTaxBreakdown(requestDTO.isIncludeTaxes())
        .includeResidentDetails(requestDTO.isIncludeResidents())
        .build();

    FinancialReport report = reportFacade.generateReport(request);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment",
        "financial-report-" + report.getReportId() + ".pdf");
    headers.setContentLength(report.getContent().length);

    return ResponseEntity.ok()
        .headers(headers)
        .body(report.getContent());
  }

  @GetMapping("/financial/{condoId}/cached/download")
  public ResponseEntity<byte[]> downloadCachedReport(
      @PathVariable Long condoId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    log.info("Downloading cached report for condo: {} on date: {}", condoId, date);

    return reportFacade.getCachedReport(condoId, date)
        .map(report -> {
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_PDF);
          headers.setContentDispositionFormData("attachment",
              "financial-report-" + report.getReportId() + ".pdf");
          headers.setContentLength(report.getContent().length);

          return ResponseEntity.ok()
              .headers(headers)
              .body(report.getContent());
        })
        .orElse(ResponseEntity.notFound().build());
  }
}