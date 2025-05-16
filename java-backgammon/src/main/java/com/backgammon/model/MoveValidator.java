package com.backgammon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates and generates possible moves based on the current board state and dice
 */
public class MoveValidator {
    /**
     * Check if a move is valid
     * @param from the source point index
     * @param to the destination point index
     * @param playerColor the player making the move
     * @param board the current board state
     * @param dice the current dice
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int from, int to, PlayerColor playerColor, Board board, Dice dice) {
        // If no dice rolled, no moves are valid
        if (!dice.hasAvailableMoves()) {
            return false;
        }
        
        // If player has checkers on the bar, they must move them first
        if (board.getBarCount(playerColor) > 0) {
            int barIndex = (playerColor == PlayerColor.WHITE) ? Board.WHITE_BAR : Board.BLACK_BAR;
            if (from != barIndex) {
                return false;
            }
        }
        
        // Calculate the die value needed for this move
        int dieNeeded = getDieValueForMove(from, to, playerColor);
        
        // Check if the required die value is available
        if (!dice.isValueAvailable(dieNeeded)) {
            return false;
        }
        
        // For bar moves
        if (from == Board.WHITE_BAR) {
            // White must enter on points 19-24
            if (to < 18 || to > 23) {
                return false;
            }
            // Check if the point is open (empty or own checkers or blot)
            Point point = board.getPoint(to);
            if (point.hasColor(PlayerColor.BLACK) && point.getCount() > 1) {
                return false;
            }
        } else if (from == Board.BLACK_BAR) {
            // Black must enter on points 1-6
            if (to < 0 || to > 5) {
                return false;
            }
            // Check if the point is open
            Point point = board.getPoint(to);
            if (point.hasColor(PlayerColor.WHITE) && point.getCount() > 1) {
                return false;
            }
        } else {
            // For regular moves (not from bar)
            
            // Check if the source point has player's checkers
            Point sourcePoint = board.getPoint(from);
            if (sourcePoint == null || !sourcePoint.hasColor(playerColor)) {
                return false;
            }
            
            // Check if the destination is valid (empty, player's checkers, or blot)
            Point destPoint = board.getPoint(to);
            if (destPoint != null && destPoint.hasColor(playerColor.opposite()) && destPoint.getCount() > 1) {
                return false;
            }
        }
        
        // For bearing off moves
        if (to == Board.WHITE_HOME || to == Board.BLACK_HOME) {
            // Check if all checkers are in the home board
            if (!board.allCheckersInHomeboard(playerColor)) {
                return false;
            }
            
            // For bearing off with exact dice or higher dice
            int highestPoint = board.getHighestCheckerInHomeboard(playerColor);
            
            if (playerColor == PlayerColor.WHITE) {
                // If the move is from the highest point, any die can be used
                if (from == highestPoint) {
                    return true;
                }
                
                // Otherwise, need exact die or no checkers on higher points
                int exactDie = from + 1;
                if (dice.isValueAvailable(exactDie)) {
                    return true;
                }
                
                // Check if using a higher die is valid (no checkers on higher points)
                boolean usingHigherDie = false;
                for (int dieValue : dice.getAvailableValues()) {
                    if (dieValue > exactDie) {
                        usingHigherDie = true;
                        break;
                    }
                }
                
                return usingHigherDie && from == highestPoint;
            } else {
                // Black bearing off
                // If the move is from the highest point, any die can be used
                if (from == highestPoint) {
                    return true;
                }
                
                // Otherwise, need exact die or no checkers on higher points
                int exactDie = 24 - from;
                if (dice.isValueAvailable(exactDie)) {
                    return true;
                }
                
                // Check if using a higher die is valid (no checkers on higher points)
                boolean usingHigherDie = false;
                for (int dieValue : dice.getAvailableValues()) {
                    if (dieValue > exactDie) {
                        usingHigherDie = true;
                        break;
                    }
                }
                
                return usingHigherDie && from == highestPoint;
            }
        }
        
        // If we've passed all checks, the move is valid
        return true;
    }
    
    /**
     * Get all possible moves for a player
     * @param playerColor the player's color
     * @param board the current board state
     * @param dice the current dice
     * @return a list of all possible moves
     */
    public List<Move> getPossibleMoves(PlayerColor playerColor, Board board, Dice dice) {
        List<Move> possibleMoves = new ArrayList<>();
        
        // If player has checkers on the bar, they must move them first
        if (board.getBarCount(playerColor) > 0) {
            int barIndex = (playerColor == PlayerColor.WHITE) ? Board.WHITE_BAR : Board.BLACK_BAR;
            int[] entryPoints = (playerColor == PlayerColor.WHITE) 
                ? new int[]{18, 19, 20, 21, 22, 23} // White enters on points 19-24
                : new int[]{0, 1, 2, 3, 4, 5}; // Black enters on points 1-6
            
            for (int point : entryPoints) {
                if (isValidMove(barIndex, point, playerColor, board, dice)) {
                    possibleMoves.add(new Move(barIndex, point));
                }
            }
            
            return possibleMoves; // If there are checkers on the bar, only bar moves are allowed
        }
        
        // Get possible moves from each point
        for (int i = 0; i < 24; i++) {
            Point point = board.getPoint(i);
            if (point != null && point.hasColor(playerColor)) {
                // For each die value, check if a move is possible
                for (int dieValue : dice.getAvailableValues()) {
                    int to;
                    if (playerColor == PlayerColor.WHITE) {
                        to = i - dieValue;
                        // Check if bearing off is possible
                        if (to < 0 && board.allCheckersInHomeboard(playerColor)) {
                            to = Board.WHITE_HOME;
                        }
                    } else {
                        to = i + dieValue;
                        // Check if bearing off is possible
                        if (to > 23 && board.allCheckersInHomeboard(playerColor)) {
                            to = Board.BLACK_HOME;
                        }
                    }
                    
                    // Check if the move is valid
                    if ((to >= 0 && to <= 23) || to == Board.WHITE_HOME || to == Board.BLACK_HOME) {
                        if (isValidMove(i, to, playerColor, board, dice)) {
                            possibleMoves.add(new Move(i, to));
                        }
                    }
                }
            }
        }
        
        return possibleMoves;
    }
    
    /**
     * Calculate the die value needed for a move
     * @param from the source point index
     * @param to the destination point index
     * @param playerColor the player making the move
     * @return the die value needed for the move
     */
    private int getDieValueForMove(int from, int to, PlayerColor playerColor) {
        if (playerColor == PlayerColor.WHITE) {
            // White moves from 24 to 1 (point indices 23 to 0)
            if (from == Board.WHITE_BAR) {
                // Coming in from the bar: die value is point number (24 - destination)
                return 24 - to;
            } else if (to == Board.WHITE_HOME) {
                // Bearing off: die value is the point number + 1
                return from + 1;
            } else {
                // Regular move: die value is the distance
                return from - to;
            }
        } else {
            // Black moves from 1 to 24 (point indices 0 to 23)
            if (from == Board.BLACK_BAR) {
                // Coming in from the bar: die value is point number
                return to + 1;
            } else if (to == Board.BLACK_HOME) {
                // Bearing off: die value is 24 - point number
                return 24 - from;
            } else {
                // Regular move: die value is the distance
                return to - from;
            }
        }
    }
}