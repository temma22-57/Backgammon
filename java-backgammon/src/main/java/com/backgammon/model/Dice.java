package com.backgammon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a pair of dice
 */
public class Dice {
    // Random number generator
    private final Random random;
    
    // Dice values and whether they have been used
    private List<Integer> values;
    private List<Boolean> used;
    
    /**
     * Create a new pair of dice
     */
    public Dice() {
        this.random = new Random();
        this.values = new ArrayList<>();
        this.used = new ArrayList<>();
    }
    
    /**
     * Roll the dice
     */
    public void roll() {
        values.clear();
        used.clear();
        
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        
        // If doubles, player gets 4 moves with the same value
        if (die1 == die2) {
            for (int i = 0; i < 4; i++) {
                values.add(die1);
                used.add(false);
            }
        } else {
            values.add(die1);
            values.add(die2);
            used.add(false);
            used.add(false);
        }
    }
    
    /**
     * Get the dice values
     * @return the dice values
     */
    public List<Integer> getValues() {
        return new ArrayList<>(values);
    }
    
    /**
     * Get whether each die has been used
     * @return list of booleans indicating whether each die has been used
     */
    public List<Boolean> getUsed() {
        return new ArrayList<>(used);
    }
    
    /**
     * Mark a die as used
     * @param value the value of the die to mark as used
     * @return true if a die was marked as used, false if the value was not found or already used
     */
    public boolean useDie(int value) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == value && !used.get(i)) {
                used.set(i, true);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if a die value is available (not used)
     * @param value the value to check
     * @return true if the value is available, false otherwise
     */
    public boolean isValueAvailable(int value) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == value && !used.get(i)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if any die values are available (not used)
     * @return true if any values are available, false otherwise
     */
    public boolean hasAvailableMoves() {
        for (boolean isUsed : used) {
            if (!isUsed) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the number of available moves
     * @return the number of dice that have not been used
     */
    public int getAvailableMoveCount() {
        int count = 0;
        for (boolean isUsed : used) {
            if (!isUsed) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get a list of available die values
     * @return list of die values that have not been used
     */
    public List<Integer> getAvailableValues() {
        List<Integer> availableValues = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (!used.get(i)) {
                availableValues.add(values.get(i));
            }
        }
        return availableValues;
    }
    
    /**
     * Reset the dice (all dice unused)
     */
    public void reset() {
        values.clear();
        used.clear();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dice: ");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (used.get(i)) {
                sb.append("(used)");
            }
            if (i < values.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}