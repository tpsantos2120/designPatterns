package org.java.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceProvider {

  public void send(EmailMessage message) {
    log.info("Sending email to: {}", message.getRecipient());
    log.info("Subject: {}", message.getSubject());

    // In a real application, this would use JavaMailSender or an email service API
    // For demo; we just log the action

    try {
      // Simulate email sending
      Thread.sleep(100);
      log.info("Email sent successfully to: {}", message.getRecipient());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new EmailException("Failed to send email", e);
    }
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EmailMessage {
    private String recipient;
    private String subject;
    private String body;
    private EmailAttachment attachment;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EmailAttachment {
    private String filename;
    private byte[] content;
    private String mimeType;
  }

  public static class EmailException extends RuntimeException {
    public EmailException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
