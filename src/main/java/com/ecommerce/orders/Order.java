package com.ecommerce.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecommerce.Customer;
import com.ecommerce.Product;

/**
 * Order - represents an order placed by a customer.
 *
 * Part of the com.ecommerce.orders package (CS 1103-01, Programming
 * Assignment Unit 2). The class imports Customer and Product from the
 * com.ecommerce package, demonstrating cross-package imports. Orders are
 * immutable once created (defensive copy of the product list), and their
 * status is managed through the nested Status enum with validation.
 */
public class Order {

    /** Possible statuses of an order during its life cycle. */
    public enum Status {
        PLACED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

    /** Shared counter used to generate unique order IDs (ORD-1001, ...). */
    private static int orderCounter = 1000;

    /** Unique identifier of the order (immutable). */
    private final String orderID;

    /** The customer who placed the order (immutable). */
    private final Customer customer;

    /** The products included in the order (immutable list). */
    private final List<Product> products;

    /** The computed total cost of the order (immutable). */
    private final double orderTotal;

    /** The current status of the order. */
    private Status status;

    /**
     * Creates a validated order from a customer and a list of products.
     *
     * @param customer the customer placing the order, not null
     * @param products the products being ordered, not empty
     * @throws IllegalArgumentException if the customer is null or the
     *         product list is null or empty
     */
    public Order(Customer customer, List<Product> products) {
        if (customer == null) {
            throw new IllegalArgumentException("An order must have a customer.");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one product.");
        }
        this.orderID = "ORD-" + (++orderCounter);
        this.customer = customer;
        this.products = new ArrayList<>(products); // defensive copy
        double computedTotal = 0.0;
        for (Product product : products) {
            computedTotal += product.getPrice();
        }
        this.orderTotal = computedTotal;
        this.status = Status.PLACED;
    }

    /** Returns the order ID, e.g. "ORD-1001". */
    public String getOrderID() {
        return orderID;
    }

    /** Returns the customer who placed the order. */
    public Customer getCustomer() {
        return customer;
    }

    /** Returns a read-only view of the products in the order. */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /** Returns the total cost of the order in dollars. */
    public double getOrderTotal() {
        return orderTotal;
    }

    /** Returns the current status of the order. */
    public Status getStatus() {
        return status;
    }

    /**
     * Updates the status of the order.
     *
     * @param newStatus the new status, not null
     * @throws IllegalArgumentException if the status is null
     * @throws IllegalStateException if the order is cancelled (a cancelled
     *         order can no longer be modified)
     */
    public void updateOrderStatus(Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null.");
        }
        if (status == Status.CANCELLED) {
            throw new IllegalStateException(
                    "Order " + orderID + " is cancelled and cannot be updated.");
        }
        this.status = newStatus;
    }

    /**
     * Generates a formatted, multi-line summary of the order, including
     * the customer, status, every product line, and the order total.
     */
    public String generateOrderSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("---------- Order Summary ----------\n");
        summary.append("Order ID : ").append(orderID).append("\n");
        summary.append("Customer : ").append(customer.getName())
               .append(" (").append(customer.getCustomerID()).append(")\n");
        summary.append("Status   : ").append(status).append("\n");
        summary.append("Items:\n");
        for (Product product : products) {
            summary.append("  - ").append(product).append("\n");
        }
        summary.append(String.format("Order total: $%.2f%n", orderTotal));
        summary.append("-----------------------------------");
        return summary.toString();
    }

    /** Returns a short one-line description of the order. */
    @Override
    public String toString() {
        return String.format("%s (%d item(s), $%.2f, %s)",
                orderID, products.size(), orderTotal, status);
    }
}
