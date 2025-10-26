package org.java.singleton;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SINGLETON PATTERN Implementation using Enum (Best Practice).
 * <p>
 * Collects and tracks metrics for report exports across the entire application.
 * <ul>
 * This demonstrates a true Singleton pattern with:
 * <li>Thread-safe implementation using enum</li>
 * <li>Single instance guarantee by the JVM</li>
 * <li>Lazy initialization</li>
 * <li>No synchronization overhead</li>
 * </ul>
 * </p>
 * <p>
 * <ul>
 * Why Enum Singleton?
 * <li>Provides serialization for free</li>
 * <li>Guaranteed thread-safety by JVM</li>
 * <li>Prevents reflection attacks</li>
 * <li>Simplest and most robust implementation</li>
 * </ul>
 * </p>
 */
public enum ReportMetricsCollector {

  /**
   * The single instance of the ReportMetricsCollector. Accessed via
   * ReportMetricsCollector.INSTANCE
   */
  INSTANCE;

  private static final Logger log = LoggerFactory.getLogger(ReportMetricsCollector.class);
  private final AtomicLong totalExports = new AtomicLong(0);
  private final AtomicInteger successfulExports = new AtomicInteger(0);
  private final AtomicInteger failedExports = new AtomicInteger(0);
  private final Map<String, AtomicInteger> exportsByFormat = new ConcurrentHashMap<>();
  private volatile LocalDateTime firstExportTime;
  private volatile LocalDateTime lastExportTime;

  /**
   * Private constructor - called once by JVM when enum is first accessed.
   */
  ReportMetricsCollector() {
    initializeMetrics();
  }

  /**
   * Initialize metrics counters for all supported formats.
   */
  private void initializeMetrics() {
    exportsByFormat.put("CSV", new AtomicInteger(0));
    exportsByFormat.put("JSON", new AtomicInteger(0));
    exportsByFormat.put("XML", new AtomicInteger(0));
    exportsByFormat.put("PDF", new AtomicInteger(0));
  }

  /**
   * Record a successful export.
   *
   * @param format      The format used for the export
   * @param recordCount Number of records exported
   */
  public void recordExport(String format, int recordCount) {
    totalExports.incrementAndGet();
    successfulExports.incrementAndGet();

    exportsByFormat.computeIfAbsent(format, k -> new AtomicInteger(0)).incrementAndGet();

    LocalDateTime now = LocalDateTime.now();
    if (firstExportTime == null) {
      firstExportTime = now;
    }
    lastExportTime = now;

    log.debug("Recorded export: format={}, records={}, total={}",
        format, recordCount, totalExports.get());
  }

  /**
   * Record a failed export.
   *
   * @param format       The format that was attempted
   * @param errorMessage The error message
   */
  public void recordFailure(String format, String errorMessage) {
    totalExports.incrementAndGet();
    failedExports.incrementAndGet();
    lastExportTime = LocalDateTime.now();

    log.warn("Recorded failed export: format={}, error={}", format, errorMessage);
  }

  /**
   * Get success rate as a percentage.
   *
   * @return Success rate (0-100)
   */
  public double getSuccessRate() {
    long total = totalExports.get();
    if (total == 0) {
      return 0.0;
    }
    return (successfulExports.get() * 100.0) / total;
  }

  /**
   * Get all format statistics.
   *
   * @return Map of format to count
   */
  public Map<String, Integer> getAllFormatStatistics() {
    Map<String, Integer> stats = new ConcurrentHashMap<>();
    exportsByFormat.forEach((format, count) -> stats.put(format, count.get()));
    return stats;
  }

  /**
   * Get comprehensive metrics summary.
   *
   * @return Metrics summary object
   */
  public MetricsSummary getSummary() {
    return new MetricsSummary(
        totalExports.get(),
        successfulExports.get(),
        failedExports.get(),
        getSuccessRate(),
        getAllFormatStatistics(),
        firstExportTime,
        lastExportTime
    );
  }

  /**
   * Metrics summary record.
   */
  public record MetricsSummary(
      long totalExports,
      int successfulExports,
      int failedExports,
      double successRate,
      Map<String, Integer> formatStatistics,
      LocalDateTime firstExportTime,
      LocalDateTime lastExportTime
  ) {
  }
}
