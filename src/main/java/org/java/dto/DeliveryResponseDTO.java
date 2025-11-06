package org.java.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {
  private String reportId;
  private List<String> successfulRecipients;
  private List<String> failedRecipients;
  private Instant sentAt;
  private String message;

  public static DeliveryResponseDTO fromConfirmation(ReportDeliveryConfirmation confirmation) {
    String message = String.format(
        "Report sent to %d recipients. %d failed.",
        confirmation.getSuccessfulRecipients().size(),
        confirmation.getFailedRecipients().size()
    );

    return DeliveryResponseDTO.builder()
        .reportId(confirmation.getReportId())
        .successfulRecipients(confirmation.getSuccessfulRecipients())
        .failedRecipients(confirmation.getFailedRecipients())
        .sentAt(confirmation.getSentAt())
        .message(message)
        .build();
  }
}