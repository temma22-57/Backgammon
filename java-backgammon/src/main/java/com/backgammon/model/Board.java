package com.backgammon.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the backgammon board
 */
public class Board {
    // Constants for special points
    public static final int WHITE_BAR = -1;
    public static final int BLACK_BAR = 24;
    public static final int WHITE_HOME = 25;
    public static final int BLACK_HOME = -2;
    public static final int CHECKERS_PER_PLAYER = 15;
    
    // The points on the board
    private Map<Integer, Point> points;
    
    // The bar (holds checkers that have been hit)
    private Map<PlayerColor, Integer> bar;
    
    // The home (holds checkers that have been borne off)
    private Map<PlayerColor, Integer> home;
    
    /**
     * Create a new board with the initial setup
     */
    public Board() {
        points = new HashMap<>();
        bar = new HashMap<>();
        home = new HashMap<>();
        
        // Initialize the bar and home
        bar.put(PlayerColor.WHITE, 0);
        bar.put(PlayerColor.BLACK, 0);
        home.put(PlayerColor.WHITE, 0);
        home.put(PlayerColor.BLACK, 0);
        
        // Create all 24 points
        for (int i = 0; i < 24; i++) {
            points.put(i, new Point(i));
        }
        
        // Set up the initial positions
        setupInitialPosition();
    }
    
    /**
     * Initialize the board with the standard backgammon starting position
     */
    private void setupInitialPosition() {
        // White checkers
        addCheckers(0, PlayerColor.WHITE, 2);   // Point 1: 2 white checkers
        addCheckers(5, PlayerColor.WHITE, 5);   // Point 6: 5 white checkers
        addCheckers(7, PlayerColor.WHITE, 3);   // Point 8: 3 white checkers
        addCheckers(11, PlayerColor.WHITE, 5);  // Point 12: 5 white checkers
        
        // Black checkers 
        addCheckers(12, PlayerColor.BLACK, 5);  // Point 13: 5 black checkers
        addCheckers(16, PlayerColor.BLACK, 3);  // Point 17: 3 black checkers
        addCheckers(18, PlayerColor.BLACK, 5);  // Point 19: 5 black checkers
        addCheckers(23, PlayerColor.BLACK, 2);  // Point 24: 2 black checkers
    }
    
    /**
     * Add multiple checkers to a point
     * @param pointIndex the index of the point
     * @param color the color of the checkers
     * @param count the number of checkers to add
     */
    private void addCheckers(int pointIndex, PlayerColor color, int count) {
        Point point = points.get(pointIndex);
        for (int i = 0; i < count; i++) {
            point.addChecker(color);
        }
    }
    
    /**
     * Get a point by its index
     * @param pointIndex the index of the point
     * @return the point, or null if the index is invalid
     */
    public Point getPoint(int pointIndex) {
        return points.get(pointIndex);
    }
    
    /**
     * Get the number of checkers on the bar for a player
     * @param color the player color
     * @return the number of checkers on the bar
     */
    public int getBarCount(PlayerColor color) {
        return bar.get(color);
    }
    
    /**
     * Add a checker to the bar
     * @param color the color of the checker
     */
    public void addToBar(PlayerColor color) {
        bar.put(color, bar.get(color) + 1);
    }
    
    /**
     * Remove a checker from the bar
     * @param color the color of the checker
     * @throws IllegalStateException if there are no checkers of the given color on the bar
     */
    public void removeFromBar(PlayerColor color) {
        if (bar.get(color) <= 0) {
            throw new IllegalStateException("No " + color + " checkers on the bar");
        }
        bar.put(color, bar.get(color) - 1);
    }
    
    /**
     * Get the number of checkers in the home for a player
     * @param color the player color
     * @return the number of checkers in the home
     */
    public int getHomeCount(PlayerColor color) {
        return home.get(color);
    }
    
    /**
     * Add a checker to the home (bearing off)
     * @param color the color of the checker
     */
    public void addToHome(PlayerColor color) {
        home.put(color, home.get(color) + 1);
    }
    
    /**
     * Check if all checkers for a player are in their home board
     * @param color the player color
     * @return true if all checkers are in the home board or home
     */
    public boolean allCheckersInHomeboard(PlayerColor color) {
        // If there are checkers on the bar, they're not in the home board
        if (bar.get(color) > 0) {
            return false;
        }
        
        // For white, home board is points 0-5
        if (color == PlayerColor.WHITE) {
            for (int i = 6; i < 24; i++) {
                Point point = points.get(i);
                if (point != null && point.hasColor(PlayerColor.WHITE)) {
                    return false;
                }
            }
        }
        
        // For black, home board is points 18-23
        if (color == PlayerColor.BLACK) {
            for (int i = 0; i < 18; i++) {
                Point point = points.get(i);
                if (point != null && point.hasColor(PlayerColor.BLACK)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Get the highest point number in the home board that has a checker of the given color
     * @param color the player color
     * @return the highest point number, or -1 if no checkers are found
     */
    public int getHighestCheckerInHomeboard(PlayerColor color) {
        if (color == PlayerColor.WHITE) {
            for (int i = 5; i >= 0; i--) {
                if (points.get(i).hasColor(PlayerColor.WHITE)) {
                    return i;
                }
            }
        } else {
            for (int i = 23; i >= 18; i--) {
                if (points.get(i).hasColor(PlayerColor.BLACK)) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Check if a player has won the game (all checkers borne off)
     * @param color the player color
     * @return true if the player has won, false otherwise
     */
    public boolean hasWon(PlayerColor color) {
        return home.get(color) == CHECKERS_PER_PLAYER;
    }
    
    /**
     * Clone this board
     * @return a new board with the same state
     */
    public Board clone() {
        Board newBoard = new Board();
        
        // Clear the new board's initial setup
        for (int i = 0; i < 24; i++) {
            newBoard.points.get(i).clear();
        }
        
        // Copy the state from this board
        for (int i = 0; i < 24; i++) {
            Point point = points.get(i);
            if (point != null && !point.isEmpty()) {
                PlayerColor color = point.getColor();
                int count = point.getCount();
                for (int j = 0; j < count; j++) {
                    newBoard.points.get(i).addChecker(color);
                }
            }
        }
        
        // Copy the bar and home
        newBoard.bar.put(PlayerColor.WHITE, bar.get(PlayerColor.WHITE));
        newBoard.bar.put(PlayerColor.BLACK, bar.get(PlayerColor.BLACK));
        newBoard.home.put(PlayerColor.WHITE, home.get(PlayerColor.WHITE));
        newBoard.home.put(PlayerColor.BLACK, home.get(PlayerColor.BLACK));
        
        return newBoard;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board:\n");
        
        // Print the top row (points 13-24)
        sb.append("13 14 15 16 17 18   19 20 21 22 23 24\n");
        for (int i = 12; i < 24; i++) {
            Point point = points.get(i);
            if (point.isEmpty()) {
                sb.append(" . ");
            } else {
                String symbol = point.getColor() == PlayerColor.WHITE ? "W" : "B";
                sb.append(symbol).append(point.getCount()).append(" ");
            }
            
            // Add the center bar separator
            if (i == 17) {
                sb.append("  ");
            }
        }
        sb.append("\n");
        
        // Print the bar
        sb.append("Bar: ");
        sb.append("W").append(bar.get(PlayerColor.WHITE)).append(" ");
        sb.append("B").append(bar.get(PlayerColor.BLACK)).append("\n");
        
        // Print the home
        sb.append("Home: ");
        sb.append("W").append(home.get(PlayerColor.WHITE)).append(" ");
        sb.append("B").append(home.get(PlayerColor.BLACK)).append("\n");
        
        // Print the bottom row (points 12-1)
        for (int i = 11; i >= 0; i--) {
            Point point = points.get(i);
            if (point.isEmpty()) {
                sb.append(" . ");
            } else {
                String symbol = point.getColor() == PlayerColor.WHITE ? "W" : "B";
                sb.append(symbol).append(point.getCount()).append(" ");
            }
            
            // Add the center bar separator
            if (i == 6) {
                sb.append("  ");
            }
        }
        sb.append("\n");
        sb.append("12 11 10  9  8  7    6  5  4  3  2  1\n");
        
        return sb.toString();
    }
}