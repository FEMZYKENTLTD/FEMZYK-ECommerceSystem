package com.ecommerce.gui;

import javafx.application.Application;

/**
 * Launcher - entry point of the FEMZYK E-Commerce System GUI.
 *
 * CS 1103-01, Programming Assignment Unit 2. JavaFX 11+ no longer launches
 * an Application subclass directly from the classpath, so this small
 * launcher class is required.
 *
 * Run with Maven:  mvn javafx:run
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Application.launch(ECommerceApp.class, args);
    }
}
