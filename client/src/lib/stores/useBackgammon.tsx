import { create } from "zustand";
import { MoveValidator } from "@/lib/backgammon/MoveValidator";
import { GameLogic } from "@/lib/backgammon/GameLogic";
import { AI } from "@/lib/backgammon/AI";
import { GameState, PlayerColor, BoardPoint } from "@/lib/backgammon/constants";

export interface Move {
  from: number;
  to: number;
}

interface BackgammonStore {
  // Game state
  gameState: GameState;
  playerTurn: PlayerColor;
  board: Record<number, BoardPoint>;
  bar: { white: number; black: number };
  dice: number[];
  selectedPoint: number | null;
  possibleMoves: Move[];
  movesPlayed: Move[];
  winner: PlayerColor | null;
  
  // Actions
  initGame: (aiMode?: boolean) => void;
  resetGame: () => void;
  rollDice: () => void;
  selectPoint: (pointIndex: number) => void;
  endTurn: () => void;
  undoMove: () => void;
  confirmDiceUse: () => void;
  aiMove: () => void;
  
  // Helpers
  isMovePossible: (from: number, to: number) => boolean;
}

export const useBackgammon = create<BackgammonStore>((set, get) => {
  // Create instances of our game logic
  const gameLogic = new GameLogic();
  const moveValidator = new MoveValidator();
  const ai = new AI();
  
  return {
    // Initial state
    gameState: "initial",
    playerTurn: "white", // White goes first
    board: {},
    bar: { white: 0, black: 0 },
    dice: [],
    selectedPoint: null,
    possibleMoves: [],
    movesPlayed: [],
    winner: null,
    
    initGame: (aiMode = false) => {
      const initialBoard = gameLogic.setupNewGame();
      
      set({
        gameState: "playing",
        playerTurn: "white",
        board: initialBoard,
        bar: { white: 0, black: 0 },
        dice: [],
        selectedPoint: null,
        possibleMoves: [],
        movesPlayed: [],
        winner: null,
      });
      
      console.log("Game initialized");
    },
    
    resetGame: () => {
      const initialBoard = gameLogic.setupNewGame();
      
      set({
        gameState: "playing",
        playerTurn: "white",
        board: initialBoard,
        bar: { white: 0, black: 0 },
        dice: [],
        selectedPoint: null,
        possibleMoves: [],
        movesPlayed: [],
        winner: null,
      });
      
      console.log("Game reset");
    },
    
    rollDice: () => {
      const { gameState, playerTurn } = get();
      
      if (gameState !== "playing") {
        return;
      }
      
      // Roll two dice
      const die1 = Math.floor(Math.random() * 6) + 1;
      const die2 = Math.floor(Math.random() * 6) + 1;
      
      // If doubles, player gets 4 moves with the same value
      const newDice = die1 === die2 
        ? [die1, die1, die1, die1] 
        : [die1, die2];
      
      console.log(`${playerTurn} rolled: ${newDice.join(', ')}`);
      
      set({ dice: newDice, movesPlayed: [] });
    },
    
    selectPoint: (pointIndex: number) => {
      const { 
        gameState, 
        playerTurn, 
        board, 
        bar, 
        dice, 
        selectedPoint,
        movesPlayed 
      } = get();
      
      if (gameState !== "playing" || dice.length === 0) {
        return;
      }
      
      // If we already have a selected point
      if (selectedPoint !== null) {
        // Check if we can move from selected point to the target point
        if (moveValidator.isValidMove(
          selectedPoint, 
          pointIndex, 
          playerTurn, 
          board, 
          bar, 
          dice, 
          movesPlayed
        )) {
          // Make the move
          const { newBoard, newBar, usedDieValue } = gameLogic.makeMove(
            selectedPoint, 
            pointIndex, 
            playerTurn, 
            { ...board }, 
            { ...bar }, 
            [...dice],
            [...movesPlayed]
          );
          
          // Add the move to moves played
          const newMovesPlayed = [...movesPlayed, { from: selectedPoint, to: pointIndex }];
          
          // Update the state
          set({ 
            board: newBoard, 
            bar: newBar, 
            selectedPoint: null,
            possibleMoves: [],
            movesPlayed: newMovesPlayed
          });
          
          console.log(`Move from ${selectedPoint} to ${pointIndex} using ${usedDieValue}`);
          
          // Check if the game is over
          const winner = gameLogic.checkWinner(newBoard);
          if (winner) {
            set({ 
              gameState: "ended",
              winner
            });
            console.log(`Game over! ${winner} wins!`);
          }
          
          return;
        } else {
          // Invalid move, just deselect
          set({ selectedPoint: null, possibleMoves: [] });
          return;
        }
      }
      
      // Selecting a point for the first time

      // If player has checkers on the bar, they must move them first
      if (bar[playerTurn] > 0) {
        // For player with checkers on the bar, only allow selecting the bar
        if ((playerTurn === "white" && pointIndex === -1) || 
            (playerTurn === "black" && pointIndex === 24)) {
          const possibleMoves = moveValidator.getPossibleMoves(
            pointIndex, 
            playerTurn, 
            board, 
            bar, 
            dice, 
            movesPlayed
          );
          
          if (possibleMoves.length > 0) {
            set({ 
              selectedPoint: pointIndex,
              possibleMoves
            });
          }
        }
        return;
      }
      
      // Check if the selected point has current player's checkers
      const point = board[pointIndex];
      if (point && point.color === playerTurn && point.count > 0) {
        // Get possible moves from this point
        const possibleMoves = moveValidator.getPossibleMoves(
          pointIndex, 
          playerTurn, 
          board, 
          bar, 
          dice, 
          movesPlayed
        );
        
        if (possibleMoves.length > 0) {
          set({ 
            selectedPoint: pointIndex,
            possibleMoves
          });
        } else {
          console.log(`No valid moves from point ${pointIndex}`);
        }
      }
    },
    
    endTurn: () => {
      const { playerTurn, dice } = get();
      
      if (dice.length === 0) {
        return;
      }
      
      const nextPlayer = playerTurn === "white" ? "black" : "white";
      
      set({
        playerTurn: nextPlayer,
        dice: [],
        selectedPoint: null,
        possibleMoves: [],
        movesPlayed: [],
      });
      
      console.log(`Turn ended. Now it's ${nextPlayer}'s turn`);
    },
    
    undoMove: () => {
      const { 
        board, 
        bar, 
        movesPlayed 
      } = get();
      
      if (movesPlayed.length === 0) {
        return;
      }
      
      // Remove the last move
      const newMovesPlayed = [...movesPlayed];
      const lastMove = newMovesPlayed.pop();
      
      if (lastMove) {
        // Restore the game state before the move
        const { newBoard, newBar } = gameLogic.undoMove(
          lastMove.from,
          lastMove.to,
          get().playerTurn,
          { ...board },
          { ...bar }
        );
        
        set({ 
          board: newBoard, 
          bar: newBar, 
          selectedPoint: null,
          possibleMoves: [],
          movesPlayed: newMovesPlayed
        });
        
        console.log(`Undid move from ${lastMove.from} to ${lastMove.to}`);
      }
    },
    
    confirmDiceUse: () => {
      const { movesPlayed, dice } = get();
      
      if (movesPlayed.length === 0) {
        return;
      }
      
      // For now, just end the turn when confirming a move
      const nextPlayer = get().playerTurn === "white" ? "black" : "white";
      
      set({
        playerTurn: nextPlayer,
        dice: [],
        selectedPoint: null,
        possibleMoves: [],
        movesPlayed: [],
      });
      
      console.log(`Moves confirmed. Now it's ${nextPlayer}'s turn`);
    },
    
    aiMove: () => {
      const { 
        playerTurn, 
        board, 
        bar, 
        dice, 
        movesPlayed 
      } = get();
      
      if (playerTurn !== "black" || dice.length === 0) {
        return;
      }
      
      // Get AI move
      const aiMoves = ai.getBestMoves(
        playerTurn,
        board,
        bar,
        dice,
        movesPlayed
      );
      
      if (aiMoves.length === 0) {
        // AI can't move, end turn
        set({
          playerTurn: "white",
          dice: [],
          selectedPoint: null,
          possibleMoves: [],
          movesPlayed: [],
        });
        console.log("AI has no valid moves. Turn ended.");
        return;
      }
      
      // Apply all AI moves sequentially
      let currentBoard = { ...board };
      let currentBar = { ...bar };
      let newMovesPlayed = [...movesPlayed];
      
      for (const move of aiMoves) {
        const result = gameLogic.makeMove(
          move.from,
          move.to,
          playerTurn,
          currentBoard,
          currentBar,
          dice,
          newMovesPlayed
        );
        
        currentBoard = result.newBoard;
        currentBar = result.newBar;
        newMovesPlayed.push({ from: move.from, to: move.to });
        
        console.log(`AI moved from ${move.from} to ${move.to}`);
      }
      
      // Check if the game is over
      const winner = gameLogic.checkWinner(currentBoard);
      
      if (winner) {
        set({
          board: currentBoard,
          bar: currentBar,
          gameState: "ended",
          winner,
          dice: [],
          selectedPoint: null,
          possibleMoves: [],
          movesPlayed: [],
        });
        console.log(`Game over! ${winner} wins!`);
      } else {
        // End AI turn
        set({
          board: currentBoard,
          bar: currentBar,
          playerTurn: "white",
          dice: [],
          selectedPoint: null,
          possibleMoves: [],
          movesPlayed: [],
        });
      }
    },
    
    isMovePossible: (from, to) => {
      const { 
        playerTurn, 
        board, 
        bar, 
        dice, 
        possibleMoves,
        selectedPoint,
        movesPlayed 
      } = get();
      
      if (selectedPoint === null) {
        return false;
      }
      
      // If we already have possible moves calculated, check those
      if (from === selectedPoint && possibleMoves.length > 0) {
        return possibleMoves.some(move => move.to === to);
      }
      
      // Otherwise, check if the move is valid
      return moveValidator.isValidMove(
        from,
        to,
        playerTurn,
        board,
        bar,
        dice,
        movesPlayed
      );
    }
  };
});
