package com.ecommerce.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.orders.Order;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * ECommerceApp - JavaFX front end of the FEMZYK E-Commerce System.
 *
 * CS 1103-01, Programming Assignment Unit 2. The window mirrors the store
 * scenario in three steps:
 *
 *   Step 1 - Browse products (the catalog with "Add to cart" buttons).
 *   Step 2 - Shopping cart (pick a customer, remove items, place an order).
 *   Step 3 - Orders (list of orders, status management, order summary).
 *
 * The window REUSES the exact same domain classes as the console
 * demonstration (com.ecommerce.Product, com.ecommerce.Customer and
 * com.ecommerce.orders.Order), which are imported here, and every risky
 * action is validated with clear error messages.
 */
public class ECommerceApp extends Application {

    private static final String APP_TITLE = "FEMZYK E-Commerce System - CS 1103-01 Unit 2";

    /** The store catalog and registered customers, seeded at startup. */
    private final List<Product> catalog = new ArrayList<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final List<Order> orders = new ArrayList<>();

    private ComboBox<String> customerBox;
    private VBox cartBox;
    private Label cartTotalLabel;
    private Label cartMessage;
    private ListView<Order> orderList;
    private ComboBox<Order.Status> statusBox;
    private Label orderMessage;
    private TextArea orderSummary;

    @Override
    public void start(Stage stage) {
        stage.setTitle(APP_TITLE);
        seedData();

        Label title = new Label("FEMZYK E-Commerce System");
        title.getStyleClass().add("header-title");
        Label subtitle = new Label("CS 1103-01 - Programming Assignment Unit 2 - JavaFX + Maven");
        subtitle.getStyleClass().add("header-sub");

        // ----- Step 1 card: product catalog -----
        VBox catalogRows = new VBox(8);
        for (Product product : catalog) {
            catalogRows.getChildren().add(productRow(product));
        }
        VBox catalogCard = new VBox(8, sectionLabel("Step 1 - Browse products"), catalogRows);
        catalogCard.getStyleClass().add("card");

        // ----- Step 2 card: shopping cart -----
        customerBox = new ComboBox<>();
        customerBox.setId("customerBox");
        customerBox.getItems().addAll(customers.keySet());
        customerBox.getSelectionModel().selectFirst();
        customerBox.setOnAction(event -> refreshCart());

        cartBox = new VBox(6);
        cartBox.setId("cartBox");
        cartTotalLabel = new Label();
        cartTotalLabel.setId("cartTotalLabel");
        cartTotalLabel.getStyleClass().add("cart-total");
        cartMessage = messageLabel("cartMessage");

        Button placeOrderButton = new Button("Place order");
        placeOrderButton.setId("placeOrderButton");
        placeOrderButton.setOnAction(event -> placeOrder());

        HBox customerRow = new HBox(10, new Label("Customer:"), customerBox);
        customerRow.setAlignment(Pos.CENTER_LEFT);
        VBox cartCard = new VBox(8, sectionLabel("Step 2 - Shopping cart"),
                customerRow, cartBox, cartTotalLabel, placeOrderButton, cartMessage);
        cartCard.getStyleClass().add("card");

        // ----- Step 3 card: orders -----
        orderList = new ListView<>();
        orderList.setId("orderList");
        orderList.setPrefHeight(110);
        orderList.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldOrder, newOrder) -> showSummary(newOrder));

        statusBox = new ComboBox<>();
        statusBox.setId("statusBox");
        statusBox.getItems().addAll(Order.Status.values());
        statusBox.setValue(Order.Status.PROCESSING);
        Button updateStatusButton = new Button("Update status");
        updateStatusButton.setId("updateStatusButton");
        updateStatusButton.setOnAction(event -> updateStatus());
        HBox statusRow = new HBox(10, new Label("New status:"), statusBox, updateStatusButton);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        orderMessage = messageLabel("orderMessage");
        orderSummary = new TextArea();
        orderSummary.setId("orderSummary");
        orderSummary.setEditable(false);
        orderSummary.setPrefRowCount(8);

        VBox orderCard = new VBox(8, sectionLabel("Step 3 - Orders"),
                orderList, statusRow, orderMessage, orderSummary);
        orderCard.getStyleClass().add("card");

        // ----- Assemble the window -----
        VBox root = new VBox(14, title, subtitle, catalogCard, cartCard, orderCard);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        refreshCart();
        Scene scene = new Scene(root, 780, 1010);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /** Creates one catalog row: product description plus "Add to cart". */
    private HBox productRow(Product product) {
        Label description = new Label(product.toString());
        description.setMaxWidth(Double.MAX_VALUE);
        Button addButton = new Button("Add to cart");
        addButton.setId("add-" + product.getProductID());
        addButton.setOnAction(event -> addToCart(product));
        HBox row = new HBox(10, description, addButton);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(description, Priority.ALWAYS);
        return row;
    }

    /** Adds a product to the cart of the selected customer. */
    private void addToCart(Product product) {
        hide(cartMessage);
        try {
            activeCustomer().addToCart(product);
        } catch (IllegalArgumentException error) {
            show(cartMessage, error.getMessage(), true);
        }
        refreshCart();
    }

    /** Rebuilds the cart rows and the total for the selected customer. */
    private void refreshCart() {
        Customer customer = activeCustomer();
        cartBox.getChildren().clear();
        if (customer.getShoppingCart().isEmpty()) {
            Label empty = new Label("The cart is empty - add products from the catalog above.");
            empty.getStyleClass().add("muted");
            cartBox.getChildren().add(empty);
        } else {
            for (Product product : customer.getShoppingCart()) {
                cartBox.getChildren().add(cartRow(product));
            }
        }
        cartTotalLabel.setText(String.format("Cart total: $%.2f (%d item(s))",
                customer.calculateCartTotal(), customer.getShoppingCart().size()));
    }

    /** Creates one cart row: product description plus "Remove". */
    private HBox cartRow(Product product) {
        Label description = new Label(product.toString());
        description.setMaxWidth(Double.MAX_VALUE);
        Button removeButton = new Button("Remove");
        removeButton.setId("remove-" + product.getProductID());
        removeButton.setOnAction(event -> {
            hide(cartMessage);
            activeCustomer().removeFromCart(product);
            refreshCart();
        });
        HBox row = new HBox(10, description, removeButton);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(description, Priority.ALWAYS);
        return row;
    }

    /**
     * Places an order with the cart of the selected customer.
     * An empty cart is rejected by the Customer class and reported
     * with a clear red message (input validation).
     */
    private void placeOrder() {
        hide(cartMessage);
        try {
            Order order = activeCustomer().placeOrder();
            orders.add(order);
            orderList.getItems().setAll(orders);
            orderList.getSelectionModel().select(order);
            show(cartMessage, "Order " + order.getOrderID()
                    + " placed successfully. The cart is now empty.", false);
        } catch (IllegalStateException error) {
            show(cartMessage, error.getMessage(), true);
        }
        refreshCart();
    }

    /**
     * Updates the status of the selected order. Without a selected order
     * the action is rejected (input validation), and a cancelled order
     * refuses further updates.
     */
    private void updateStatus() {
        hide(orderMessage);
        Order selected = orderList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            show(orderMessage, "Select an order in the list first.", true);
            return;
        }
        try {
            selected.updateOrderStatus(statusBox.getValue());
            orderList.refresh();
            showSummary(selected);
            show(orderMessage, selected.getOrderID() + " is now "
                    + selected.getStatus() + ".", false);
        } catch (IllegalStateException error) {
            show(orderMessage, error.getMessage(), true);
        }
    }

    /** Shows the formatted summary of the selected order. */
    private void showSummary(Order order) {
        orderSummary.setText(order == null
                ? "Select an order to see its full summary."
                : order.generateOrderSummary());
    }

    /** Returns the customer currently selected in the combo box. */
    private Customer activeCustomer() {
        return customers.get(customerBox.getValue());
    }

    /** Seeds the catalog and the three demo customers. */
    private void seedData() {
        catalog.add(new Product("P101", "Wireless Mouse", 25.99));
        catalog.add(new Product("P102", "Mechanical Keyboard", 89.50));
        catalog.add(new Product("P103", "USB-C Hub", 42.75));
        catalog.add(new Product("P104", "Laptop Stand", 34.00));
        customers.put("Alice Johnson (C001)", new Customer("C001", "Alice Johnson"));
        customers.put("Ben Adams (C002)", new Customer("C002", "Ben Adams"));
        customers.put("Carol Lee (C003)", new Customer("C003", "Carol Lee"));
    }

    /** Creates a section heading label. */
    private Label sectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    /** Creates an initially hidden label used for error or result messages. */
    private Label messageLabel(String id) {
        Label label = new Label();
        label.setId(id);
        label.setWrapText(true);
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    /** Shows a label with the given message in error (red) or success (green) style. */
    private void show(Label label, String message, boolean isError) {
        label.getStyleClass().removeAll("error", "result");
        label.getStyleClass().add(isError ? "error" : "result");
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    /** Hides a previously shown label. */
    private void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
}
