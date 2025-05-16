export type PlayerColor = "white" | "black";
export type GameState = "initial" | "playing" | "ended";

// Represents a point on the board
export interface BoardPoint {
  color: PlayerColor;
  count: number;
}

// Represents a move from one point to another
export interface Move {
  from: number; // Point index or -1/24 for bar
  to: number;   // Point index or -2/25 for bearing off
}

// Colors for the board elements
export const COLORS = {
  // Board
  BOARD_BG: "#2D4739", // Dark green background
  POINT_LIGHT: "#D2AD70", // Light tan for even points
  POINT_DARK: "#8E6E48",  // Dark brown for odd points
  BAR_BG: "#1E3226",      // Darker green for the bar
  
  // Checkers
  WHITE_CHECKER: "#F8F8F8", // Off-white for white checkers
  BLACK_CHECKER: "#1A1A1A", // Near black for black checkers
  
  // Highlights
  SELECTED_POINT: "#4F9BE3", // Blue for selected point
  HOVER_POINT: "#73BFB8", // Teal for hovered point
  VALID_MOVE: "#8BC34A", // Green for valid moves
  
  // Dice
  DICE: "#FFFFFF", // White for unused dice
  DICE_USED: "#CCCCCC", // Grey for used dice
};

// Board dimensions
export const POINT_WIDTH = 60;
export const BOARD_PADDING = 30;

// Special point indices
export const WHITE_BAR = -1;
export const BLACK_BAR = 24;
export const WHITE_HOME = 25;
export const BLACK_HOME = -2;

// Initial checker positions for a new game
export const INITIAL_POSITIONS = {
  // White checker positions (count)
  0: { color: "white" as PlayerColor, count: 2 },   // Point 1
  5: { color: "white" as PlayerColor, count: 5 },   // Point 6
  7: { color: "white" as PlayerColor, count: 3 },   // Point 8
  11: { color: "white" as PlayerColor, count: 5 },  // Point 12
  
  // Black checker positions (count) 
  12: { color: "black" as PlayerColor, count: 5 },  // Point 13
  16: { color: "black" as PlayerColor, count: 3 },  // Point 17
  18: { color: "black" as PlayerColor, count: 5 },  // Point 19
  23: { color: "black" as PlayerColor, count: 2 },  // Point 24
  
  // Home positions for bearing off
  [WHITE_HOME]: { color: "white" as PlayerColor, count: 0 },
  [BLACK_HOME]: { color: "black" as PlayerColor, count: 0 },
};

// Total number of checkers per player
export const CHECKERS_PER_PLAYER = 15;
