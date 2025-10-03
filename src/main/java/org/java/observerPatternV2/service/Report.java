package org.java.observerPatternV2.service;

import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.observers.impl.CsvReportObserver;
import org.java.observerPatternV2.observers.impl.JsonReportObserver;
import org.java.observerPatternV2.observers.impl.PdfReportObserver;
import org.java.observerPatternV2.observers.impl.XmlReportObserver;
import org.java.observerPatternV2.subject.EventManager;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Report class is responsible for handling updates related to sales data and notifying
 * subscribed observers of these updates using the Observer design pattern. It creates an
 * {@link EventManager} to manage event notifications specifically for the {@link EventType#UPDATE}
 * event type.
 */
@Component
public class Report {

  public EventManager events;

  /**
   * Constructs a new Report instance and initializes the EventManager to handle
   * events of the type {@link EventType#UPDATE}.
   * The EventManager is used to manage observers and notify them
   * of updates related to sales data.
   */
  public Report() {
    this.events = new EventManager(EventType.UPDATE, EventType.DELETE);
  }

  /**
   * Notifies all subscribed observers of an update event with the provided sales data.
   *
   * @param sales the list of Sale objects containing the sales data to be passed to observers
   */
  public void updateReport(List<Sale> sales) {
    events.notify(EventType.UPDATE, sales);
  }

  /**
   * Notifies all subscribed observers of a delete event. This triggers the delete operation
   * on all observers subscribed to the {@link EventType#DELETE} event type through the
   * {@link EventManager}.
   */
  public void deleteReport() {
    events.notify(EventType.DELETE);
  }
}
