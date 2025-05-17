# Java Backgammon

This is a Java implementation of the classic board game Backgammon, featuring a graphical user interface using Java Swing, AI opponent, and complete game mechanics.

## Project Structure

The project is organized following the Model-View-Controller (MVC) design pattern:

### Model
- `PlayerColor` - Enum representing player colors (WHITE, BLACK)
- `GameState` - Enum representing game states (INITIAL, PLAYING, ENDED)
- `Point` - Represents a point on the backgammon board
- `Board` - Represents the backgammon board with all points, home areas, and bar
- `Move` - Represents a move from one point to another
- `Dice` - Handles dice rolling and tracking used dice
- `GameLogic` - Contains the core game logic
- `MoveValidator` - Validates and generates possible moves
- `AI` - Provides AI player functionality

### View
- `GameView` - Interface for the game view
- `SwingGameView` - Swing implementation of the game view

### Controller
- `GameController` - Controls the game flow and handles user input

## Features

- Complete backgammon game mechanics
- Enhanced interactive graphical user interface
  - Triangular pips with correct orientation
  - Checkers rendered as full 3D-like circles
  - Valid moves highlighted with hollow circles
  - Clear point numbering with improved visibility
- Play against AI or another player
- Intelligent move validation with visual highlighting of possible moves
- Dice rolling with doubles support
- Checker movement including hitting opponent's blots
- Bar and bearing off functionality
- Undo move functionality
- Comprehensive game state tracking

## How to Play

1. Start a new game by selecting either "Play vs AI" or "2 Players"
2. Roll the dice to determine who goes first
3. Click on a point with your checkers to select it
4. Valid destination points will be highlighted
5. Click on a highlighted point to move a checker there
6. Use the "End Turn" button when you've made all possible moves
7. Use the "Undo Move" button if you want to take back a move

## Game Rules

- Roll dice and move checkers in the direction of your home board
- White moves from 24 to 1, Black moves from 1 to 24
- Landing on a single opponent checker sends it to the bar
- Checkers on the bar must re-enter before any other move
- When all your checkers are in your home board, you can bear them off

## Requirements

- Java 17 or higher
- Maven (for building)

## Building and Running

To compile and run the program:

```bash
# Compile
javac -d target/classes -cp src/main/java src/main/java/com/backgammon/BackgammonApp.java

# Run
java -cp target/classes com.backgammon.BackgammonApp
```

Or using Maven:

```bash
mvn clean package
java -jar target/java-backgammon-1.0-SNAPSHOT.jar
```