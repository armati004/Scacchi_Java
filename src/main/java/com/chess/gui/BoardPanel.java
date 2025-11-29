package com.chess.gui;

import com.chess.model.Board;
import com.chess.model.Game;
import com.chess.model.Pawn;
import com.chess.model.Piece;
import com.chess.model.PieceType;
import com.chess.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays the chess board.
 */
public class BoardPanel extends JPanel {
    private static final int SQUARE_SIZE = 80;
    private static final java.awt.Color LIGHT_SQUARE = new java.awt.Color(240, 217, 181);
    private static final java.awt.Color DARK_SQUARE = new java.awt.Color(181, 136, 99);
    private static final java.awt.Color SELECTED_COLOR = new java.awt.Color(106, 168, 79, 150);
    private static final java.awt.Color VALID_MOVE_COLOR = new java.awt.Color(106, 168, 79, 100);
    private static final java.awt.Color LAST_MOVE_COLOR = new java.awt.Color(255, 255, 0, 100);
    private static final java.awt.Color CHECK_COLOR = new java.awt.Color(255, 0, 0, 100);

    private final Game game;
    private Position selectedPosition;
    private List<Position> validMoves;
    private Position lastMoveFrom;
    private Position lastMoveTo;
    private GameStatusListener statusListener;

    private static final Map<String, String> PIECE_SYMBOLS = new HashMap<>();
    
    static {
        // Unicode chess symbols
        PIECE_SYMBOLS.put("WK", "\u2654");
        PIECE_SYMBOLS.put("WQ", "\u2655");
        PIECE_SYMBOLS.put("WR", "\u2656");
        PIECE_SYMBOLS.put("WB", "\u2657");
        PIECE_SYMBOLS.put("WN", "\u2658");
        PIECE_SYMBOLS.put("WP", "\u2659");
        PIECE_SYMBOLS.put("BK", "\u265A");
        PIECE_SYMBOLS.put("BQ", "\u265B");
        PIECE_SYMBOLS.put("BR", "\u265C");
        PIECE_SYMBOLS.put("BB", "\u265D");
        PIECE_SYMBOLS.put("BN", "\u265E");
        PIECE_SYMBOLS.put("BP", "\u265F");
    }

    public interface GameStatusListener {
        void onStatusChanged(String status);
        void onGameOver(String result);
    }

    public BoardPanel(Game game) {
        this.game = game;
        this.selectedPosition = null;
        this.validMoves = null;

        setPreferredSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    public void setStatusListener(GameStatusListener listener) {
        this.statusListener = listener;
    }

    private void handleClick(int x, int y) {
        int col = x / SQUARE_SIZE;
        int row = y / SQUARE_SIZE;
        Position clickedPos = new Position(row, col);

        if (!clickedPos.isValid()) return;

        if (selectedPosition == null) {
            // Select a piece
            Piece piece = game.getBoard().getPieceAt(clickedPos);
            if (piece != null && piece.getColor() == game.getCurrentTurn()) {
                selectedPosition = clickedPos;
                validMoves = game.getBoard().getLegalMoves(clickedPos);
                repaint();
            }
        } else {
            // Try to move to the clicked position
            if (validMoves != null && validMoves.contains(clickedPos)) {
                // Check for pawn promotion
                Piece piece = game.getBoard().getPieceAt(selectedPosition);
                PieceType promotionType = null;
                
                if (piece instanceof Pawn) {
                    int targetRow = (piece.getColor() == com.chess.model.Color.WHITE) ? 0 : 7;
                    if (clickedPos.getRow() == targetRow) {
                        promotionType = showPromotionDialog();
                    }
                }

                lastMoveFrom = selectedPosition;
                lastMoveTo = clickedPos;
                
                game.makeMove(selectedPosition, clickedPos, promotionType);
                
                notifyStatusChange();
            }
            
            // Clear selection
            selectedPosition = null;
            validMoves = null;
            repaint();
        }
    }

    private PieceType showPromotionDialog() {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Choose piece for promotion:",
            "Pawn Promotion",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        switch (choice) {
            case 1: return PieceType.ROOK;
            case 2: return PieceType.BISHOP;
            case 3: return PieceType.KNIGHT;
            default: return PieceType.QUEEN;
        }
    }

    private void notifyStatusChange() {
        if (statusListener != null) {
            Game.GameState state = game.getState();
            statusListener.onStatusChanged(game.getStateMessage());
            
            if (state == Game.GameState.CHECKMATE || 
                state == Game.GameState.STALEMATE || 
                state == Game.GameState.DRAW) {
                statusListener.onGameOver(game.getStateMessage());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Board board = game.getBoard();
        Position kingInCheck = null;
        
        if (game.getState() == Game.GameState.CHECK || game.getState() == Game.GameState.CHECKMATE) {
            kingInCheck = board.findKing(game.getCurrentTurn());
        }

        // Draw squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = col * SQUARE_SIZE;
                int y = row * SQUARE_SIZE;
                Position pos = new Position(row, col);

                // Base color
                boolean isLight = (row + col) % 2 == 0;
                g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);

                // Highlight last move
                if ((lastMoveFrom != null && pos.equals(lastMoveFrom)) ||
                    (lastMoveTo != null && pos.equals(lastMoveTo))) {
                    g2d.setColor(LAST_MOVE_COLOR);
                    g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Highlight king in check
                if (kingInCheck != null && pos.equals(kingInCheck)) {
                    g2d.setColor(CHECK_COLOR);
                    g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Highlight selected square
                if (selectedPosition != null && pos.equals(selectedPosition)) {
                    g2d.setColor(SELECTED_COLOR);
                    g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }

                // Highlight valid moves
                if (validMoves != null && validMoves.contains(pos)) {
                    g2d.setColor(VALID_MOVE_COLOR);
                    if (board.getPieceAt(pos) != null) {
                        // Capture indicator - draw a ring
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawOval(x + 5, y + 5, SQUARE_SIZE - 10, SQUARE_SIZE - 10);
                    } else {
                        // Empty square - draw a dot
                        int dotSize = 20;
                        g2d.fillOval(x + (SQUARE_SIZE - dotSize) / 2, 
                                    y + (SQUARE_SIZE - dotSize) / 2, 
                                    dotSize, dotSize);
                    }
                }

                // Draw piece
                Piece piece = board.getPieceAt(pos);
                if (piece != null) {
                    drawPiece(g2d, piece, x, y);
                }

                // Draw coordinate labels
                g2d.setColor(isLight ? DARK_SQUARE : LIGHT_SQUARE);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
                if (col == 0) {
                    g2d.drawString(String.valueOf(8 - row), x + 2, y + 14);
                }
                if (row == 7) {
                    g2d.drawString(String.valueOf((char)('a' + col)), x + SQUARE_SIZE - 12, y + SQUARE_SIZE - 4);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2d, Piece piece, int x, int y) {
        String key = (piece.getColor() == com.chess.model.Color.WHITE ? "W" : "B") + piece.getType().getSymbol();
        String symbol = PIECE_SYMBOLS.get(key);
        
        if (symbol != null) {
            g2d.setFont(new Font("Serif", Font.PLAIN, 60));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (SQUARE_SIZE - fm.stringWidth(symbol)) / 2;
            int textY = y + (SQUARE_SIZE + fm.getAscent() - fm.getDescent()) / 2 - 5;
            
            // Draw shadow for better visibility
            g2d.setColor(new java.awt.Color(0, 0, 0, 50));
            g2d.drawString(symbol, textX + 2, textY + 2);
            
            // Draw piece
            g2d.setColor(piece.getColor() == com.chess.model.Color.WHITE ? java.awt.Color.WHITE : java.awt.Color.BLACK);
            g2d.drawString(symbol, textX, textY);
        }
    }

    public void resetBoard() {
        game.reset();
        selectedPosition = null;
        validMoves = null;
        lastMoveFrom = null;
        lastMoveTo = null;
        repaint();
        notifyStatusChange();
    }
}
