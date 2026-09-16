package com.ecommerce;

/**
 * Product - represents a product available for purchase in the store.
 *
 * Part of the com.ecommerce package (CS 1103-01, Programming Assignment Unit 2).
 * All fields are private to demonstrate encapsulation; the product ID is
 * immutable once created, and both constructors and setters validate their
 * inputs so that an invalid product can never exist.
 */
public class Product {

    /** Unique identifier of the product (immutable). */
    private final String productID;

    /** Display name of the product. */
    private String name;

    /** Price of the product in dollars (never negative). */
    private double price;

    /**
     * Creates a validated product.
     *
     * @param productID unique identifier, not empty
     * @param name      display name, not empty
     * @param price     price in dollars, zero or positive
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Product(String productID, String name, double price) {
        if (productID == null || productID.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.productID = productID.trim();
        this.name = name.trim();
        this.price = price;
    }

    /** Returns the product ID. */
    public String getProductID() {
        return productID;
    }

    /** Returns the product name. */
    public String getName() {
        return name;
    }

    /** Returns the product price in dollars. */
    public double getPrice() {
        return price;
    }

    /**
     * Updates the product name.
     *
     * @param name new name, not empty
     * @throws IllegalArgumentException if the name is empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Updates the product price.
     *
     * @param price new price in dollars, zero or positive
     * @throws IllegalArgumentException if the price is negative
     */
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    /**
     * Returns a one-line description of the product, used in the catalog,
     * shopping carts, and order summaries.
     */
    @Override
    public String toString() {
        return String.format("%s - %s ($%.2f)", productID, name, price);
    }
}
