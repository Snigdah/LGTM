package com.example.demo.model;

import java.time.Instant;

/**
 * Simple order record stored in memory. Good enough for a telemetry demo.
 */
public class Order {

    private Long id;
    private String customer;
    private double amount;
    private String status;
    private Instant createdAt;

    public Order() {
    }

    public Order(Long id, String customer, double amount, String status, Instant createdAt) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
