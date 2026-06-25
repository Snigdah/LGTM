package com.example.demo.service;

import com.example.demo.exception.PaymentDeclinedException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Step 3 of the checkout chain. Charges the customer.
 * This is the method that FAILS when the amount is over the limit, so the
 * "charge-payment" span shows the error in the trace.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private static final double LIMIT = 5000.0;

    @Observed(name = "payment.charge", contextualName = "charge-payment")
    public String charge(String customer, double amount) {
        log.info("Step 3/3 - charging customer={} amount={}", customer, amount);
        sleep(40, 120);

        if (amount > LIMIT) {
            log.error("Step 3/3 - payment DECLINED for customer={} amount={} (limit={})",
                    customer, amount, LIMIT);
            throw new PaymentDeclinedException(customer, amount);
        }

        String txn = "TXN-" + System.currentTimeMillis();
        log.info("Step 3/3 - payment APPROVED for customer={} txn={}", customer, txn);
        return txn;
    }

    private void sleep(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
