package org.java.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class ReportIdGenerator {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private final AtomicLong counter = new AtomicLong(0);

  public String generate() {
    String timestamp = LocalDateTime.now().format(FORMATTER);
    long count = counter.incrementAndGet();
    return String.format("RPT-%s-%04d", timestamp, count % 10000);
  }
}
