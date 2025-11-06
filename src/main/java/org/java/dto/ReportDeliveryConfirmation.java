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
public class ReportDeliveryConfirmation {
  private String reportId;
  private List<String> successfulRecipients;
  private List<String> failedRecipients;
  private Instant sentAt;
}