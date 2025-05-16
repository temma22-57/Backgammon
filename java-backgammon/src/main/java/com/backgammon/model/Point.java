package com.backgammon.model;

/**
 * Represents a point on the backgammon board
 */
public class Point {
    // The index of the point on the board (1-24)
    private final int index;
    
    // The color of the checkers on this point, if any
    private PlayerColor color;
    
    // The number of checkers on this point
    private int count;
    
    /**
     * Create a new point with the given index
     * @param index the index of the point on the board (1-24)
     */
    public Point(int index) {
        this.index = index;
        this.color = null;
        this.count = 0;
    }
    
    /**
     * Get the index of this point
     * @return the index
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * Get the color of the checkers on this point
     * @return the color, or null if the point is empty
     */
    public PlayerColor getColor() {
        return color;
    }
    
    /**
     * Get the number of checkers on this point
     * @return the count
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Add a checker to this point
     * @param color the color of the checker to add
     * @throws IllegalArgumentException if attempting to add a checker of a different color
     *                                  to a point with multiple checkers
     */
    public void addChecker(PlayerColor color) {
        // If the point is empty, set the color
        if (count == 0) {
            this.color = color;
        } else if (this.color != color) {
            // Can't add a checker of a different color unless the point is empty
            // or has only one checker (hitting)
            if (count > 1) {
                throw new IllegalArgumentException(
                    "Cannot add " + color + " checker to point " + index +
                    " with " + count + " " + this.color + " checkers");
            }
            
            // Hitting a single checker - the opponent's checker is removed and sent to the bar
            this.color = color;
            this.count = 0; // Reset count since the opponent's checker is removed
        }
        
        this.count++;
    }
    
    /**
     * Remove a checker from this point
     * @return the color of the removed checker
     * @throws IllegalStateException if the point is empty
     */
    public PlayerColor removeChecker() {
        if (count == 0 || color == null) {
            throw new IllegalStateException("Cannot remove checker from empty point " + index);
        }
        
        count--;
        
        PlayerColor removedColor = color;
        
        // If the point is now empty, reset the color
        if (count == 0) {
            color = null;
        }
        
        return removedColor;
    }
    
    /**
     * Clear all checkers from this point
     */
    public void clear() {
        count = 0;
        color = null;
    }
    
    /**
     * Check if this point is a blot (has only one checker)
     * @return true if the point is a blot, false otherwise
     */
    public boolean isBlot() {
        return count == 1;
    }
    
    /**
     * Check if this point is made (has two or more checkers)
     * @return true if the point is made, false otherwise
     */
    public boolean isMade() {
        return count >= 2;
    }
    
    /**
     * Check if this point is empty
     * @return true if the point is empty, false otherwise
     */
    public boolean isEmpty() {
        return count == 0;
    }
    
    /**
     * Check if this point has checkers of the given color
     * @param color the color to check
     * @return true if the point has checkers of the given color, false otherwise
     */
    public boolean hasColor(PlayerColor color) {
        return this.color == color && count > 0;
    }
    
    /**
     * Clone this point
     * @return a new point with the same state as this one
     */
    public Point clone() {
        Point newPoint = new Point(index);
        newPoint.color = this.color;
        newPoint.count = this.count;
        return newPoint;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Point " + index + ": empty";
        } else {
            return "Point " + index + ": " + count + " " + color + " checkers";
        }
    }
}