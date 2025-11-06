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
public class FinancialReportRequest {
  private Long condoId;
  private LocalDate startDate;
  private LocalDate endDate;
  private String userId;
  private ReportFormat format;
  private boolean includeTaxBreakdown;
  private boolean includeResidentDetails;
}
