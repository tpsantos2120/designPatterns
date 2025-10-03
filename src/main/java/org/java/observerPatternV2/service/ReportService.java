package org.java.observerPatternV2.service;

import java.util.List;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.observers.Observer;
import org.java.observerPatternV2.observers.impl.CsvReportObserver;
import org.java.observerPatternV2.observers.impl.JsonReportObserver;
import org.java.observerPatternV2.observers.impl.PdfReportObserver;
import org.java.observerPatternV2.observers.impl.XmlReportObserver;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

  private final Report report;

  private final Observer csvReportObserver = new CsvReportObserver();
  private final Observer jsonReportObserver = new JsonReportObserver();
  private final Observer pdfReportObserver = new PdfReportObserver();
  private final Observer xmlReportObserver = new XmlReportObserver();

  public ReportService(Report report) {
    this.report = report;
  }

  /**
   * Updates the sales report by creating a new Report instance, subscribing observers for
   * handling updates in different formats (CSV and JSON), and then notifying the observers
   * with the given sales data.
   *
   * @param sales the list of Sale objects containing data to be processed and used for
   *              generating reports
   */
  public void updateReportsV1(List<Sale> sales) {
    report.events.subscribe(EventType.UPDATE, csvReportObserver);
    report.events.subscribe(EventType.UPDATE, jsonReportObserver);
    report.events.subscribe(EventType.UPDATE, pdfReportObserver);
    report.events.subscribe(EventType.UPDATE, xmlReportObserver);
    report.updateReport(sales);
  }
  public void updateReportsV2(List<Sale> sales) {
    report.events.unsubscribe(EventType.UPDATE, csvReportObserver);
    report.updateReport(sales);
  }

  public void deleteReports() {
    report.events.subscribe(EventType.DELETE, new CsvReportObserver());
    report.deleteReport();
  }
}
