import React from "react";
import { COLORS } from "@/lib/backgammon/constants";

interface PointProps {
  index: number;
  isEven: boolean;
  pointWidth: number;
  isUp: boolean;
  isSelected?: boolean;
  isHovered?: boolean;
  isValidMove?: boolean;
  onMouseEnter?: () => void;
  onMouseLeave?: () => void;
  onClick?: () => void;
  children?: React.ReactNode;
}

const Point: React.FC<PointProps> = ({
  index,
  isEven,
  pointWidth,
  isUp,
  isSelected = false,
  isHovered = false,
  isValidMove = false,
  onMouseEnter,
  onMouseLeave,
  onClick,
  children,
}) => {
  const pointHeight = 200;
  const color = isEven ? COLORS.POINT_DARK : COLORS.POINT_LIGHT;
  
  // Different highlight colors based on point state
  let backgroundColor = color;
  if (isSelected) {
    backgroundColor = COLORS.SELECTED_POINT;
  } else if (isValidMove) {
    backgroundColor = COLORS.VALID_MOVE;
  } else if (isHovered) {
    backgroundColor = COLORS.HOVER_POINT;
  }
  
  return (
    <div
      className="relative flex items-center justify-center cursor-pointer"
      style={{
        width: pointWidth,
        height: pointHeight,
      }}
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      onClick={onClick}
    >
      {/* Triangle point */}
      <div
        style={{
          width: '90%',
          height: '80%',
          backgroundColor,
          clipPath: isUp 
            ? 'polygon(50% 0%, 100% 100%, 0% 100%)' 
            : 'polygon(50% 100%, 0% 0%, 100% 0%)',
          transition: 'background-color 0.2s ease',
        }}
      />
      
      {/* Point number (small, at bottom or top based on orientation) */}
      <div
        className="absolute text-xs font-medium opacity-60"
        style={{
          [isUp ? 'bottom' : 'top']: '2px',
          left: '50%',
          transform: 'translateX(-50%)',
        }}
      >
        {index}
      </div>
      
      {/* Container for checkers, positioned at bottom or top based on orientation */}
      <div
        className="absolute"
        style={{
          [isUp ? 'top' : 'bottom']: '0',
          left: '50%',
          transform: 'translateX(-50%)',
          width: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          [isUp ? 'justifyContent' : 'justifyContent']: 'flex-start',
        }}
      >
        {children}
      </div>
    </div>
  );
};

export default Point;
