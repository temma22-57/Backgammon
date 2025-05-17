package com.backgammon.view;

import com.backgammon.controller.GameController;
import com.backgammon.model.Board;
import com.backgammon.model.GameState;
import com.backgammon.model.PlayerColor;
import com.backgammon.model.Point;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing implementation of the game view
 */
public class SwingGameView extends JFrame implements GameView {
    // UI constants
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    private static final int BOARD_WIDTH = 700;
    private static final int BOARD_HEIGHT = 500;
    private static final int POINT_WIDTH = 50;
    private static final int CHECKER_SIZE = 40;
    
    // Colors
    private static final Color BOARD_COLOR = new Color(45, 71, 57); // Dark green
    private static final Color BAR_COLOR = new Color(30, 50, 38);   // Darker green
    private static final Color LIGHT_POINT_COLOR = new Color(210, 173, 112); // Light tan
    private static final Color DARK_POINT_COLOR = new Color(142, 110, 72);   // Dark brown
    private static final Color WHITE_CHECKER_COLOR = new Color(248, 248, 248); // Off-white
    private static final Color BLACK_CHECKER_COLOR = new Color(26, 26, 26);    // Near black
    private static final Color SELECTED_COLOR = new Color(79, 155, 227);  // Blue
    private static final Color HIGHLIGHT_COLOR = new Color(139, 195, 74); // Green
    
    // Game controller
    private GameController controller;
    
    // UI components
    private BoardPanel boardPanel;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private JButton rollButton;
    private JButton endTurnButton;
    private JPanel dicePanel;
    
    // Game state
    private List<Integer> diceValues;
    private List<Boolean> diceUsed;
    private int selectedPoint;
    private List<Integer> validMoves;
    
    /**
     * Create a new Swing game view
     */
    public SwingGameView() {
        diceValues = new ArrayList<>();
        diceUsed = new ArrayList<>();
        selectedPoint = -999; // Invalid point, meaning no selection
        validMoves = new ArrayList<>();
        
        initializeUI();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setTitle("Backgammon");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create the board panel
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);
        
        // Create the control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Status label
        statusLabel = new JLabel("Welcome to Backgammon");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Dice panel
        dicePanel = new JPanel();
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(dicePanel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Control buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        
        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(e -> controller.rollDice());
        buttonPanel.add(rollButton);
        
        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(e -> controller.endTurn());
        buttonPanel.add(endTurnButton);
        
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> showNewGameDialog());
        buttonPanel.add(newGameButton);
        
        controlPanel.add(buttonPanel);
        
        // Rules info
        JTextArea rulesText = new JTextArea(
            "Game Rules:\n" +
            "- Roll dice and move checkers in the direction of your home board\n" +
            "- White moves from 24 to 1, Black moves from 1 to 24\n" +
            "- Landing on a single opponent checker sends it to the bar\n" +
            "- Checkers on the bar must re-enter before any other move\n" +
            "- When all your checkers are in your home board, you can bear them off"
        );
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setOpaque(false);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 12));
        
        controlPanel.add(Box.createVerticalStrut(20));
        controlPanel.add(rulesText);
        
        add(controlPanel, BorderLayout.EAST);
        
        // Initialize button states
        updateButtonStates(GameState.INITIAL, PlayerColor.WHITE);
        
        // Show the window
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Initialize the game controller
     */
    public void initializeController() {
        this.controller = new GameController(this);
        showNewGameDialog();
    }
    
    /**
     * Show the dialog to start a new game
     */
    private void showNewGameDialog() {
        Object[] options = {"Play vs AI", "2 Players", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Select game mode:",
            "New Game",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 0) {
            // AI mode
            controller.startNewGame(true);
        } else if (choice == 1) {
            // 2 players mode
            controller.startNewGame(false);
        }
    }
    
    /**
     * Update the button states based on game state
     * @param gameState the current game state
     * @param currentPlayer the current player
     */
    private void updateButtonStates(GameState gameState, PlayerColor currentPlayer) {
        if (gameState == GameState.PLAYING) {
            boolean hasRolled = !diceValues.isEmpty();
            rollButton.setEnabled(!hasRolled);
            endTurnButton.setEnabled(hasRolled);
        } else {
            rollButton.setEnabled(false);
            endTurnButton.setEnabled(false);
        }
    }
    
    @Override
    public void updateBoard(Board board) {
        boardPanel.updateBoard(board);
        boardPanel.repaint();
    }
    
    @Override
    public void updateGameState(GameState gameState, PlayerColor currentPlayer, PlayerColor winner) {
        if (gameState == GameState.ENDED) {
            statusLabel.setText("Game Over! " + winner + " wins!");
        } else {
            statusLabel.setText(currentPlayer + "'s turn");
        }
        
        updateButtonStates(gameState, currentPlayer);
    }
    
    @Override
    public void updateDice(List<Integer> diceValues, List<Boolean> used) {
        this.diceValues = new ArrayList<>(diceValues);
        this.diceUsed = new ArrayList<>(used);
        
        // Update the dice display
        dicePanel.removeAll();
        for (int i = 0; i < diceValues.size(); i++) {
            DiceLabel diceLabel = new DiceLabel(diceValues.get(i), used.get(i));
            dicePanel.add(diceLabel);
        }
        dicePanel.revalidate();
        dicePanel.repaint();
    }
    
    @Override
    public void highlightPoints(int pointIndex, List<Integer> validDestinations) {
        this.selectedPoint = pointIndex;
        this.validMoves = new ArrayList<>(validDestinations);
        boardPanel.repaint();
    }
    
    @Override
    public void clearHighlights() {
        this.selectedPoint = -999;
        this.validMoves.clear();
        boardPanel.repaint();
    }
    
    @Override
    public void playMoveSound() {
        try {
            URL soundURL = getClass().getResource("/sounds/move.wav");
            if (soundURL != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Panel that displays the backgammon board
     */
    private class BoardPanel extends JPanel {
        private Board board;
        
        public BoardPanel() {
            setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
            setBackground(BOARD_COLOR);
            
            // Add mouse listener for selecting points
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleClick(e.getX(), e.getY());
                }
            });
        }
        
        /**
         * Update the board data
         * @param board the current board state
         */
        public void updateBoard(Board board) {
            this.board = board;
        }
        
        /**
         * Handle a mouse click on the board
         * @param x the x coordinate
         * @param y the y coordinate
         */
        private void handleClick(int x, int y) {
            // Check if the bar was clicked
            int barCenterX = getWidth() / 2;
            if (x >= barCenterX - 15 && x <= barCenterX + 15) {
                // Bar clicked - determine if it's the top or bottom half
                if (y < getHeight() / 2) {
                    controller.handleBarClick(PlayerColor.BLACK);
                } else {
                    controller.handleBarClick(PlayerColor.WHITE);
                }
                return;
            }
            
            // Check if a point was clicked
            int pointIndex = getPointIndexFromCoordinates(x, y);
            if (pointIndex >= 0 && pointIndex < 24) {
                controller.handlePointClick(pointIndex);
            }
        }
        
        /**
         * Convert screen coordinates to a point index
         * @param x the x coordinate
         * @param y the y coordinate
         * @return the point index, or -1 if no point was clicked
         */
        private int getPointIndexFromCoordinates(int x, int y) {
            int boardWidth = getWidth();
            int boardHeight = getHeight();
            int pointWidth = boardWidth / 14; // 12 points + 2 for the bar
            
            // Determine quadrant
            boolean isTopHalf = y < boardHeight / 2;
            boolean isLeftHalf = x < boardWidth / 2;
            
            // Calculate point index within quadrant
            int quadrantX = isLeftHalf ? x : x - boardWidth / 2 - 30; // Adjust for the bar
            int pointInQuadrant = quadrantX / pointWidth;
            
            // Apply bounds checking
            if (pointInQuadrant < 0) pointInQuadrant = 0;
            if (pointInQuadrant >= 6) pointInQuadrant = 5; // Cap at 5 (6 points per quadrant)
            
            // UPDATED: Convert to global point index based on our corrected point layout
            int pointIndex;
            if (isTopHalf) {
                if (isLeftHalf) {
                    // Top left quadrant (points 13-18)
                    pointIndex = 12 + pointInQuadrant;
                } else {
                    // Top right quadrant (points 19-24)
                    pointIndex = 18 + pointInQuadrant;
                }
            } else {
                if (isLeftHalf) {
                    // Bottom left quadrant (points 7-12)
                    pointIndex = 11 - pointInQuadrant;
                } else {
                    // Bottom right quadrant (points 1-6)
                    pointIndex = 5 - pointInQuadrant;
                }
            }
            
            // Ensure index is valid
            if (pointIndex < 0) pointIndex = 0;
            if (pointIndex > 23) pointIndex = 23;
            
            return pointIndex;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable anti-aliasing for smoother graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Draw the bar
            g2d.setColor(BAR_COLOR);
            g2d.fillRect(width / 2 - 15, 0, 30, height);
            
            // Draw the points
            drawPoints(g2d);
            
            // Draw the home areas
            drawHomeAreas(g2d);
            
            // Draw the checkers
            if (board != null) {
                drawCheckers(g2d);
            }
        }
        
        /**
         * Draw the triangular points on the board
         * @param g2d the graphics context
         */
        private void drawPoints(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            int pointWidth = width / 14; // 12 points + 2 for the bar
            int pointHeight = height / 3;
            
            // Draw the points
            for (int i = 0; i < 24; i++) {
                boolean isEven = i % 2 == 0;
                boolean isTopHalf = i < 12;
                
                // Calculate the point's position
                int x, y;
                if (i < 6) {
                    // Points 1-6 (bottom right)
                    x = width / 2 + 15 + (5 - i) * pointWidth;
                    y = height - pointHeight;
                } else if (i < 12) {
                    // Points 7-12 (bottom left)
                    x = width / 2 - 15 - (i - 6) * pointWidth;
                    y = height - pointHeight;
                } else if (i < 18) {
                    // Points 13-18 (top left)
                    x = width / 2 - 15 - (i - 12) * pointWidth;
                    y = 0;
                } else {
                    // Points 19-24 (top right)
                    x = width / 2 + 15 + (i - 18) * pointWidth;
                    y = 0;
                }
                
                // Set color based on even/odd and whether the point is selected or a valid move
                if (i == selectedPoint) {
                    g2d.setColor(SELECTED_COLOR);
                } else if (validMoves.contains(i)) {
                    g2d.setColor(HIGHLIGHT_COLOR);
                } else {
                    g2d.setColor(isEven ? LIGHT_POINT_COLOR : DARK_POINT_COLOR);
                }
                
                // Draw the triangle - FIXED: corrected orientation
                int[] xPoints = {x, x + pointWidth, x + pointWidth / 2};
                int[] yPoints;
                if (isTopHalf) {
                    // Top half - triangles point DOWN (corrected)
                    yPoints = new int[]{y, y, y + pointHeight};
                } else {
                    // Bottom half - triangles point UP (corrected)
                    yPoints = new int[]{y + pointHeight, y + pointHeight, y};
                }
                
                g2d.fillPolygon(xPoints, yPoints, 3);
                
                // Draw valid move indicator (hollow circle) if this is a valid move
                if (validMoves.contains(i)) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2));
                    int circleX = x + pointWidth / 2 - 15;
                    int circleY = isTopHalf ? y + pointHeight / 2 - 15 : y + pointHeight / 2 - 15;
                    g2d.drawOval(circleX, circleY, 30, 30);
                }
                
                // Draw the point number with better visibility
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String pointNumber = String.valueOf(i + 1);
                int textX = x + pointWidth / 2 - g2d.getFontMetrics().stringWidth(pointNumber) / 2;
                int textY = isTopHalf ? y + pointHeight + 15 : y - 5;
                
                // Add a dark background behind the text for better readability
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(pointNumber);
                int textHeight = fm.getHeight();
                g2d.setColor(new Color(0, 0, 0, 128)); // Semi-transparent black
                g2d.fillRect(textX - 2, textY - textHeight + 4, textWidth + 4, textHeight);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(pointNumber, textX, textY);
            }
        }
        
        /**
         * Draw the home areas (off-board areas for bearing off)
         * @param g2d the graphics context
         */
        private void drawHomeAreas(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            
            // Draw the white home (right side)
            g2d.setColor(new Color(255, 255, 255, 50)); // Semi-transparent white
            g2d.fillRect(width - 30, height / 2, 25, height / 2 - 5);
            
            // Draw the black home (left side)
            g2d.setColor(new Color(0, 0, 0, 50)); // Semi-transparent black
            g2d.fillRect(5, 5, 25, height / 2 - 5);
            
            // Label the home areas
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("HOME", width - 30, height - 5);
            g2d.drawString("HOME", 5, 20);
        }
        
        /**
         * Draw the checkers on the board
         * @param g2d the graphics context
         */
        private void drawCheckers(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            int pointWidth = width / 14;
            
            // Draw the checkers on the points
            for (int i = 0; i < 24; i++) {
                Point point = board.getPoint(i);
                if (point != null && !point.isEmpty()) {
                    // Calculate the base position for checkers on this point
                    int x, y;
                    if (i < 6) {
                        // Points 1-6 (bottom right)
                        x = width / 2 + 15 + (5 - i) * pointWidth + pointWidth / 2;
                        y = height - 30;
                    } else if (i < 12) {
                        // Points 7-12 (bottom left)
                        x = width / 2 - 15 - (i - 5) * pointWidth - pointWidth / 2;
                        y = height - 30;
                    } else if (i < 18) {
                        // Points 13-18 (top left)
                        x = width / 2 - 15 - (17 - i) * pointWidth - pointWidth / 2;
                        y = 30;
                    } else {
                        // Points 19-24 (top right)
                        x = width / 2 + 15 + (i - 18) * pointWidth + pointWidth / 2;
                        y = 30;
                    }
                    
                    drawCheckersStack(g2d, x, y, point.getColor(), point.getCount(), i < 12);
                }
            }
            
            // Draw the checkers on the bar
            int barCenterX = width / 2;
            
            // White checkers on the bar
            int whiteBarCount = board.getBarCount(PlayerColor.WHITE);
            if (whiteBarCount > 0) {
                drawCheckersStack(g2d, barCenterX, height - 30, PlayerColor.WHITE, whiteBarCount, false);
            }
            
            // Black checkers on the bar
            int blackBarCount = board.getBarCount(PlayerColor.BLACK);
            if (blackBarCount > 0) {
                drawCheckersStack(g2d, barCenterX, 30, PlayerColor.BLACK, blackBarCount, true);
            }
            
            // Draw the home counts
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            
            // White home count
            int whiteHomeCount = board.getHomeCount(PlayerColor.WHITE);
            if (whiteHomeCount > 0) {
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(whiteHomeCount), width - 20, height - 30);
            }
            
            // Black home count
            int blackHomeCount = board.getHomeCount(PlayerColor.BLACK);
            if (blackHomeCount > 0) {
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(blackHomeCount), 15, 30);
            }
        }
        
        /**
         * Draw a stack of checkers
         * @param g2d the graphics context
         * @param x the x coordinate of the center of the stack
         * @param y the y coordinate of the base of the stack
         * @param color the color of the checkers
         * @param count the number of checkers
         * @param isTopHalf whether the stack is in the top half of the board
         */
        private void drawCheckersStack(Graphics2D g2d, int x, int y, PlayerColor color, int count, boolean isTopHalf) {
            int stackLimit = 5; // Max checkers to show in a stack
            int checkerSize = CHECKER_SIZE;
            int checkerHeight = checkerSize / 3;
            
            // Draw the stack
            int numToRender = Math.min(count, stackLimit);
            for (int i = 0; i < numToRender; i++) {
                int offsetY = isTopHalf ? i * checkerHeight : -i * checkerHeight;
                
                // UPDATED: Draw the checker as a full circle
                g2d.setColor(color == PlayerColor.WHITE ? WHITE_CHECKER_COLOR : BLACK_CHECKER_COLOR);
                
                // Fill the circle
                g2d.fillOval(x - checkerSize / 2, y + offsetY - checkerSize / 2, checkerSize, checkerSize);
                
                // Add a 3D effect with a gradient
                if (color == PlayerColor.WHITE) {
                    // For white checkers
                    g2d.setPaint(new GradientPaint(
                        x - checkerSize / 2, y + offsetY - checkerSize / 2,
                        new Color(255, 255, 255),
                        x + checkerSize / 2, y + offsetY + checkerSize / 2,
                        new Color(220, 220, 220)
                    ));
                } else {
                    // For black checkers
                    g2d.setPaint(new GradientPaint(
                        x - checkerSize / 2, y + offsetY - checkerSize / 2,
                        new Color(50, 50, 50),
                        x + checkerSize / 2, y + offsetY + checkerSize / 2,
                        new Color(10, 10, 10)
                    ));
                }
                
                g2d.fillOval(x - checkerSize / 2, y + offsetY - checkerSize / 2, checkerSize, checkerSize);
                
                // Draw a border for better definition
                g2d.setColor(color == PlayerColor.WHITE ? Color.LIGHT_GRAY : Color.BLACK);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x - checkerSize / 2, y + offsetY - checkerSize / 2, checkerSize, checkerSize);
            }
            
            // If there are more checkers than the stack limit, show a count
            if (count > stackLimit) {
                // Add a contrasting background for the count text
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillOval(x - 15, y - 12, 30, 24);
                
                // Draw the count
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String countText = String.valueOf(count);
                int textWidth = g2d.getFontMetrics().stringWidth(countText);
                g2d.drawString(countText, x - textWidth / 2, y + 5);
            }
        }
    }
    
    /**
     * Label for displaying a die
     */
    private class DiceLabel extends JLabel {
        private final int value;
        private final boolean used;
        
        public DiceLabel(int value, boolean used) {
            this.value = value;
            this.used = used;
            setPreferredSize(new Dimension(50, 50));
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the die
            g2d.setColor(used ? Color.LIGHT_GRAY : Color.WHITE);
            g2d.fillRoundRect(5, 5, 40, 40, 8, 8);
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(5, 5, 40, 40, 8, 8);
            
            // Draw the dots
            g2d.setColor(Color.BLACK);
            switch (value) {
                case 1:
                    drawDot(g2d, 25, 25);
                    break;
                case 2:
                    drawDot(g2d, 15, 15);
                    drawDot(g2d, 35, 35);
                    break;
                case 3:
                    drawDot(g2d, 15, 15);
                    drawDot(g2d, 25, 25);
                    drawDot(g2d, 35, 35);
                    break;
                case 4:
                    drawDot(g2d, 15, 15);
                    drawDot(g2d, 35, 15);
                    drawDot(g2d, 15, 35);
                    drawDot(g2d, 35, 35);
                    break;
                case 5:
                    drawDot(g2d, 15, 15);
                    drawDot(g2d, 35, 15);
                    drawDot(g2d, 25, 25);
                    drawDot(g2d, 15, 35);
                    drawDot(g2d, 35, 35);
                    break;
                case 6:
                    drawDot(g2d, 15, 15);
                    drawDot(g2d, 15, 25);
                    drawDot(g2d, 15, 35);
                    drawDot(g2d, 35, 15);
                    drawDot(g2d, 35, 25);
                    drawDot(g2d, 35, 35);
                    break;
            }
            
            // Draw "Used" indicator if the die is used
            if (used) {
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(5, 5, 40, 40, 8, 8);
            }
        }
        
        private void drawDot(Graphics2D g2d, int x, int y) {
            g2d.fillOval(x - 3, y - 3, 6, 6);
        }
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SwingGameView view = new SwingGameView();
            view.initializeController();
        });
    }
}