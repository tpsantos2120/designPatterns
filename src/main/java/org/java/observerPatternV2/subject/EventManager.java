package org.java.observerPatternV2.subject;

import java.util.HashMap;
import java.util.Map;
import org.java.observerPatternV2.enums.EventType;
import org.java.observerPatternV2.model.Sale;
import org.java.observerPatternV2.observers.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject in the Observer Pattern. Maintains a list of observers and notifies them when sales data
 * is exported.
 */
@Component
public class EventManager {

  private final Map<EventType, List<Observer>> events = new HashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(EventManager.class);

  public EventManager() {
    this(EventType.values());
  }

  /**
   * Initializes an EventManager instance with the specified event types. Creates and maps each
   * provided {@code EventType} to an empty list of observers to manage subscriptions for those
   * event types.
   *
   * @param operations the event types to be managed by this EventManager
   */
  public EventManager(EventType... operations) {
    for (EventType operation : operations) {
      this.events.put(operation, new ArrayList<>());
    }
  }

  /**
   * Subscribes an observer to a specific event type, allowing the observer to be notified of
   * changes or actions associated with the given event.
   *
   * @param eventType the type of event the observer wants to subscribe to
   * @param observer  the observer to be subscribed to the specified event type
   */
  public void subscribe(EventType eventType, Observer observer) {
    List<Observer> observers = events.get(eventType);
    logger.info("Event {} - Subscribing to {}", eventType, observer.getClass().getSimpleName());
    observers.add(observer);
  }

  /**
   * Unsubscribes a given observer from a specific event type, preventing the observer from
   * receiving notifications related to the specified event.
   *
   * @param eventType the type of event the observer wants to unsubscribe from
   * @param observer  the observer to be unsubscribed from the specified event type
   */
  public void unsubscribe(EventType eventType, Observer observer) {
    List<Observer> observers = events.get(eventType);
    logger.info("Event {} - Unsubscribing to {}", eventType, observer.getClass().getSimpleName());
    observers.remove(observer);
  }

  /**
   * Notifies all registered observers of a specific event type, providing them with the associated
   * sales data for further processing.
   *
   * @param eventType the type of event to notify observers about
   * @param sales     the list of Sale objects containing data to be passed to observers
   */
  public void notify(EventType eventType, List<Sale> sales) {
    List<Observer> observers = events.get(eventType);
    for (Observer observer : observers) {
      observer.update(eventType, sales);
      logger.info("Event: {} - Notifying update to {}", eventType,
          observer.getClass().getSimpleName());
    }
  }

  /**
   * Notifies all observers subscribed to a specific event type, triggering the `delete` method on
   * each observer associated with the specified event type.
   *
   * @param eventType the type of event to notify observers about
   */
  public void notify(EventType eventType) {
    List<Observer> observers = events.get(eventType);
    for (Observer observer : observers) {
      observer.delete(eventType);
      logger.info("Event: {} - Notifying delete to {}", eventType,
          observer.getClass().getSimpleName());
    }
  }
}
