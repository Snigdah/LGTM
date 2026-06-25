package com.example.payment.exception;

public class PaymentDeclinedException extends RuntimeException {

    public PaymentDeclinedException(String customer, double amount) {
        super("Payment declined for " + customer + ": amount " + amount + " exceeds limit");
    }
}
