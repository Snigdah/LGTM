package com.example.demo.service;

import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.model.CreateOrderRequest;
import com.example.demo.model.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Business logic for orders. In-memory only.
 *
 * <p>Observability features demonstrated here:
 * <ul>
 *   <li>{@code @Observed} -> creates a child span for each method (visible in Tempo)</li>
 *   <li>A custom Micrometer counter -> exported to Mimir as orders_created_total</li>
 *   <li>SLF4J logging -> written to stdout as JSON and shipped to Loki by Alloy</li>
 * </ul>
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);
    private final Counter ordersCreated;

    public OrderService(MeterRegistry meterRegistry) {
        this.ordersCreated = Counter.builder("orders.created")
                .description("Total number of orders created")
                .register(meterRegistry);
        seed();
    }

    private void seed() {
        createInternal("Alice", 120.50);
        createInternal("Bob", 75.00);
        log.info("Seeded {} demo orders on startup", store.size());
    }

    @Observed(name = "order.list", contextualName = "list-orders")
    public List<Order> findAll() {
        log.info("Listing all orders, count={}", store.size());
        return new ArrayList<>(store.values());
    }

    @Observed(name = "order.get", contextualName = "get-order")
    public Order findById(Long id) {
        log.debug("Looking up order id={}", id);
        Order order = store.get(id);
        if (order == null) {
            log.warn("Order id={} not found", id);
            throw new OrderNotFoundException(id);
        }
        return order;
    }

    @Observed(name = "order.create", contextualName = "create-order")
    public Order create(CreateOrderRequest request) {
        Order order = createInternal(request.getCustomer(), request.getAmount());
        ordersCreated.increment();
        log.info("Created order id={} customer={} amount={}",
                order.getId(), order.getCustomer(), order.getAmount());
        return order;
    }

    @Observed(name = "order.delete", contextualName = "delete-order")
    public void delete(Long id) {
        Order removed = store.remove(id);
        if (removed == null) {
            log.warn("Tried to delete missing order id={}", id);
            throw new OrderNotFoundException(id);
        }
        log.info("Deleted order id={}", id);
    }

    /**
     * Simulates a slow downstream call so you can see latency in Tempo traces.
     */
    @Observed(name = "order.slow", contextualName = "slow-operation")
    public String slowOperation() {
        long delayMs = ThreadLocalRandom.current().nextLong(500, 2000);
        log.info("Starting slow operation, simulated delay={}ms", delayMs);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Finished slow operation after {}ms", delayMs);
        return "completed in " + delayMs + "ms";
    }

    private Order createInternal(String customer, double amount) {
        long id = idSequence.incrementAndGet();
        Order order = new Order(id, customer, amount, "NEW", Instant.now());
        store.put(id, order);
        return order;
    }
}
