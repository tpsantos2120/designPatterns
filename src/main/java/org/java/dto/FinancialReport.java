package org.java.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.java.enums.ReportFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReport {
  private String reportId;
  private Long condoId;
  private LocalDate generatedAt;
  private byte[] content;
  private ReportFormat format;
  private ReportMetadata metadata;
}
