import { BoardPoint, PlayerColor, Move, WHITE_BAR, BLACK_BAR, WHITE_HOME, BLACK_HOME } from "./constants";
import { GameLogic } from "./GameLogic";

export class MoveValidator {
  private gameLogic: GameLogic;
  
  constructor() {
    this.gameLogic = new GameLogic();
  }
  
  /**
   * Checks if a move from one point to another is valid
   */
  isValidMove(
    from: number,
    to: number,
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number },
    dice: number[],
    movesPlayed: Move[]
  ): boolean {
    // If no dice rolled, no moves are valid
    if (dice.length === 0) {
      return false;
    }
    
    // Calculate remaining dice (unused)
    const remainingDice = dice.slice(movesPlayed.length);
    
    // If no remaining dice, no moves are valid
    if (remainingDice.length === 0) {
      return false;
    }
    
    // If player has checkers on the bar, they must move them first
    if (playerColor === "white" && bar.white > 0) {
      if (from !== WHITE_BAR) {
        return false;
      }
    }
    
    if (playerColor === "black" && bar.black > 0) {
      if (from !== BLACK_BAR) {
        return false;
      }
    }
    
    // Calculate the die value needed for this move
    const dieNeeded = this.getDieValueForMove(from, to, playerColor);
    
    // Check if any of the remaining dice match the required value
    const hasDie = remainingDice.includes(dieNeeded);
    
    // For bar moves
    if (from === WHITE_BAR) {
      // Must enter on opponent's outer board (points 19-24)
      if (to < 19 || to > 24) {
        return false;
      }
      // Check if the point is open (empty or own checkers)
      if (board[to] && board[to].color !== playerColor && board[to].count > 1) {
        return false;
      }
    }
    
    if (from === BLACK_BAR) {
      // Must enter on opponent's outer board (points 1-6)
      if (to < 1 || to > 6) {
        return false;
      }
      // Check if the point is open (empty or own checkers)
      if (board[to-1] && board[to-1].color !== playerColor && board[to-1].count > 1) {
        return false;
      }
    }
    
    // For regular moves (not from bar)
    if (from !== WHITE_BAR && from !== BLACK_BAR) {
      // Check if the source point has player's checkers
      if (!board[from] || board[from].color !== playerColor || board[from].count === 0) {
        return false;
      }
      
      // Check if the destination point is valid (empty or player's checkers or single opponent checker)
      if (board[to] && board[to].color !== playerColor && board[to].count > 1) {
        return false;
      }
    }
    
    // For bearing off moves
    if (to === WHITE_HOME || to === BLACK_HOME) {
      // Check if all checkers are in the home board
      if (!this.gameLogic.allCheckersInHomeboard(playerColor, board, bar)) {
        return false;
      }
      
      // For bearing off with exact dice
      if (hasDie) {
        return true;
      }
      
      // For bearing off with higher dice when no exact match is available
      // (only if no lower point has a checker)
      // Get the highest point in the home board with a checker
      const highestPoint = this.gameLogic.getHighestCheckerInHomeboard(playerColor, board);
      
      if (playerColor === "white") {
        // If the point is exactly the die value away from home
        if (from + 1 === dieNeeded) {
          return true;
        }
        
        // If using a higher die, make sure there are no checkers on lower points
        const usingHigherDie = remainingDice.some(die => die > from + 1);
        if (usingHigherDie && from === highestPoint) {
          return true;
        }
      } else {
        // If the point is exactly the die value away from home
        if (24 - from === dieNeeded) {
          return true;
        }
        
        // If using a higher die, make sure there are no checkers on lower points
        const usingHigherDie = remainingDice.some(die => die > 24 - from);
        if (usingHigherDie && from === highestPoint) {
          return true;
        }
      }
      
      return false;
    }
    
    // If we have the required die, the move is valid
    return hasDie;
  }
  
  /**
   * Gets all possible moves from a given point
   */
  getPossibleMoves(
    from: number,
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number },
    dice: number[],
    movesPlayed: Move[]
  ): Move[] {
    const possibleMoves: Move[] = [];
    
    // Calculate remaining dice (unused)
    const remainingDice = dice.slice(movesPlayed.length);
    
    // If no remaining dice, no moves are possible
    if (remainingDice.length === 0) {
      return possibleMoves;
    }
    
    // Handle moves from the bar
    if (from === WHITE_BAR || from === BLACK_BAR) {
      const pointsToCheck = from === WHITE_BAR ? [19, 20, 21, 22, 23, 24] : [1, 2, 3, 4, 5, 6];
      
      for (const point of pointsToCheck) {
        const to = point;
        
        if (this.isValidMove(from, to, playerColor, board, bar, dice, movesPlayed)) {
          possibleMoves.push({ from, to });
        }
      }
      
      return possibleMoves;
    }
    
    // Handle bearing off moves
    const canBearOff = this.gameLogic.allCheckersInHomeboard(playerColor, board, bar);
    
    if (canBearOff) {
      const homeDest = playerColor === "white" ? WHITE_HOME : BLACK_HOME;
      
      if (this.isValidMove(from, homeDest, playerColor, board, bar, dice, movesPlayed)) {
        possibleMoves.push({ from, to: homeDest });
      }
    }
    
    // Handle regular moves
    for (const die of remainingDice) {
      let to: number;
      
      if (playerColor === "white") {
        to = from - die;
        if (to >= 1) {
          if (this.isValidMove(from, to, playerColor, board, bar, dice, movesPlayed)) {
            possibleMoves.push({ from, to });
          }
        }
      } else {
        to = from + die;
        if (to <= 24) {
          if (this.isValidMove(from, to, playerColor, board, bar, dice, movesPlayed)) {
            possibleMoves.push({ from, to });
          }
        }
      }
    }
    
    return possibleMoves;
  }
  
  /**
   * Gets all possible moves for a player with the current dice
   */
  getAllPossibleMoves(
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number },
    dice: number[],
    movesPlayed: Move[]
  ): Move[] {
    const allMoves: Move[] = [];
    
    // If player has checkers on the bar, they must move them first
    if (playerColor === "white" && bar.white > 0) {
      return this.getPossibleMoves(WHITE_BAR, playerColor, board, bar, dice, movesPlayed);
    }
    
    if (playerColor === "black" && bar.black > 0) {
      return this.getPossibleMoves(BLACK_BAR, playerColor, board, bar, dice, movesPlayed);
    }
    
    // Check all points for possible moves
    for (const pointIndex in board) {
      const point = board[parseInt(pointIndex)];
      
      if (point && point.color === playerColor && point.count > 0) {
        const movesFromPoint = this.getPossibleMoves(
          parseInt(pointIndex),
          playerColor,
          board,
          bar,
          dice,
          movesPlayed
        );
        
        allMoves.push(...movesFromPoint);
      }
    }
    
    return allMoves;
  }
  
  /**
   * Calculate the die value needed for a move
   */
  private getDieValueForMove(from: number, to: number, playerColor: PlayerColor): number {
    if (playerColor === "white") {
      // White moves from 24 to 1
      if (from === WHITE_BAR) {
        // Bar to board - die value is 25 - destination point
        return 25 - to;
      } else if (to === WHITE_HOME) {
        // Bearing off - die value is the exact point number or higher if exact not possible
        return from + 1;
      } else {
        // Regular move - die value is the distance moved
        return from - to;
      }
    } else {
      // Black moves from 1 to 24
      if (from === BLACK_BAR) {
        // Bar to board - die value is destination point
        return to;
      } else if (to === BLACK_HOME) {
        // Bearing off - die value is the exact point number or higher if exact not possible
        return 24 - from;
      } else {
        // Regular move - die value is the distance moved
        return to - from;
      }
    }
  }
}
