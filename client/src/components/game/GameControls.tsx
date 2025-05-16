import React, { useEffect } from "react";
import { Button } from "@/components/ui/button";
import { useBackgammon } from "@/lib/stores/useBackgammon";
import { GameState } from "@/lib/backgammon/constants";
import { PlayIcon, SkipForwardIcon, Dice5 } from "lucide-react";

interface GameControlsProps {
  gameState: GameState;
  playerTurn: "white" | "black";
  isAgainstAI: boolean;
  dice: number[];
  movesPlayed: { from: number; to: number }[];
}

const GameControls: React.FC<GameControlsProps> = ({
  gameState,
  playerTurn,
  isAgainstAI,
  dice,
  movesPlayed,
}) => {
  const { 
    rollDice, 
    endTurn, 
    resetGame, 
    undoMove,
    confirmDiceUse,
    aiMove,
    winner
  } = useBackgammon();

  // When it's AI's turn in AI mode, make the AI move
  useEffect(() => {
    if (isAgainstAI && playerTurn === "black" && gameState === "playing" && dice.length > 0) {
      // Add a small delay to make it feel more natural
      const timeout = setTimeout(() => {
        aiMove();
      }, 1000);
      
      return () => clearTimeout(timeout);
    }
  }, [isAgainstAI, playerTurn, gameState, dice, aiMove]);

  return (
    <div className="space-y-4">
      <div className="bg-muted p-3 rounded-md">
        <div className="text-lg font-semibold mb-2">Game Status</div>
        
        <div className="grid grid-cols-2 gap-2 mb-4">
          <div className="bg-background p-2 rounded text-center">
            <div className="text-xs text-muted-foreground mb-1">Current Turn</div>
            <div className="font-medium flex items-center justify-center gap-1">
              <span 
                className="inline-block w-3 h-3 rounded-full" 
                style={{ 
                  backgroundColor: playerTurn === "white" ? "white" : "black",
                  border: "1px solid #666" 
                }}
              />
              {playerTurn === "white" ? "White" : "Black"}
            </div>
          </div>
          
          <div className="bg-background p-2 rounded text-center">
            <div className="text-xs text-muted-foreground mb-1">Game Phase</div>
            <div className="font-medium">
              {gameState === "initial" && "Not Started"}
              {gameState === "playing" && "In Progress"}
              {gameState === "ended" && "Game Over"}
            </div>
          </div>
        </div>
        
        {winner && (
          <div className="bg-accent text-accent-foreground p-3 rounded-md mb-4 text-center">
            <div className="font-bold text-lg mb-1">
              {winner === "white" ? "White" : "Black"} Wins!
            </div>
            <Button 
              variant="outline" 
              className="mt-2" 
              size="sm"
              onClick={resetGame}
            >
              Play Again
            </Button>
          </div>
        )}
        
        {/* Dice information */}
        {dice.length > 0 && (
          <div className="mb-4">
            <div className="text-sm font-medium mb-1">Dice Values:</div>
            <div className="flex items-center gap-2">
              {dice.map((value, index) => (
                <div 
                  key={index}
                  className={`w-8 h-8 flex items-center justify-center rounded ${
                    index >= dice.length - movesPlayed.length 
                      ? "bg-muted-foreground/30" 
                      : "bg-background"
                  }`}
                >
                  {value}
                </div>
              ))}
            </div>
            <div className="text-xs text-muted-foreground mt-1">
              {movesPlayed.length > 0 
                ? `${movesPlayed.length} moves made, ${dice.length - movesPlayed.length} remaining`
                : `${dice.length} moves available`}
            </div>
          </div>
        )}
        
        {/* Player or AI indicator */}
        {isAgainstAI && (
          <div className="text-sm flex items-center justify-between mb-2">
            <span>
              <span className="font-semibold">You:</span> White
            </span>
            <span>
              <span className="font-semibold">AI:</span> Black
            </span>
          </div>
        )}
      </div>
      
      {/* Game controls */}
      <div className="space-y-2">
        {gameState === "playing" && (
          <>
            {/* Roll dice button - show only when no dice are rolled yet */}
            {dice.length === 0 && (
              <Button 
                className="w-full flex items-center gap-2" 
                onClick={rollDice}
                disabled={
                  (isAgainstAI && playerTurn === "black") || 
                  gameState !== "playing"
                }
              >
                <Dice5 size={16} />
                Roll Dice
              </Button>
            )}
            
            {/* End turn button - show only when dice are rolled */}
            {dice.length > 0 && (
              <>
                <Button 
                  className="w-full" 
                  onClick={endTurn}
                  disabled={
                    (isAgainstAI && playerTurn === "black") || 
                    gameState !== "playing"
                  }
                >
                  <SkipForwardIcon size={16} className="mr-2" />
                  End Turn
                </Button>
                
                {/* Confirm dice usage button */}
                {movesPlayed.length > 0 && (
                  <Button 
                    variant="outline" 
                    className="w-full"
                    onClick={confirmDiceUse}
                    disabled={
                      (isAgainstAI && playerTurn === "black") || 
                      gameState !== "playing"
                    }
                  >
                    Confirm Move
                  </Button>
                )}
                
                {/* Undo move button */}
                {movesPlayed.length > 0 && (
                  <Button 
                    variant="outline" 
                    className="w-full"
                    onClick={undoMove}
                    disabled={
                      movesPlayed.length === 0 || 
                      (isAgainstAI && playerTurn === "black") || 
                      gameState !== "playing"
                    }
                  >
                    Undo Last Move
                  </Button>
                )}
              </>
            )}
          </>
        )}
        
        {gameState === "initial" && (
          <Button className="w-full" onClick={() => rollDice()}>
            <PlayIcon size={16} className="mr-2" />
            Start Game
          </Button>
        )}
      </div>
      
      <div className="text-xs text-muted-foreground mt-4">
        <p className="mb-1"><strong>Game Rules:</strong></p>
        <ul className="list-disc pl-4 space-y-1">
          <li>Roll dice and move your checkers in the direction of your home board</li>
          <li>White moves from 24 to 1, Black moves from 1 to 24</li>
          <li>Landing on a single opponent checker sends it to the bar</li>
          <li>Checkers on the bar must re-enter before any other move</li>
          <li>When all your checkers are in your home board, you can bear them off</li>
        </ul>
      </div>
    </div>
  );
};

export default GameControls;
