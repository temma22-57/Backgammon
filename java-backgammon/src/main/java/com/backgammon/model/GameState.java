package com.backgammon.model;

/**
 * Enum representing the current state of the game
 */
public enum GameState {
    INITIAL,    // Game not started yet
    PLAYING,    // Game in progress
    ENDED       // Game has ended with a winner
}