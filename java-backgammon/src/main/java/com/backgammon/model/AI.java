package com.backgammon.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * AI player for backgammon
 */
public class AI {
    private final MoveValidator moveValidator;
    private final PlayerColor aiColor;
    
    /**
     * Create a new AI player
     * @param aiColor the color the AI will play as
     */
    public AI(PlayerColor aiColor) {
        this.moveValidator = new MoveValidator();
        this.aiColor = aiColor;
    }
    
    /**
     * Get the best move for the AI to make
     * @param board the current board state
     * @param dice the current dice
     * @return the best move, or null if no moves are possible
     */
    public Move getBestMove(Board board, Dice dice) {
        // Get all possible moves
        List<Move> possibleMoves = moveValidator.getPossibleMoves(aiColor, board, dice);
        
        if (possibleMoves.isEmpty()) {
            return null;
        }
        
        // Score each move
        List<ScoredMove> scoredMoves = new ArrayList<>();
        for (Move move : possibleMoves) {
            int score = evaluateMove(move, board, dice);
            scoredMoves.add(new ScoredMove(move, score));
        }
        
        // Sort by score (highest first)
        scoredMoves.sort(Comparator.comparingInt(ScoredMove::getScore).reversed());
        
        // Return the highest-scoring move
        return scoredMoves.get(0).getMove();
    }
    
    /**
     * Evaluate a move and assign a score
     * @param move the move to evaluate
     * @param board the current board state
     * @param dice the current dice
     * @return the score (higher is better)
     */
    private int evaluateMove(Move move, Board board, Dice dice) {
        int score = 0;
        
        // Prioritize moves:
        // 1. Getting off the bar
        // 2. Bearing off checkers
        // 3. Hitting opponent blots
        // 4. Making points (building stacks)
        // 5. Moving out of opponent's home board
        // 6. Avoiding leaving blots
        
        // Getting off the bar is highest priority
        if (move.getFrom() == Board.BLACK_BAR || move.getFrom() == Board.WHITE_BAR) {
            score += 100;
            
            // Entering on a safe point (own checkers) is even better
            Point destPoint = board.getPoint(move.getTo());
            if (destPoint.hasColor(aiColor)) {
                score += 50;
            }
            
            // Hitting an opponent's blot is also good
            if (destPoint.hasColor(aiColor.opposite()) && destPoint.getCount() == 1) {
                score += 40;
            }
        }
        
        // Bearing off is very good
        if (move.getTo() == Board.BLACK_HOME || move.getTo() == Board.WHITE_HOME) {
            score += 90;
        }
        
        // Hitting opponent blots
        Point destPoint = board.getPoint(move.getTo());
        if (destPoint != null && destPoint.hasColor(aiColor.opposite()) && destPoint.getCount() == 1) {
            score += 80;
            
            // Hitting in opponent's home board is even better
            if ((aiColor == PlayerColor.WHITE && move.getTo() >= 0 && move.getTo() <= 5) ||
                (aiColor == PlayerColor.BLACK && move.getTo() >= 18 && move.getTo() <= 23)) {
                score += 20;
            }
        }
        
        // Making a point (building a stack on a point we already occupy)
        if (destPoint != null && destPoint.hasColor(aiColor)) {
            score += 60;
            
            // Making a point in our home board is even better
            if ((aiColor == PlayerColor.WHITE && move.getTo() >= 18 && move.getTo() <= 23) ||
                (aiColor == PlayerColor.BLACK && move.getTo() >= 0 && move.getTo() <= 5)) {
                score += 20;
            }
        }
        
        // Moving out of opponent's home board
        if ((aiColor == PlayerColor.WHITE && move.getFrom() >= 0 && move.getFrom() <= 5) ||
            (aiColor == PlayerColor.BLACK && move.getFrom() >= 18 && move.getFrom() <= 23)) {
            score += 30;
        }
        
        // Avoid leaving blots
        Point sourcePoint = board.getPoint(move.getFrom());
        if (sourcePoint != null && sourcePoint.getCount() == 2) {
            // Moving would leave a blot, which is risky
            score -= 20;
        }
        
        return score;
    }
    
    /**
     * Inner class to associate a move with its score
     */
    private static class ScoredMove {
        private final Move move;
        private final int score;
        
        public ScoredMove(Move move, int score) {
            this.move = move;
            this.score = score;
        }
        
        public Move getMove() {
            return move;
        }
        
        public int getScore() {
            return score;
        }
    }
}