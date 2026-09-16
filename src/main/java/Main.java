import java.util.ArrayList;
import java.util.List;

import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.orders.Order;

/**
 * Main - demonstration program for the FEMZYK e-commerce system.
 *
 * CS 1103-01, Programming Assignment Unit 2. This class sits OUTSIDE the
 * com.ecommerce and com.ecommerce.orders packages and uses the import
 * statement to bring their classes in, exactly as the assignment requires:
 *
 *   1. Create instances of products, customers, and orders.
 *   2. Let customers browse products, add them to a shopping cart,
 *      and place orders.
 *   3. Display information about products, customers, and orders.
 *   4. Demonstrate input validation and error handling for order
 *      placement and management.
 */
public class Main {

    /** A line used to separate the sections of the console output. */
    private static final String DIVIDER =
            "==================================================";

    /**
     * Program entry point: runs the e-commerce demonstration in order.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.println("      FEMZYK E-COMMERCE SYSTEM (demonstration)     ");
        System.out.println(DIVIDER);

        // ----- 1. Create products and let customers browse the catalog -----
        List<Product> catalog = createCatalog();

        System.out.println("\n--- 1. Browse the product catalog ---");
        for (Product product : catalog) {
            System.out.println("  " + product);
        }

        // ----- 2. Customers fill their shopping carts -----
        Customer alice = new Customer("C001", "Alice Johnson");
        Customer ben = new Customer("C002", "Ben Adams");

        System.out.println("\n--- 2. Customers fill their shopping carts ---");
        alice.addToCart(catalog.get(0));   // Wireless Mouse
        alice.addToCart(catalog.get(1));   // Mechanical Keyboard
        alice.addToCart(catalog.get(3));   // Laptop Stand
        alice.removeFromCart(catalog.get(3)); // Alice changes her mind
        ben.addToCart(catalog.get(1));     // Mechanical Keyboard
        ben.addToCart(catalog.get(2));     // USB-C Hub

        printCart(alice);
        printCart(ben);

        // ----- 3. Place orders (with error handling) -----
        System.out.println("\n--- 3. Place orders ---");
        Order aliceOrder = null;
        Order benOrder = null;
        try {
            aliceOrder = alice.placeOrder();
            System.out.println("Order placed successfully for " + alice.getName() + ".");
            System.out.println(aliceOrder.generateOrderSummary());
        } catch (IllegalStateException error) {
            System.out.println("Order failed: " + error.getMessage());
        }

        try {
            benOrder = ben.placeOrder();
            System.out.println("Order placed successfully for " + ben.getName() + ".");
            System.out.println(benOrder.generateOrderSummary());
        } catch (IllegalStateException error) {
            System.out.println("Order failed: " + error.getMessage());
        }

        // ----- 4. Demonstrate input validation and error handling -----
        System.out.println("\n--- 4. Input validation and error handling ---");
        demonstrateValidation(catalog, ben);

        // ----- 5. Manage order status -----
        System.out.println("\n--- 5. Manage order status ---");
        try {
            aliceOrder.updateOrderStatus(Order.Status.PROCESSING);
            System.out.println(aliceOrder.getOrderID() + " is now " + aliceOrder.getStatus() + ".");
            aliceOrder.updateOrderStatus(Order.Status.SHIPPED);
            System.out.println(aliceOrder.getOrderID() + " is now " + aliceOrder.getStatus() + ".");
        } catch (IllegalStateException error) {
            System.out.println("Status update failed: " + error.getMessage());
        }

        System.out.println("\n--- Final order overview ---");
        System.out.println("  " + aliceOrder);
        System.out.println("  " + benOrder);

        System.out.println("\nThank you for using the FEMZYK E-Commerce System!");
    }

    /**
     * Creates the store catalog. The negative-price attempt at the end is
     * shown on purpose in section 4, so it is created inside a try-catch
     * there instead of here.
     */
    private static List<Product> createCatalog() {
        List<Product> catalog = new ArrayList<>();
        catalog.add(new Product("P101", "Wireless Mouse", 25.99));
        catalog.add(new Product("P102", "Mechanical Keyboard", 89.50));
        catalog.add(new Product("P103", "USB-C Hub", 42.75));
        catalog.add(new Product("P104", "Laptop Stand", 34.00));
        return catalog;
    }

    /**
     * Prints a customer's current cart and its total cost.
     */
    private static void printCart(Customer customer) {
        System.out.println("  " + customer.getName() + "'s cart ("
                + customer.getShoppingCart().size() + " item(s)):");
        for (Product product : customer.getShoppingCart()) {
            System.out.println("    - " + product);
        }
        System.out.printf("    Cart total: $%.2f%n", customer.calculateCartTotal());
    }

    /**
     * Deliberately performs four invalid operations and shows that the
     * program rejects each one with a clear, handled error message:
     * a negative product price, an order with an empty cart, removing a
     * product that is not in a cart, and updating a cancelled order.
     */
    private static void demonstrateValidation(List<Product> catalog, Customer ben) {
        // Attempt 1: a product with a negative price must be rejected.
        try {
            new Product("P999", "Broken Item", -5.00);
        } catch (IllegalArgumentException error) {
            System.out.println("Attempt 1 rejected: " + error.getMessage());
        }

        // Attempt 2: placing an order with an empty cart must fail.
        try {
            Customer carol = new Customer("C003", "Carol Lee");
            carol.placeOrder();
        } catch (IllegalStateException error) {
            System.out.println("Attempt 2 rejected: " + error.getMessage());
        }

        // Attempt 3: removing a product that is not in the cart.
        boolean removed = ben.removeFromCart(catalog.get(3));
        if (!removed) {
            System.out.println("Attempt 3 rejected: product was not in Ben's cart.");
        }

        // Attempt 4: updating an order that has been cancelled.
        try {
            Customer carol = new Customer("C003", "Carol Lee");
            carol.addToCart(catalog.get(2));
            Order carolOrder = carol.placeOrder();
            carolOrder.updateOrderStatus(Order.Status.CANCELLED);
            System.out.println(carolOrder.getOrderID() + " is now " + carolOrder.getStatus() + ".");
            carolOrder.updateOrderStatus(Order.Status.PROCESSING);
        } catch (IllegalStateException error) {
            System.out.println("Attempt 4 rejected: " + error.getMessage());
        }
    }
}
