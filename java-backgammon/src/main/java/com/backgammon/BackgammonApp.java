package com.backgammon;

import com.backgammon.view.SwingGameView;

import javax.swing.*;

/**
 * Main application class for Backgammon
 */
public class BackgammonApp {
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SwingGameView gameView = new SwingGameView();
            gameView.initializeController();
        });
    }
}