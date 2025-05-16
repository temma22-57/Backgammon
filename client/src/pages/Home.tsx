import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAudio } from "@/lib/stores/useAudio";
import { Card, CardContent, CardHeader, CardTitle, CardDescription, CardFooter } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Dice5, Users, Bot, VolumeX, Volume2 } from "lucide-react";

export default function Home() {
  const navigate = useNavigate();
  const { isMuted, toggleMute } = useAudio();
  const [activeTab, setActiveTab] = useState("info");

  // Start game with the selected mode
  const startGame = (mode: "ai" | "local") => {
    navigate(`/game/${mode}`);
  };

  return (
    <div className="min-h-screen w-full flex flex-col items-center justify-center bg-background p-4">
      <Card className="w-full max-w-3xl">
        <CardHeader className="text-center">
          <CardTitle className="text-4xl font-bold mb-2">Backgammon</CardTitle>
          <CardDescription className="text-lg">
            Play the classic board game of strategy and luck
          </CardDescription>
        </CardHeader>
        
        <CardContent>
          <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="info">About the Game</TabsTrigger>
              <TabsTrigger value="play">Play Now</TabsTrigger>
            </TabsList>
            
            <TabsContent value="info" className="mt-4">
              <div className="space-y-4">
                <h3 className="text-xl font-semibold">How to Play Backgammon</h3>
                <p>
                  Backgammon is one of the oldest known board games. The objective is to move all your checkers into 
                  your home board and then bear them off. The first player to bear off all their checkers wins.
                </p>
                
                <h4 className="text-lg font-medium mt-4">Basic Rules:</h4>
                <ul className="list-disc list-inside space-y-2">
                  <li>Players take turns rolling two dice and moving their checkers accordingly</li>
                  <li>Checkers move counterclockwise for both players, from higher numbers to lower numbers</li>
                  <li>You can move one checker twice or two checkers once, based on the dice values</li>
                  <li>Landing on a single opponent checker "hits" it and sends it to the bar</li>
                  <li>Checkers on the bar must re-enter before any other move can be made</li>
                  <li>When all your checkers are in your home board, you can start bearing them off</li>
                </ul>
              </div>
            </TabsContent>
            
            <TabsContent value="play" className="mt-4">
              <div className="space-y-8">
                <div className="grid gap-6 sm:grid-cols-2">
                  <Card className="border hover:border-primary transition-colors cursor-pointer" onClick={() => startGame("local")}>
                    <CardHeader>
                      <CardTitle className="flex items-center gap-2">
                        <Users className="h-5 w-5" />
                        Local 2-Player
                      </CardTitle>
                      <CardDescription>
                        Play against a friend on the same device
                      </CardDescription>
                    </CardHeader>
                    <CardFooter>
                      <Button className="w-full" onClick={() => startGame("local")}>
                        Start Game
                      </Button>
                    </CardFooter>
                  </Card>
                  
                  <Card className="border hover:border-primary transition-colors cursor-pointer" onClick={() => startGame("ai")}>
                    <CardHeader>
                      <CardTitle className="flex items-center gap-2">
                        <Bot className="h-5 w-5" />
                        Play vs AI
                      </CardTitle>
                      <CardDescription>
                        Challenge our computer opponent
                      </CardDescription>
                    </CardHeader>
                    <CardFooter>
                      <Button className="w-full" onClick={() => startGame("ai")}>
                        Start Game
                      </Button>
                    </CardFooter>
                  </Card>
                </div>
                
                <div className="flex items-center justify-between p-4 bg-muted rounded-lg">
                  <div className="flex items-center space-x-2">
                    <Dice5 className="h-5 w-5" />
                    <span>Game Settings</span>
                  </div>
                  
                  <div className="flex items-center space-x-2">
                    <Button 
                      variant="ghost" 
                      size="icon" 
                      onClick={(e) => {
                        e.stopPropagation();
                        toggleMute();
                      }}
                    >
                      {isMuted ? <VolumeX /> : <Volume2 />}
                    </Button>
                    <div className="flex items-center space-x-2">
                      <Switch id="sound" checked={!isMuted} onCheckedChange={() => toggleMute()} />
                      <Label htmlFor="sound">Sound</Label>
                    </div>
                  </div>
                </div>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>
    </div>
  );
}
