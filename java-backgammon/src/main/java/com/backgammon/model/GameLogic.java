package com.backgammon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the core game logic for backgammon
 */
public class GameLogic {
    // The board
    private Board board;
    
    // The dice
    private Dice dice;
    
    // The move validator
    private MoveValidator moveValidator;
    
    // The current game state
    private GameState gameState;
    
    // The current player's turn
    private PlayerColor currentPlayer;
    
    // The winner (if any)
    private PlayerColor winner;
    
    // List of moves played in the current turn
    private List<Move> movesPlayed;
    
    /**
     * Create a new game
     */
    public GameLogic() {
        this.board = new Board();
        this.dice = new Dice();
        this.moveValidator = new MoveValidator();
        this.gameState = GameState.INITIAL;
        this.currentPlayer = PlayerColor.WHITE; // White goes first
        this.winner = null;
        this.movesPlayed = new ArrayList<>();
    }
    
    /**
     * Start a new game
     */
    public void startGame() {
        this.board = new Board(); // Reset the board
        this.dice.reset();  // Clear the dice
        this.gameState = GameState.PLAYING;
        this.currentPlayer = PlayerColor.WHITE;
        this.winner = null;
        this.movesPlayed.clear();
    }
    
    /**
     * Roll the dice for the current player
     * @return the dice values
     */
    public List<Integer> rollDice() {
        if (gameState != GameState.PLAYING) {
            throw new IllegalStateException("Cannot roll dice when game is not in progress");
        }
        
        dice.roll();
        movesPlayed.clear();
        
        // Check if the player has any legal moves with these dice
        List<Move> possibleMoves = moveValidator.getPossibleMoves(currentPlayer, board, dice);
        if (possibleMoves.isEmpty()) {
            // No legal moves, end the turn
            endTurn();
        }
        
        return dice.getValues();
    }
    
    /**
     * Make a move
     * @param move the move to make
     * @return true if the move was made successfully, false otherwise
     */
    public boolean makeMove(Move move) {
        if (gameState != GameState.PLAYING) {
            return false;
        }
        
        // Check if the move is valid
        if (!moveValidator.isValidMove(move.getFrom(), move.getTo(), currentPlayer, board, dice)) {
            return false;
        }
        
        // Make the move
        int from = move.getFrom();
        int to = move.getTo();
        
        // Calculate the die value used
        int dieUsed = calculateDieValue(from, to);
        
        // Mark the die as used
        if (!dice.useDie(dieUsed)) {
            return false; // Should not happen if move is valid
        }
        
        // Move the checker
        if (from == Board.WHITE_BAR) {
            // White coming in from the bar
            board.removeFromBar(PlayerColor.WHITE);
            makeCheckersMove(null, board.getPoint(to), PlayerColor.WHITE);
        } else if (from == Board.BLACK_BAR) {
            // Black coming in from the bar
            board.removeFromBar(PlayerColor.BLACK);
            makeCheckersMove(null, board.getPoint(to), PlayerColor.BLACK);
        } else if (to == Board.WHITE_HOME) {
            // White bearing off
            makeCheckersMove(board.getPoint(from), null, PlayerColor.WHITE);
            board.addToHome(PlayerColor.WHITE);
        } else if (to == Board.BLACK_HOME) {
            // Black bearing off
            makeCheckersMove(board.getPoint(from), null, PlayerColor.BLACK);
            board.addToHome(PlayerColor.BLACK);
        } else {
            // Regular move
            makeCheckersMove(board.getPoint(from), board.getPoint(to), currentPlayer);
        }
        
        // Add the move to the list of moves played
        movesPlayed.add(move);
        
        // Check if the game is over
        checkGameOver();
        
        // Check if the player has any more moves available
        if (!dice.hasAvailableMoves() || moveValidator.getPossibleMoves(currentPlayer, board, dice).isEmpty()) {
            endTurn();
        }
        
        return true;
    }
    
    /**
     * Calculate the die value used for a move
     * @param from the source point index
     * @param to the destination point index
     * @return the die value used
     */
    private int calculateDieValue(int from, int to) {
        if (currentPlayer == PlayerColor.WHITE) {
            if (from == Board.WHITE_BAR) {
                return 24 - to;
            } else if (to == Board.WHITE_HOME) {
                return from + 1;
            } else {
                return from - to;
            }
        } else {
            if (from == Board.BLACK_BAR) {
                return to + 1;
            } else if (to == Board.BLACK_HOME) {
                return 24 - from;
            } else {
                return to - from;
            }
        }
    }
    
    /**
     * Move a checker from one point to another
     * @param from the source point (null if from bar)
     * @param to the destination point (null if bearing off)
     * @param playerColor the player's color
     */
    private void makeCheckersMove(Point from, Point to, PlayerColor playerColor) {
        // Remove from source point
        if (from != null) {
            from.removeChecker();
        }
        
        // Add to destination point (if not bearing off)
        if (to != null) {
            // Check if hitting an opponent's blot
            if (to.hasColor(playerColor.opposite()) && to.getCount() == 1) {
                // Remove opponent's checker and add to bar
                to.removeChecker();
                board.addToBar(playerColor.opposite());
            }
            
            // Add the player's checker
            to.addChecker(playerColor);
        }
    }
    
    /**
     * End the current player's turn
     */
    public void endTurn() {
        // Clear dice and moves
        dice.reset();
        movesPlayed.clear();
        
        // Switch to the other player
        currentPlayer = currentPlayer.opposite();
    }
    
    /**
     * Undo the last move
     * @return true if a move was undone, false if there are no moves to undo
     */
    public boolean undoLastMove() {
        if (movesPlayed.isEmpty()) {
            return false;
        }
        
        // Get the last move
        Move lastMove = movesPlayed.remove(movesPlayed.size() - 1);
        
        // Restore the die value
        int dieValue = calculateDieValue(lastMove.getFrom(), lastMove.getTo());
        for (int i = 0; i < dice.getValues().size(); i++) {
            if (dice.getValues().get(i) == dieValue && dice.getUsed().get(i)) {
                // Un-use this die
                dice.getUsed().set(i, false);
                break;
            }
        }
        
        // Undo the move on the board
        int from = lastMove.getFrom();
        int to = lastMove.getTo();
        
        // This is a simplified undo that doesn't handle all scenarios perfectly,
        // especially with hits. A real implementation would need to track the full
        // move history to handle complex cases.
        
        if (from == Board.WHITE_BAR) {
            // White coming in from the bar - put back on bar
            Point destPoint = board.getPoint(to);
            destPoint.removeChecker();
            board.addToBar(PlayerColor.WHITE);
        } else if (from == Board.BLACK_BAR) {
            // Black coming in from the bar - put back on bar
            Point destPoint = board.getPoint(to);
            destPoint.removeChecker();
            board.addToBar(PlayerColor.BLACK);
        } else if (to == Board.WHITE_HOME) {
            // White bearing off - put back on point
            board.getPoint(from).addChecker(PlayerColor.WHITE);
            // Remove from home
            if (board.getHomeCount(PlayerColor.WHITE) > 0) {
                board.home.put(PlayerColor.WHITE, board.getHomeCount(PlayerColor.WHITE) - 1);
            }
        } else if (to == Board.BLACK_HOME) {
            // Black bearing off - put back on point
            board.getPoint(from).addChecker(PlayerColor.BLACK);
            // Remove from home
            if (board.getHomeCount(PlayerColor.BLACK) > 0) {
                board.home.put(PlayerColor.BLACK, board.getHomeCount(PlayerColor.BLACK) - 1);
            }
        } else {
            // Regular move
            Point fromPoint = board.getPoint(from);
            Point toPoint = board.getPoint(to);
            
            // Remove from destination
            PlayerColor color = toPoint.removeChecker();
            
            // Add back to source
            fromPoint.addChecker(color);
        }
        
        return true;
    }
    
    /**
     * Check if the game is over
     */
    private void checkGameOver() {
        // Check if white has won
        if (board.hasWon(PlayerColor.WHITE)) {
            gameState = GameState.ENDED;
            winner = PlayerColor.WHITE;
        }
        
        // Check if black has won
        if (board.hasWon(PlayerColor.BLACK)) {
            gameState = GameState.ENDED;
            winner = PlayerColor.BLACK;
        }
    }
    
    /**
     * Get a list of possible moves for the current player
     * @return list of possible moves
     */
    public List<Move> getPossibleMoves() {
        if (gameState != GameState.PLAYING || !dice.hasAvailableMoves()) {
            return new ArrayList<>();
        }
        
        return moveValidator.getPossibleMoves(currentPlayer, board, dice);
    }
    
    /**
     * Get the current game state
     * @return the game state
     */
    public GameState getGameState() {
        return gameState;
    }
    
    /**
     * Get the current player
     * @return the player whose turn it is
     */
    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Get the winner (if any)
     * @return the winner, or null if the game is not over
     */
    public PlayerColor getWinner() {
        return winner;
    }
    
    /**
     * Get the board
     * @return the game board
     */
    public Board getBoard() {
        return board;
    }
    
    /**
     * Get the dice
     * @return the dice
     */
    public Dice getDice() {
        return dice;
    }
    
    /**
     * Get the moves played this turn
     * @return list of moves played
     */
    public List<Move> getMovesPlayed() {
        return new ArrayList<>(movesPlayed);
    }
}