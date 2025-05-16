import React from "react";
import { COLORS } from "@/lib/backgammon/constants";

interface DiceProps {
  value: number;
  used?: boolean;
  scale?: number;
}

const Dice: React.FC<DiceProps> = ({ value, used = false, scale = 1 }) => {
  const size = 40 * scale;
  const dotSize = 6 * scale;
  
  // Dice dot positions based on value
  const getDotPositions = (diceValue: number) => {
    switch (diceValue) {
      case 1:
        return [{ top: "50%", left: "50%" }];
      case 2:
        return [
          { top: "25%", left: "25%" },
          { top: "75%", left: "75%" },
        ];
      case 3:
        return [
          { top: "25%", left: "25%" },
          { top: "50%", left: "50%" },
          { top: "75%", left: "75%" },
        ];
      case 4:
        return [
          { top: "25%", left: "25%" },
          { top: "25%", left: "75%" },
          { top: "75%", left: "25%" },
          { top: "75%", left: "75%" },
        ];
      case 5:
        return [
          { top: "25%", left: "25%" },
          { top: "25%", left: "75%" },
          { top: "50%", left: "50%" },
          { top: "75%", left: "25%" },
          { top: "75%", left: "75%" },
        ];
      case 6:
        return [
          { top: "25%", left: "25%" },
          { top: "25%", left: "50%" },
          { top: "25%", left: "75%" },
          { top: "75%", left: "25%" },
          { top: "75%", left: "50%" },
          { top: "75%", left: "75%" },
        ];
      default:
        return [];
    }
  };

  return (
    <div
      style={{
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor: used ? COLORS.DICE_USED : COLORS.DICE,
        borderRadius: `${4 * scale}px`,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        position: "relative",
        boxShadow: `0 ${2 * scale}px ${4 * scale}px rgba(0,0,0,0.2)`,
        opacity: used ? 0.6 : 1,
        transition: "all 0.3s ease",
      }}
    >
      {getDotPositions(value).map((position, index) => (
        <div
          key={index}
          style={{
            position: "absolute",
            width: `${dotSize}px`,
            height: `${dotSize}px`,
            backgroundColor: used ? "#888" : "#333",
            borderRadius: "50%",
            top: position.top,
            left: position.left,
            transform: "translate(-50%, -50%)",
          }}
        />
      ))}
    </div>
  );
};

export default Dice;
