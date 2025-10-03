package org.java.observerPatternV2.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a record of a single sale transaction.
 */
public record Sale(UUID id,
                   String productName,
                   double amount,
                   LocalDate date,
                   String customerName) {

}
