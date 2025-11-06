package org.java.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java.model.Transaction;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxCalculationService {

  private static final BigDecimal TAX_RATE = new BigDecimal("0.15"); // 15% tax rate

  public TaxCalculationResult calculate(TaxCalculationRequest request) {
    log.info("Calculating taxes for {} transactions", request.getTransactions().size());

    BigDecimal totalRevenue = request.getTransactions().stream()
        .filter(t -> t.getType() == Transaction.TransactionType.REVENUE)
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalTax = totalRevenue.multiply(TAX_RATE)
        .setScale(2, RoundingMode.HALF_UP);

    log.info("Calculated tax amount: {} on revenue: {}", totalTax, totalRevenue);

    return TaxCalculationResult.builder()
        .totalTax(totalTax)
        .taxableAmount(totalRevenue)
        .taxRate(TAX_RATE)
        .jurisdiction(request.getJurisdiction())
        .taxYear(request.getTaxYear())
        .build();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TaxCalculationRequest {
    private List<Transaction> transactions;
    private String jurisdiction;
    private int taxYear;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TaxCalculationResult {
    private BigDecimal totalTax;
    private BigDecimal taxableAmount;
    private BigDecimal taxRate;
    private String jurisdiction;
    private int taxYear;
  }
}
