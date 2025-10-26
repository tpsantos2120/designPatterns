package org.java.model;

import java.time.LocalDate;
import java.util.UUID;

public record Sale(UUID id,
                   String productName,
                   double amount,
                   LocalDate date,
                   String customerName) {

  /**
   * Creates a new Builder instance for constructing Sale objects. This is the entry point for the
   * Builder pattern.
   *
   * @return A new Builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder class for Sale record. BUILDER PATTERN: Allows step-by-step construction of Sale
   * objects with a fluent interface.
   */
  public static final class Builder {
    private UUID id;
    private String productName;
    private double amount;
    private LocalDate date;
    private String customerName;

    private Builder() {
    }

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder productName(String productName) {
      this.productName = productName;
      return this;
    }

    public Builder amount(double amount) {
      this.amount = amount;
      return this;
    }

    public Builder date(LocalDate date) {
      this.date = date;
      return this;
    }

    public Builder customerName(String customerName) {
      this.customerName = customerName;
      return this;
    }

    /**
     * Builds the Sale instance with the provided values.
     *
     * @return A new Sale instance
     */
    public Sale build() {
      return new Sale(id, productName, amount, date, customerName);
    }
  }
}
