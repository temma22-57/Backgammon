import { 
  BoardPoint, 
  PlayerColor,
  INITIAL_POSITIONS,
  WHITE_BAR,
  BLACK_BAR,
  WHITE_HOME,
  BLACK_HOME,
  CHECKERS_PER_PLAYER
} from "./constants";
import { Move } from "@/lib/stores/useBackgammon";

export class GameLogic {
  /**
   * Sets up a new game with the initial checker positions
   */
  setupNewGame(): Record<number, BoardPoint> {
    return { ...INITIAL_POSITIONS };
  }
  
  /**
   * Makes a move on the board
   * Returns the new board state, bar state, and the die value used
   */
  makeMove(
    from: number,
    to: number,
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number },
    dice: number[],
    movesPlayed: Move[]
  ): { 
    newBoard: Record<number, BoardPoint>; 
    newBar: { white: number; black: number };
    usedDieValue: number;
  } {
    // Create copies to modify
    const newBoard = { ...board };
    const newBar = { ...bar };
    
    // Calculate the die value needed for this move
    const usedDieValue = this.getDieValueForMove(from, to, playerColor);
    
    // Remove checker from source
    if (from === WHITE_BAR) {
      newBar.white--;
    } else if (from === BLACK_BAR) {
      newBar.black--;
    } else {
      newBoard[from].count--;
      if (newBoard[from].count === 0) {
        delete newBoard[from];
      }
    }
    
    // Handle landing on opponent's checker (hit)
    if (newBoard[to] && newBoard[to].color !== playerColor && newBoard[to].count === 1) {
      // Send opponent's checker to the bar
      const opponent = playerColor === "white" ? "black" : "white";
      newBar[opponent]++;
      
      // Remove the opponent's checker from the point
      delete newBoard[to];
    }
    
    // Add checker to destination
    if (to === WHITE_HOME || to === BLACK_HOME) {
      // Bearing off
      if (!newBoard[to]) {
        newBoard[to] = { color: playerColor, count: 1 };
      } else {
        newBoard[to].count++;
      }
    } else {
      if (!newBoard[to]) {
        newBoard[to] = { color: playerColor, count: 1 };
      } else {
        newBoard[to].count++;
      }
    }
    
    // Remove the used die from dice array
    // Find the first unused die with the right value
    const unusedDiceCount = dice.length - movesPlayed.length;
    const usedDieIndex = dice.findIndex((value, index) => {
      return value === usedDieValue && index >= dice.length - unusedDiceCount;
    });
    
    // If we found the die, move it to the beginning of the array (marking it as used)
    if (usedDieIndex !== -1) {
      const usedDie = dice.splice(usedDieIndex, 1)[0];
      dice.unshift(usedDie);
    }
    
    return { newBoard, newBar, usedDieValue };
  }
  
  /**
   * Undoes a move (for the undo feature)
   */
  undoMove(
    from: number,
    to: number,
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number }
  ): {
    newBoard: Record<number, BoardPoint>;
    newBar: { white: number; black: number };
  } {
    // Create copies to modify
    const newBoard = { ...board };
    const newBar = { ...bar };
    
    // Remove checker from destination
    if (to === WHITE_HOME || to === BLACK_HOME) {
      newBoard[to].count--;
      if (newBoard[to].count === 0) {
        delete newBoard[to];
      }
    } else {
      newBoard[to].count--;
      if (newBoard[to].count === 0) {
        delete newBoard[to];
      }
    }
    
    // Check if a hit was undone (if the point now has 0 checkers)
    // This is a heuristic - we assume that if we're undoing a move and
    // the destination is now empty, it was a hit move
    if (!newBoard[to]) {
      const opponent = playerColor === "white" ? "black" : "white";
      // Check if the opponent has checkers on the bar
      if (newBar[opponent] > 0) {
        // Add opponent's checker back to the point
        newBoard[to] = { color: opponent, count: 1 };
        // Remove from the bar
        newBar[opponent]--;
      }
    }
    
    // Add checker back to source
    if (from === WHITE_BAR) {
      newBar.white++;
    } else if (from === BLACK_BAR) {
      newBar.black++;
    } else {
      if (!newBoard[from]) {
        newBoard[from] = { color: playerColor, count: 1 };
      } else {
        newBoard[from].count++;
      }
    }
    
    return { newBoard, newBar };
  }
  
  /**
   * Calculate the die value needed for a move
   */
  getDieValueForMove(from: number, to: number, playerColor: PlayerColor): number {
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
        return to + 1;
      } else if (to === BLACK_HOME) {
        // Bearing off - die value is the exact point number or higher if exact not possible
        return 24 - from;
      } else {
        // Regular move - die value is the distance moved
        return to - from;
      }
    }
  }
  
  /**
   * Check if a player has won the game (all checkers borne off)
   */
  checkWinner(board: Record<number, BoardPoint>): PlayerColor | null {
    // Check white
    const whiteHome = board[WHITE_HOME];
    if (whiteHome && whiteHome.count === CHECKERS_PER_PLAYER) {
      return "white";
    }
    
    // Check black
    const blackHome = board[BLACK_HOME];
    if (blackHome && blackHome.count === CHECKERS_PER_PLAYER) {
      return "black";
    }
    
    // No winner yet
    return null;
  }
  
  /**
   * Check if all checkers are in the home board (for bearing off)
   */
  allCheckersInHomeboard(
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>,
    bar: { white: number; black: number }
  ): boolean {
    // If there are checkers on the bar, they're not in the home board
    if (playerColor === "white" && bar.white > 0) {
      return false;
    }
    if (playerColor === "black" && bar.black > 0) {
      return false;
    }
    
    // For white, home board is points 1-6
    if (playerColor === "white") {
      for (let i = 7; i <= 24; i++) {
        if (board[i-1] && board[i-1].color === "white" && board[i-1].count > 0) {
          return false;
        }
      }
    }
    
    // For black, home board is points 19-24
    if (playerColor === "black") {
      for (let i = 1; i <= 18; i++) {
        if (board[i-1] && board[i-1].color === "black" && board[i-1].count > 0) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  /**
   * Get the highest point in the home board that has a checker
   */
  getHighestCheckerInHomeboard(
    playerColor: PlayerColor,
    board: Record<number, BoardPoint>
  ): number {
    if (playerColor === "white") {
      for (let i = 6; i >= 1; i--) {
        if (board[i-1] && board[i-1].color === "white" && board[i-1].count > 0) {
          return i;
        }
      }
    } else {
      for (let i = 18; i >= 13; i--) {
        if (board[i] && board[i].color === "black" && board[i].count > 0) {
          return i + 1;
        }
      }
    }
    
    return 0; // No checkers found (shouldn't happen in normal play)
  }
}
