package org.java;

import java.time.LocalDate;
import java.util.UUID;

public record Sale(UUID id, String productName, double amount, LocalDate date, String customerName) {

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", customerName='" + customerName + '\'' +
                '}';
    }

  public static Builder builder() {
    return new Builder();
  }

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

    public Sale build() {
      return new Sale(id, productName, amount, date, customerName);
    }
  }
}
