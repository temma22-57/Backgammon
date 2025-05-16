package com.backgammon.model;

/**
 * Represents a move from one point to another
 */
public class Move {
    // The index of the source point
    private final int from;
    
    // The index of the destination point
    private final int to;
    
    /**
     * Create a new move
     * @param from the source point index
     * @param to the destination point index
     */
    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }
    
    /**
     * Get the source point index
     * @return the source index
     */
    public int getFrom() {
        return from;
    }
    
    /**
     * Get the destination point index
     * @return the destination index
     */
    public int getTo() {
        return to;
    }
    
    @Override
    public String toString() {
        return "Move from " + from + " to " + to;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Move other = (Move) obj;
        return from == other.from && to == other.to;
    }
    
    @Override
    public int hashCode() {
        return 31 * from + to;
    }
}