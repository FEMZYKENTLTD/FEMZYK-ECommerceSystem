package com.ecommerce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecommerce.orders.Order;

/**
 * Customer - represents a customer with a shopping cart.
 *
 * Part of the com.ecommerce package (CS 1103-01, Programming Assignment Unit 2).
 * The cart is kept as a private List and is only exposed as an unmodifiable
 * view, so outside code can read it but cannot change it directly.
 * The class imports Order from the com.ecommerce.orders package, which
 * demonstrates the use of the import statement between packages.
 */
public class Customer {

    /** Unique identifier of the customer (immutable). */
    private final String customerID;

    /** Display name of the customer. */
    private String name;

    /** The customer's shopping cart (private for encapsulation). */
    private final List<Product> shoppingCart;

    /**
     * Creates a validated customer with an empty shopping cart.
     *
     * @param customerID unique identifier, not empty
     * @param name       display name, not empty
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Customer(String customerID, String name) {
        if (customerID == null || customerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.customerID = customerID.trim();
        this.name = name.trim();
        this.shoppingCart = new ArrayList<>();
    }

    /** Returns the customer ID. */
    public String getCustomerID() {
        return customerID;
    }

    /** Returns the customer name. */
    public String getName() {
        return name;
    }

    /**
     * Updates the customer name.
     *
     * @param name new name, not empty
     * @throws IllegalArgumentException if the name is empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns a read-only view of the shopping cart, so that other
     * classes cannot modify it directly (data hiding).
     */
    public List<Product> getShoppingCart() {
        return Collections.unmodifiableList(shoppingCart);
    }

    /**
     * Adds a product to the shopping cart.
     *
     * @param product the product to add, not null
     * @throws IllegalArgumentException if the product is null
     */
    public void addToCart(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add a null product to the cart.");
        }
        shoppingCart.add(product);
        System.out.println(name + " added to cart: " + product);
    }

    /**
     * Removes a product from the shopping cart.
     *
     * @param product the product to remove
     * @return true if it was found and removed, false if it was not in the cart
     */
    public boolean removeFromCart(Product product) {
        boolean removed = shoppingCart.remove(product);
        if (removed) {
            System.out.println(name + " removed from cart: " + product);
        }
        return removed;
    }

    /** Returns the total cost of all products currently in the cart. */
    public double calculateCartTotal() {
        double total = 0.0;
        for (Product product : shoppingCart) {
            total += product.getPrice();
        }
        return total;
    }

    /**
     * Places an order with the current contents of the shopping cart.
     * The Order class (com.ecommerce.orders package) copies the cart
     * contents, and the cart is emptied after the purchase.
     *
     * @return the newly created Order
     * @throws IllegalStateException if the shopping cart is empty
     */
    public Order placeOrder() {
        if (shoppingCart.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot place an order: the shopping cart of " + name + " is empty.");
        }
        Order order = new Order(this, shoppingCart);
        shoppingCart.clear();
        return order;
    }
}
