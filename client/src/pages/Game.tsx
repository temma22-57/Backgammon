import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Board from "@/components/game/Board";
import GameControls from "@/components/game/GameControls";
import { useBackgammon } from "@/lib/stores/useBackgammon";
import { Button } from "@/components/ui/button";
import { useAudio } from "@/lib/stores/useAudio";
import { HomeIcon, RotateCw, VolumeX, Volume2 } from "lucide-react";
import { toast } from "sonner";

export default function Game() {
  const { mode } = useParams<{ mode: string }>();
  const navigate = useNavigate();
  const [isValidMode, setIsValidMode] = useState(false);

  const { 
    initGame, 
    gameState, 
    playerTurn, 
    winner,
    resetGame,
    dice,
    movesPlayed
  } = useBackgammon();

  const { backgroundMusic, isMuted, toggleMute } = useAudio();

  // Initialize game based on mode
  useEffect(() => {
    if (mode === "ai" || mode === "local") {
      setIsValidMode(true);
      initGame(mode === "ai");
    } else {
      navigate("/404");
    }

    // Start background music
    if (backgroundMusic && !isMuted) {
      backgroundMusic.play().catch(error => {
        console.log("Background music play prevented by browser policy:", error);
      });
    }

    return () => {
      // Stop music when leaving game page
      if (backgroundMusic) {
        backgroundMusic.pause();
        backgroundMusic.currentTime = 0;
      }
    };
  }, [mode, initGame, navigate, backgroundMusic, isMuted]);

  // Show winner toast
  useEffect(() => {
    if (winner) {
      toast.success(`${winner === 'white' ? 'White' : 'Black'} player wins!`, {
        duration: 5000,
      });
    }
  }, [winner]);

  if (!isValidMode) {
    return <div>Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <header className="p-4 border-b flex items-center justify-between">
        <h1 className="text-xl font-semibold">Backgammon</h1>
        <div className="flex gap-2">
          <Button variant="outline" size="icon" onClick={toggleMute}>
            {isMuted ? <VolumeX size={18} /> : <Volume2 size={18} />}
          </Button>
          <Button variant="outline" size="icon" onClick={() => resetGame()}>
            <RotateCw size={18} />
          </Button>
          <Button variant="outline" size="icon" onClick={() => navigate("/")}>
            <HomeIcon size={18} />
          </Button>
        </div>
      </header>

      <main className="flex-1 flex flex-col md:flex-row p-4 gap-4">
        <div className="flex-1 flex items-center justify-center">
          <Board />
        </div>
        
        <div className="w-full md:w-80 p-4 border rounded-lg bg-card">
          <GameControls 
            gameState={gameState} 
            playerTurn={playerTurn}
            isAgainstAI={mode === "ai"}
            dice={dice}
            movesPlayed={movesPlayed}
          />
        </div>
      </main>
    </div>
  );
}
