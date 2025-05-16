import { BoardPoint, PlayerColor, Move } from "./constants";
import { MoveValidator } from "./MoveValidator";
import { GameLogic } from "./GameLogic";

export class AI {
  private moveValidator: MoveValidator;
  private gameLogic: GameLogic;
  
  constructor() {
    this.moveValidator = new MoveValidator();
    this.gameLogic = new GameLogic();
  }
  
  /**
   * Get the best moves for the AI to make
   */
  getBestMoves(
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number },
    dice: number[],
    movesPlayed: Move[]
  ): Move[] {
    // Get all possible moves
    const allPossibleMoves = this.moveValidator.getAllPossibleMoves(
      playerColor,
      board,
      bar,
      dice,
      movesPlayed
    );
    
    if (allPossibleMoves.length === 0) {
      return [];
    }
    
    // Prioritize moves in this order:
    // 1. Get checkers off the bar
    // 2. Hit opponent checkers
    // 3. Bear off checkers
    // 4. Move checkers to safety (build points with multiple checkers)
    // 5. Move checkers to opponent's home board safely
    
    // Get checkers off the bar
    if (playerColor === "black" && bar.black > 0) {
      const barMoves = allPossibleMoves.filter(move => move.from === 24);
      
      if (barMoves.length > 0) {
        // Prioritize hitting opponent checkers when coming in from the bar
        const hitMoves = barMoves.filter(move => {
          return board[move.to] && board[move.to].color === "white" && board[move.to].count === 1;
        });
        
        if (hitMoves.length > 0) {
          return [hitMoves[0]];
        }
        
        // Otherwise, prioritize entering on points with own checkers
        const safeEntryMoves = barMoves.filter(move => {
          return board[move.to] && board[move.to].color === "black";
        });
        
        if (safeEntryMoves.length > 0) {
          return [safeEntryMoves[0]];
        }
        
        // If no safe entry, just enter on any open point
        return [barMoves[0]];
      }
    }
    
    // Look for hitting opponent checkers (blots)
    const hitMoves = allPossibleMoves.filter(move => {
      return board[move.to] && board[move.to].color === "white" && board[move.to].count === 1;
    });
    
    if (hitMoves.length > 0) {
      // Prioritize hitting opponent checkers in their home board
      const criticalHits = hitMoves.filter(move => move.to >= 1 && move.to <= 6);
      
      if (criticalHits.length > 0) {
        return [criticalHits[0]];
      }
      
      return [hitMoves[0]];
    }
    
    // Check if we can bear off
    if (this.gameLogic.allCheckersInHomeboard(playerColor, board, bar)) {
      const bearOffMoves = allPossibleMoves.filter(move => move.to === -2);
      
      if (bearOffMoves.length > 0) {
        // Prioritize bearing off the highest checker
        return [bearOffMoves.reduce((highest, move) => 
          move.from > highest.from ? move : highest, bearOffMoves[0])];
      }
    }
    
    // Build points (move to points with own checkers)
    const buildPointMoves = allPossibleMoves.filter(move => {
      return board[move.to] && board[move.to].color === playerColor;
    });
    
    if (buildPointMoves.length > 0) {
      // Prioritize building points in the home board
      const homeboardBuilds = buildPointMoves.filter(move => move.to >= 19 && move.to <= 24);
      
      if (homeboardBuilds.length > 0) {
        return [homeboardBuilds[0]];
      }
      
      return [buildPointMoves[0]];
    }
    
    // Move to safety (avoid leaving blots)
    // Check if the move would leave a single checker vulnerable
    const safeMoves = allPossibleMoves.filter(move => {
      // If there's more than one checker at the source, removing one won't leave a blot
      if (board[move.from] && board[move.from].count > 2) {
        return true;
      }
      
      // If there's exactly one checker, moving it clears the point
      if (board[move.from] && board[move.from].count === 1) {
        return true;
      }
      
      // If there are exactly two checkers, moving one would leave a blot
      return false;
    });
    
    if (safeMoves.length > 0) {
      return [safeMoves[0]];
    }
    
    // If no strategic moves found, just make any valid move
    return [allPossibleMoves[0]];
  }
}
