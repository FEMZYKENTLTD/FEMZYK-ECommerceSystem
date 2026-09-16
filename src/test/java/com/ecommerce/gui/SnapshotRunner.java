package com.ecommerce.gui;

import java.io.File;

import javax.imageio.ImageIO;

import com.ecommerce.orders.Order;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Development utility (not part of the submitted program): renders the real
 * ECommerceApp user interface, drives it through two representative
 * sessions, and saves a screenshot of each so the README and the report
 * can include genuine images of the program's output.
 */
public class SnapshotRunner extends Application {

    /** Directory where the screenshots are written (override with -Dsnapshot.dir=...). */
    private static final String OUT_DIR = System.getProperty("snapshot.dir", ".");

    @Override
    public void start(Stage stage) {
        try {
            captureFullSession(stage, OUT_DIR + "/screenshot-gui.png");
            Stage secondStage = new Stage();
            captureValidationSession(secondStage, OUT_DIR + "/screenshot-gui-validation.png");
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            Platform.exit();
        }
    }

    /** Session 1: two customers shop, two orders are placed and managed. */
    private void captureFullSession(Stage stage, String path) throws Exception {
        new ECommerceApp().start(stage);
        Scene scene = stage.getScene();

        ComboBox<String> customerBox = (ComboBox<String>) scene.lookup("#customerBox");
        ListView<Order> orderList = (ListView<Order>) scene.lookup("#orderList");
        ComboBox<Order.Status> statusBox = (ComboBox<Order.Status>) scene.lookup("#statusBox");

        // Alice fills her cart and places an order.
        fire(scene, "#add-P101");
        fire(scene, "#add-P102");

        // Ben fills his cart and places an order.
        customerBox.getSelectionModel().select(1);
        fire(scene, "#add-P102");
        fire(scene, "#add-P103");
        fire(scene, "#placeOrderButton");

        // Alice places her order as well.
        customerBox.getSelectionModel().select(0);
        fire(scene, "#placeOrderButton");

        // Manage the first order: PLACED -> PROCESSING -> SHIPPED.
        orderList.getSelectionModel().select(0);
        statusBox.setValue(Order.Status.PROCESSING);
        fire(scene, "#updateStatusButton");
        statusBox.setValue(Order.Status.SHIPPED);
        fire(scene, "#updateStatusButton");

        renderAndSave(scene, path);
    }

    /** Session 2: an empty-cart order and a missing selection are rejected. */
    private void captureValidationSession(Stage stage, String path) throws Exception {
        new ECommerceApp().start(stage);
        Scene scene = stage.getScene();

        ComboBox<String> customerBox = (ComboBox<String>) scene.lookup("#customerBox");

        // Attempt 1: place an order with an empty cart (Alice).
        fire(scene, "#placeOrderButton");

        // Attempt 2: place an order with an empty cart (Carol).
        customerBox.getSelectionModel().select(2);
        fire(scene, "#placeOrderButton");

        // Attempt 3: update a status while no order is selected.
        fire(scene, "#updateStatusButton");

        renderAndSave(scene, path);
    }

    /** Fires the button with the given id. */
    private void fire(Scene scene, String buttonId) {
        Button button = (Button) scene.lookup(buttonId);
        if (button != null) {
            button.fire();
        }
    }

    /** Forces a layout pass, snapshots the scene and writes it as a PNG. */
    private void renderAndSave(Scene scene, String path) throws Exception {
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        WritableImage image = scene.snapshot(null);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(path));
        System.out.println("Saved screenshot: " + path);
    }

    /**
     * Launches the utility.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
