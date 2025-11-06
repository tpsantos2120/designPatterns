package org.java.service;

import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditLogger {

  public void log(AuditEntry entry) {
    log.info("AUDIT: {} - User: {} - Resource: {}/{} - Condo: {} - Time: {}",
        entry.getAction(),
        entry.getUserId(),
        entry.getResourceType(),
        entry.getResourceId(),
        entry.getCondoId(),
        entry.getTimestamp());

    if (entry.getMetadata() != null && !entry.getMetadata().isEmpty()) {
      log.debug("AUDIT Metadata: {}", entry.getMetadata());
    }

    // In a real application, this would persist to a database or send to a logging service
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AuditEntry {
    private String action;
    private String userId;
    private String resourceType;
    private String resourceId;
    private Long condoId;
    private Instant timestamp;
    private Map<String, String> metadata;
  }
}
