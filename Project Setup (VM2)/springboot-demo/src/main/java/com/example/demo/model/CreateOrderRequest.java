package com.example.demo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request body for POST /api/orders.
 */
public class CreateOrderRequest {

    @NotBlank(message = "customer must not be blank")
    private String customer;

    @Positive(message = "amount must be greater than zero")
    private double amount;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
