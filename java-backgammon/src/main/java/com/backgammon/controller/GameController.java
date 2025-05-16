package com.backgammon.controller;

import com.backgammon.model.*;
import com.backgammon.view.GameView;

import java.util.List;

/**
 * Controller for the backgammon game, manages the game flow and user interactions
 */
public class GameController {
    // The game logic
    private GameLogic gameLogic;
    
    // The game view
    private GameView gameView;
    
    // AI player (if enabled)
    private AI ai;
    
    // Whether AI mode is enabled
    private boolean aiMode;
    
    // Selected point for move
    private Integer selectedPoint;
    
    /**
     * Create a new game controller
     * @param gameView the view component
     */
    public GameController(GameView gameView) {
        this.gameLogic = new GameLogic();
        this.gameView = gameView;
        this.aiMode = false;
        this.selectedPoint = null;
    }
    
    /**
     * Start a new game
     * @param aiMode whether to play against AI
     */
    public void startNewGame(boolean aiMode) {
        gameLogic = new GameLogic();
        gameLogic.startGame();
        this.aiMode = aiMode;
        
        if (aiMode) {
            // AI always plays as black
            this.ai = new AI(PlayerColor.BLACK);
        } else {
            this.ai = null;
        }
        
        selectedPoint = null;
        updateView();
    }
    
    /**
     * Roll the dice for the current player
     */
    public void rollDice() {
        if (gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        List<Integer> diceValues = gameLogic.rollDice();
        gameView.updateDice(diceValues, gameLogic.getDice().getUsed());
        
        // If AI's turn, make AI move after a short delay
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            makeAIMove();
        }
        
        updateView();
    }
    
    /**
     * Handle point click
     * @param pointIndex the index of the clicked point
     */
    public void handlePointClick(int pointIndex) {
        if (gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        // If it's AI's turn and AI mode is enabled, ignore clicks
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            return;
        }
        
        PlayerColor currentPlayer = gameLogic.getCurrentPlayer();
        Board board = gameLogic.getBoard();
        Dice dice = gameLogic.getDice();
        
        // If a point is already selected, try to move from selected point to clicked point
        if (selectedPoint != null) {
            Move move = new Move(selectedPoint, pointIndex);
            boolean moveMade = gameLogic.makeMove(move);
            
            if (moveMade) {
                // Successful move
                gameView.playMoveSound();
                
                // If it's now AI's turn, make AI move
                if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
                    makeAIMove();
                }
            }
            
            selectedPoint = null;
            updateView();
            return;
        }
        
        // Check if the clicked point has the current player's checkers
        Point point = board.getPoint(pointIndex);
        if (point != null && point.hasColor(currentPlayer)) {
            // Select this point
            selectedPoint = pointIndex;
            
            // Highlight possible moves from this point
            List<Move> possibleMoves = gameLogic.getPossibleMoves();
            List<Integer> validDestinations = possibleMoves.stream()
                .filter(move -> move.getFrom() == pointIndex)
                .map(Move::getTo)
                .toList();
            
            gameView.highlightPoints(pointIndex, validDestinations);
        }
    }
    
    /**
     * Handle bar click
     * @param color the color of the bar section clicked
     */
    public void handleBarClick(PlayerColor color) {
        if (gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        // If it's AI's turn and AI mode is enabled, ignore clicks
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            return;
        }
        
        // Only allow selecting the bar if it has the current player's checkers
        PlayerColor currentPlayer = gameLogic.getCurrentPlayer();
        if (color != currentPlayer) {
            return;
        }
        
        Board board = gameLogic.getBoard();
        if (board.getBarCount(color) > 0) {
            // Select the bar
            selectedPoint = (color == PlayerColor.WHITE) ? Board.WHITE_BAR : Board.BLACK_BAR;
            
            // Highlight possible moves from the bar
            List<Move> possibleMoves = gameLogic.getPossibleMoves();
            List<Integer> validDestinations = possibleMoves.stream()
                .filter(move -> move.getFrom() == selectedPoint)
                .map(Move::getTo)
                .toList();
            
            gameView.highlightPoints(selectedPoint, validDestinations);
        }
    }
    
    /**
     * End the current player's turn
     */
    public void endTurn() {
        if (gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        // If it's AI's turn and AI mode is enabled, ignore
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            return;
        }
        
        gameLogic.endTurn();
        selectedPoint = null;
        
        // If it's now AI's turn, make AI move
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            makeAIMove();
        }
        
        updateView();
    }
    
    /**
     * Make an AI move
     */
    private void makeAIMove() {
        if (ai == null || gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        // Roll dice for AI if not already rolled
        if (!gameLogic.getDice().hasAvailableMoves()) {
            gameLogic.rollDice();
            gameView.updateDice(gameLogic.getDice().getValues(), gameLogic.getDice().getUsed());
        }
        
        // Make a move until no more moves are possible
        boolean madeMove;
        do {
            Move bestMove = ai.getBestMove(gameLogic.getBoard(), gameLogic.getDice());
            if (bestMove == null) {
                break;
            }
            
            madeMove = gameLogic.makeMove(bestMove);
            if (madeMove) {
                gameView.playMoveSound();
            }
            
            // Brief pause between moves for clarity
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            updateView();
        } while (madeMove && gameLogic.getDice().hasAvailableMoves());
        
        // End AI turn
        if (gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            gameLogic.endTurn();
        }
        
        updateView();
    }
    
    /**
     * Undo the last move
     */
    public void undoLastMove() {
        if (gameLogic.getGameState() != GameState.PLAYING) {
            return;
        }
        
        // If it's AI's turn and AI mode is enabled, ignore
        if (aiMode && gameLogic.getCurrentPlayer() == PlayerColor.BLACK) {
            return;
        }
        
        gameLogic.undoLastMove();
        selectedPoint = null;
        updateView();
    }
    
    /**
     * Update the view with current game state
     */
    private void updateView() {
        gameView.updateBoard(gameLogic.getBoard());
        gameView.updateGameState(
            gameLogic.getGameState(),
            gameLogic.getCurrentPlayer(),
            gameLogic.getWinner()
        );
        gameView.updateDice(
            gameLogic.getDice().getValues(),
            gameLogic.getDice().getUsed()
        );
        
        // Clear highlights if no point is selected
        if (selectedPoint == null) {
            gameView.clearHighlights();
        }
    }
    
    /**
     * Get the current game state
     * @return the game state
     */
    public GameState getGameState() {
        return gameLogic.getGameState();
    }
    
    /**
     * Get the current player
     * @return the player whose turn it is
     */
    public PlayerColor getCurrentPlayer() {
        return gameLogic.getCurrentPlayer();
    }
    
    /**
     * Get the winner (if any)
     * @return the winner, or null if the game is not over
     */
    public PlayerColor getWinner() {
        return gameLogic.getWinner();
    }
}