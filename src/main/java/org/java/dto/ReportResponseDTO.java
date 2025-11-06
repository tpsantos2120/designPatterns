package org.java.dto;

import java.time.LocalDate;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.java.enums.ReportFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {
  private String reportId;
  private Long condoId;
  private LocalDate generatedAt;
  private String contentBase64;
  private ReportFormat format;
  private ReportMetadata metadata;

  public static ReportResponseDTO fromReport(FinancialReport report) {
    return ReportResponseDTO.builder()
        .reportId(report.getReportId())
        .condoId(report.getCondoId())
        .generatedAt(report.getGeneratedAt())
        .contentBase64(Base64.getEncoder().encodeToString(report.getContent()))
        .format(report.getFormat())
        .metadata(report.getMetadata())
        .build();
  }
}
