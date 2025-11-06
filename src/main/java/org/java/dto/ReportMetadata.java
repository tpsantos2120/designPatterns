package org.java.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportMetadata {
  private BigDecimal totalRevenue;
  private BigDecimal totalExpenses;
  private BigDecimal netBalance;
  private BigDecimal taxAmount;
  private int transactionCount;
  private LocalDate periodStart;
  private LocalDate periodEnd;
}
