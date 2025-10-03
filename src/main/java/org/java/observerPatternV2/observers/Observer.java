package org.java.observerPatternV2.observers;

import java.util.List;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.model.Sale;

/**
 * Represents an observer in the Observer design pattern. An observer is notified of changes
 * or events in the subject it observes and reacts accordingly. Implementations of this
 * interface define the specific behavior to execute when the observer is updated.
 */
public interface Observer {
  /**
   * Updates the observer with the specified event type and the list of sales data.
   * This method is called to notify the observer when a specific event occurs.
   *
   * @param eventType the type of event that triggered the notification
   * @param sales     the list of Sale objects containing the relevant sales data
   */
  void update(EventType eventType, List<Sale> sales);
  void delete(EventType eventType);
}
