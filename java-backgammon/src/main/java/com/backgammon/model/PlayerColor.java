package com.backgammon.model;

/**
 * Enum representing the player colors in backgammon
 */
public enum PlayerColor {
    WHITE,
    BLACK;
    
    /**
     * Get the opposite color
     * @return the opposite player color
     */
    public PlayerColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
    
    @Override
    public String toString() {
        return this == WHITE ? "White" : "Black";
    }
}