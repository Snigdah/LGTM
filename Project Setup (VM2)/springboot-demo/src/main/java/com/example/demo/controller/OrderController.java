package com.example.demo.controller;

import com.example.demo.model.CreateOrderRequest;
import com.example.demo.model.Order;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** GET /api/orders -> list all orders */
    @GetMapping
    public List<Order> list() {
        log.info("GET /api/orders");
        return orderService.findAll();
    }

    /** GET /api/orders/{id} -> single order (404 if missing) */
    @GetMapping("/{id}")
    public Order getOne(@PathVariable Long id) {
        log.info("GET /api/orders/{}", id);
        return orderService.findById(id);
    }

    /** POST /api/orders -> create an order */
    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders customer={}", request.getCustomer());
        Order created = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** DELETE /api/orders/{id} -> remove an order */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/orders/{}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/orders/slow -> deliberately slow (good for trace latency testing) */
    @GetMapping("/slow")
    public String slow() {
        log.info("GET /api/orders/slow");
        return orderService.slowOperation();
    }

    /** GET /api/orders/error -> always throws (good for error logs + traces) */
    @GetMapping("/error")
    public String error() {
        log.error("GET /api/orders/error - about to throw a demo exception");
        throw new IllegalStateException("Deliberate demo failure for observability testing");
    }
}
