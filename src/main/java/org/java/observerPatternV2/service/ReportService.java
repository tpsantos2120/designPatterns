package org.java.observerPatternV2.service;

import java.util.List;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.observers.impl.CsvReportObserver;
import org.java.observerPatternV2.observers.impl.JsonReportObserver;
import org.java.observerPatternV2.observers.impl.PdfReportObserver;
import org.java.observerPatternV2.observers.impl.XmlReportObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

  private final Report report;
  private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
  private final Observer csvReportObserver = new CsvReportObserver();
  private final Observer jsonReportObserver = new JsonReportObserver();
  private final Observer pdfReportObserver = new PdfReportObserver();
  private final Observer xmlReportObserver = new XmlReportObserver();

  public ReportService(Report report) {
    this.report = report;
  }

  /**
   * Updates the sales report by creating a new Report instance, subscribing observers for handling
   * updates in different formats (CSV and JSON), and then notifying the observers with the given
   * sales data.
   *
   * @param sales the list of Sale objects containing data to be processed and used for generating
   *              reports
   */
  public void subscribeToAllReports(List<Sale> sales) {
    report.events.subscribe(EventType.UPDATE, csvReportObserver);
    report.events.subscribe(EventType.UPDATE, jsonReportObserver);
    report.events.subscribe(EventType.UPDATE, pdfReportObserver);
    report.events.subscribe(EventType.UPDATE, xmlReportObserver);
    report.updateReport(sales);
    logger.info("All reports were notified\n");
  }

  /**
   * Unsubscribes the CSV report observer from receiving update events and processes the given sales
   * data by updating the report.
   *
   * @param sales the list of Sale objects containing sales data to be processed
   */
  public void unsubscribeCsvReport(List<Sale> sales) {
    report.events.unsubscribe(EventType.UPDATE, csvReportObserver);
    report.updateReport(sales);
    logger.info("All reports were notified but CSV Report\n");
  }

  /**
   * Deletes reports by subscribing the CSV report observer to the DELETE event type and then
   * triggering the delete operation. The observer is responsible for managing the deletion of
   * corresponding report data or files.
   */
  public void deleteReports() {
    report.events.subscribe(EventType.DELETE, new CsvReportObserver());
    report.deleteReport();
    logger.info("CSV Report deleted\n");
  }
}
