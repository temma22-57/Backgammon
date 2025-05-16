import { PlayerColor } from "./constants";

/**
 * Represents a point on the backgammon board
 */
export class Point {
  // The index of the point on the board (1-24)
  readonly index: number;
  
  // The color of the checkers on this point, if any
  private _color: PlayerColor | null = null;
  
  // The number of checkers on this point
  private _count: number = 0;
  
  constructor(index: number) {
    this.index = index;
  }
  
  /**
   * Get the color of the checkers on this point
   */
  get color(): PlayerColor | null {
    return this._color;
  }
  
  /**
   * Get the number of checkers on this point
   */
  get count(): number {
    return this._count;
  }
  
  /**
   * Add a checker to this point
   */
  addChecker(color: PlayerColor): void {
    // If the point is empty, set the color
    if (this._count === 0) {
      this._color = color;
    } else if (this._color !== color) {
      // Can't add a checker of a different color unless the point is empty
      // or has only one checker (hitting)
      if (this._count > 1) {
        throw new Error(`Cannot add ${color} checker to point ${this.index} with ${this._count} ${this._color} checkers`);
      }
      
      // Hitting a single checker - the opponent's checker is removed and sent to the bar
      this._color = color;
      this._count = 0; // Reset count since the opponent's checker is removed
    }
    
    this._count++;
  }
  
  /**
   * Remove a checker from this point
   */
  removeChecker(): PlayerColor {
    if (this._count === 0 || this._color === null) {
      throw new Error(`Cannot remove checker from empty point ${this.index}`);
    }
    
    this._count--;
    
    const removedColor = this._color;
    
    // If the point is now empty, reset the color
    if (this._count === 0) {
      this._color = null;
    }
    
    return removedColor;
  }
  
  /**
   * Clear all checkers from this point
   */
  clear(): void {
    this._count = 0;
    this._color = null;
  }
  
  /**
   * Check if this point is a blot (has only one checker)
   */
  isBlot(): boolean {
    return this._count === 1;
  }
  
  /**
   * Check if this point is made (has two or more checkers)
   */
  isMade(): boolean {
    return this._count >= 2;
  }
  
  /**
   * Check if this point is empty
   */
  isEmpty(): boolean {
    return this._count === 0;
  }
  
  /**
   * Check if this point has checkers of the given color
   */
  hasColor(color: PlayerColor): boolean {
    return this._color === color && this._count > 0;
  }
  
  /**
   * Clone this point
   */
  clone(): Point {
    const newPoint = new Point(this.index);
    newPoint._color = this._color;
    newPoint._count = this._count;
    return newPoint;
  }
}
