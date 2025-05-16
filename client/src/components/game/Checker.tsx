import React from "react";
import { COLORS } from "@/lib/backgammon/constants";

interface CheckerProps {
  color: "white" | "black";
  count: number;
  scale?: number;
}

const Checker: React.FC<CheckerProps> = ({ color, count, scale = 1 }) => {
  const size = 40 * scale;
  const stackLimit = 5; // Max checkers to show in a stack
  
  // Calculate the number of checkers to render in the stack
  const numToRender = Math.min(count, stackLimit);
  
  return (
    <div className="flex flex-col items-center">
      {/* If there are more checkers than the stack limit, show a count */}
      {count > stackLimit && (
        <div 
          className="absolute text-xs font-bold text-white z-20"
          style={{ 
            top: '50%', 
            left: '50%', 
            transform: 'translate(-50%, -50%)',
            textShadow: '0px 0px 2px black, 0px 0px 2px black',
            fontSize: `${12 * scale}px`
          }}
        >
          {count}
        </div>
      )}
      
      {/* Render the stack of checkers */}
      <div className="relative flex flex-col items-center">
        {Array.from({ length: numToRender }).map((_, i) => (
          <div
            key={i}
            style={{
              width: `${size}px`,
              height: `${size * 0.2}px`,
              borderRadius: '50%',
              backgroundColor: color === "white" ? COLORS.WHITE_CHECKER : COLORS.BLACK_CHECKER,
              border: `${1 * scale}px solid ${color === "white" ? "#ccc" : "#333"}`,
              boxShadow: `0 ${1 * scale}px ${3 * scale}px rgba(0,0,0,0.3)`,
              position: 'absolute',
              top: `${i * (size * 0.2) * 0.7}px`, // Stack with some overlap
              zIndex: 10 - i, // Higher checkers in the stack are visually on top
              transition: 'all 0.2s ease',
            }}
          />
        ))}
      </div>
    </div>
  );
};

export default Checker;
