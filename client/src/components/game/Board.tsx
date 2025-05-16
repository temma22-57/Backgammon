import { useEffect, useRef, useState } from "react";
import { useBackgammon } from "@/lib/stores/useBackgammon";
import { COLORS, POINT_WIDTH, BOARD_PADDING } from "@/lib/backgammon/constants";
import { useAudio } from "@/lib/stores/useAudio";
import Point from "./Point";
import Checker from "./Checker";
import Dice from "./Dice";

const Board = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const boardContainerRef = useRef<HTMLDivElement>(null);
  const [boardSize, setBoardSize] = useState({ width: 800, height: 600 });
  const [scale, setScale] = useState(1);
  const [selectedPointIndex, setSelectedPointIndex] = useState<number | null>(null);
  const [hoveredPointIndex, setHoveredPointIndex] = useState<number | null>(null);
  
  const { playHit } = useAudio();
  
  const { 
    board, 
    bar, 
    playerTurn, 
    gameState, 
    dice, 
    selectPoint, 
    possibleMoves,
    isMovePossible
  } = useBackgammon();
  
  // Handle window resize
  useEffect(() => {
    const handleResize = () => {
      if (boardContainerRef.current) {
        const containerWidth = boardContainerRef.current.clientWidth;
        const containerHeight = boardContainerRef.current.clientHeight;
        
        // Calculate scale to fit the board in the container
        const idealWidth = 800;
        const idealHeight = 600;
        const widthScale = containerWidth / idealWidth;
        const heightScale = containerHeight / idealHeight;
        const newScale = Math.min(widthScale, heightScale, 1); // Cap at 1x for larger screens
        
        setBoardSize({
          width: idealWidth * newScale,
          height: idealHeight * newScale
        });
        setScale(newScale);
      }
    };
    
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);
  
  // Handle point click
  const handlePointClick = (pointIndex: number) => {
    if (gameState !== 'playing') return;
    
    // If clicking on a point that has a possible move from the selected point
    if (selectedPointIndex !== null && possibleMoves.some(move => move.to === pointIndex)) {
      // Make the move to the clicked point
      selectPoint(pointIndex);
      playHit(); // Play sound for moving pieces
    } else {
      // Select this point if it has the current player's checkers
      selectPoint(pointIndex);
    }
  };
  
  const handleBarClick = (color: 'white' | 'black') => {
    if (gameState !== 'playing' || color !== playerTurn) return;
    
    // If player has checkers on the bar, they must move them first
    if (bar[color] > 0) {
      selectPoint(color === 'white' ? -1 : 24);
      playHit();
    }
  };
  
  return (
    <div 
      ref={boardContainerRef} 
      className="relative w-full h-full flex items-center justify-center"
      style={{ 
        backgroundImage: `url(/textures/wood.jpg)`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        borderRadius: '0.5rem',
        boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        overflow: 'hidden'
      }}
    >
      <div 
        className="relative"
        style={{ 
          width: boardSize.width, 
          height: boardSize.height,
          transform: `scale(${scale})`,
          transformOrigin: 'center'
        }}
      >
        {/* Board background */}
        <div 
          className="absolute inset-0" 
          style={{ 
            backgroundColor: COLORS.BOARD_BG, 
            padding: BOARD_PADDING,
            borderRadius: '8px'
          }} 
        />
        
        {/* Center bar */}
        <div 
          className="absolute"
          style={{
            top: BOARD_PADDING,
            left: '50%',
            transform: 'translateX(-50%)',
            width: '30px',
            height: boardSize.height - (BOARD_PADDING * 2),
            backgroundColor: COLORS.BAR_BG,
            zIndex: 1
          }}
        />
        
        {/* Points - Bottom row (13-24) */}
        <div 
          className="absolute"
          style={{
            bottom: BOARD_PADDING,
            left: BOARD_PADDING,
            right: BOARD_PADDING,
            height: (boardSize.height - (BOARD_PADDING * 2)) / 2,
            display: 'flex',
            flexDirection: 'row',
            zIndex: 2
          }}
        >
          {/* Bottom left quadrant (points 13-18) */}
          <div className="flex flex-1">
            {Array.from({ length: 6 }).map((_, i) => {
              const pointIndex = 12 + (i + 1);
              const isSelected = selectedPointIndex === pointIndex;
              const isHovered = hoveredPointIndex === pointIndex;
              const isValidMove = selectedPointIndex !== null && isMovePossible(selectedPointIndex, pointIndex);
              
              return (
                <Point 
                  key={pointIndex} 
                  index={pointIndex} 
                  isEven={i % 2 === 0}
                  pointWidth={POINT_WIDTH * scale}
                  isUp={false}
                  isSelected={isSelected}
                  isHovered={isHovered}
                  isValidMove={isValidMove}
                  onMouseEnter={() => setHoveredPointIndex(pointIndex)}
                  onMouseLeave={() => setHoveredPointIndex(null)}
                  onClick={() => handlePointClick(pointIndex)}
                >
                  {/* Render checkers */}
                  {board[pointIndex] && board[pointIndex].count > 0 && (
                    <Checker 
                      color={board[pointIndex].color}
                      count={board[pointIndex].count}
                      scale={scale}
                    />
                  )}
                </Point>
              );
            })}
          </div>
          
          {/* Bar for the center divide */}
          <div style={{ width: 30 }} />
          
          {/* Bottom right quadrant (points 19-24) */}
          <div className="flex flex-1">
            {Array.from({ length: 6 }).map((_, i) => {
              const pointIndex = 18 + (i + 1);
              const isSelected = selectedPointIndex === pointIndex;
              const isHovered = hoveredPointIndex === pointIndex;
              const isValidMove = selectedPointIndex !== null && isMovePossible(selectedPointIndex, pointIndex);
              
              return (
                <Point 
                  key={pointIndex} 
                  index={pointIndex} 
                  isEven={i % 2 === 0}
                  pointWidth={POINT_WIDTH * scale}
                  isUp={false}
                  isSelected={isSelected}
                  isHovered={isHovered}
                  isValidMove={isValidMove}
                  onMouseEnter={() => setHoveredPointIndex(pointIndex)}
                  onMouseLeave={() => setHoveredPointIndex(null)}
                  onClick={() => handlePointClick(pointIndex)}
                >
                  {/* Render checkers */}
                  {board[pointIndex] && board[pointIndex].count > 0 && (
                    <Checker 
                      color={board[pointIndex].color}
                      count={board[pointIndex].count}
                      scale={scale}
                    />
                  )}
                </Point>
              );
            })}
          </div>
        </div>
        
        {/* Points - Top row (1-12) */}
        <div 
          className="absolute"
          style={{
            top: BOARD_PADDING,
            left: BOARD_PADDING,
            right: BOARD_PADDING,
            height: (boardSize.height - (BOARD_PADDING * 2)) / 2,
            display: 'flex',
            flexDirection: 'row',
            zIndex: 2
          }}
        >
          {/* Top left quadrant (points 7-12) */}
          <div className="flex flex-1">
            {Array.from({ length: 6 }).map((_, i) => {
              const pointIndex = 12 - i;
              const isSelected = selectedPointIndex === pointIndex;
              const isHovered = hoveredPointIndex === pointIndex;
              const isValidMove = selectedPointIndex !== null && isMovePossible(selectedPointIndex, pointIndex);
              
              return (
                <Point 
                  key={pointIndex} 
                  index={pointIndex} 
                  isEven={i % 2 === 0}
                  pointWidth={POINT_WIDTH * scale}
                  isUp={true}
                  isSelected={isSelected}
                  isHovered={isHovered}
                  isValidMove={isValidMove}
                  onMouseEnter={() => setHoveredPointIndex(pointIndex)}
                  onMouseLeave={() => setHoveredPointIndex(null)}
                  onClick={() => handlePointClick(pointIndex)}
                >
                  {/* Render checkers */}
                  {board[pointIndex] && board[pointIndex].count > 0 && (
                    <Checker 
                      color={board[pointIndex].color}
                      count={board[pointIndex].count}
                      scale={scale}
                    />
                  )}
                </Point>
              );
            })}
          </div>
          
          {/* Bar for the center divide */}
          <div style={{ width: 30 }} />
          
          {/* Top right quadrant (points 1-6) */}
          <div className="flex flex-1">
            {Array.from({ length: 6 }).map((_, i) => {
              const pointIndex = 6 - i;
              const isSelected = selectedPointIndex === pointIndex;
              const isHovered = hoveredPointIndex === pointIndex;
              const isValidMove = selectedPointIndex !== null && isMovePossible(selectedPointIndex, pointIndex);
              
              return (
                <Point 
                  key={pointIndex} 
                  index={pointIndex} 
                  isEven={i % 2 === 0}
                  pointWidth={POINT_WIDTH * scale}
                  isUp={true}
                  isSelected={isSelected}
                  isHovered={isHovered}
                  isValidMove={isValidMove}
                  onMouseEnter={() => setHoveredPointIndex(pointIndex)}
                  onMouseLeave={() => setHoveredPointIndex(null)}
                  onClick={() => handlePointClick(pointIndex)}
                >
                  {/* Render checkers */}
                  {board[pointIndex] && board[pointIndex].count > 0 && (
                    <Checker 
                      color={board[pointIndex].color}
                      count={board[pointIndex].count}
                      scale={scale}
                    />
                  )}
                </Point>
              );
            })}
          </div>
        </div>
        
        {/* Bar for hit checkers */}
        <div 
          className="absolute"
          style={{
            top: BOARD_PADDING,
            left: '50%',
            transform: 'translateX(-50%)',
            width: '30px',
            height: boardSize.height - (BOARD_PADDING * 2),
            zIndex: 3,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          {/* Black bar (top half) */}
          <div 
            className="w-full h-1/2 flex flex-col items-center justify-start p-1 cursor-pointer"
            onClick={() => handleBarClick('black')}
          >
            {bar.black > 0 && (
              <Checker 
                color="black"
                count={bar.black}
                scale={scale * 0.8} // Smaller on the bar
              />
            )}
          </div>
          
          {/* White bar (bottom half) */}
          <div 
            className="w-full h-1/2 flex flex-col items-center justify-end p-1 cursor-pointer"
            onClick={() => handleBarClick('white')}
          >
            {bar.white > 0 && (
              <Checker 
                color="white"
                count={bar.white}
                scale={scale * 0.8} // Smaller on the bar
              />
            )}
          </div>
        </div>
        
        {/* Home/bearing off areas */}
        <div 
          className="absolute"
          style={{
            top: BOARD_PADDING,
            right: 10,
            width: '40px',
            height: boardSize.height - (BOARD_PADDING * 2),
            zIndex: 3,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          {/* Black home (top) */}
          <div 
            className="w-full h-1/2 flex flex-col items-center justify-start p-1"
            style={{ backgroundColor: 'rgba(0,0,0,0.1)', borderRadius: '4px' }}
            onClick={() => handlePointClick(-2)} // Special index for black home
          >
            {board[-2] && board[-2].count > 0 && (
              <div className="text-center text-sm font-semibold">
                {board[-2].count}
              </div>
            )}
          </div>
          
          {/* White home (bottom) */}
          <div 
            className="w-full h-1/2 flex flex-col items-center justify-end p-1"
            style={{ backgroundColor: 'rgba(255,255,255,0.1)', borderRadius: '4px' }}
            onClick={() => handlePointClick(25)} // Special index for white home
          >
            {board[25] && board[25].count > 0 && (
              <div className="text-center text-sm font-semibold">
                {board[25].count}
              </div>
            )}
          </div>
        </div>
        
        {/* Dice display at center of board */}
        {dice.length > 0 && (
          <div 
            className="absolute"
            style={{
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              zIndex: 10,
              display: 'flex',
              gap: '10px'
            }}
          >
            {dice.map((value, index) => (
              <Dice 
                key={index} 
                value={value} 
                used={index >= dice.length - movesPlayed.length}
                scale={scale}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Board;
