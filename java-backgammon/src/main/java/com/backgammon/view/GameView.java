package com.backgammon.view;

import com.backgammon.model.Board;
import com.backgammon.model.GameState;
import com.backgammon.model.PlayerColor;

import java.util.List;

/**
 * Interface for the game view, which displays the game to the user
 */
public interface GameView {
    /**
     * Update the board display
     * @param board the current board state
     */
    void updateBoard(Board board);
    
    /**
     * Update the game state display
     * @param gameState the current game state
     * @param currentPlayer the current player
     * @param winner the winner (if any)
     */
    void updateGameState(GameState gameState, PlayerColor currentPlayer, PlayerColor winner);
    
    /**
     * Update the dice display
     * @param diceValues the current dice values
     * @param used whether each die has been used
     */
    void updateDice(List<Integer> diceValues, List<Boolean> used);
    
    /**
     * Highlight a point and its valid move destinations
     * @param pointIndex the index of the selected point
     * @param validDestinations the indices of valid destinations
     */
    void highlightPoints(int pointIndex, List<Integer> validDestinations);
    
    /**
     * Clear all highlights
     */
    void clearHighlights();
    
    /**
     * Play a sound for moving pieces
     */
    void playMoveSound();
    
    /**
     * Display an error message
     * @param message the message to display
     */
    void showError(String message);
}