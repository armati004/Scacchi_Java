package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chess board.
 */
public class Board {
    private final Piece[][] squares;
    private Position enPassantTarget;

    public Board() {
        squares = new Piece[8][8];
    }

    public void initializeStandardPosition() {
        // Clear the board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }

        // Place black pieces (row 0)
        squares[0][0] = new Rook(Color.BLACK);
        squares[0][1] = new Knight(Color.BLACK);
        squares[0][2] = new Bishop(Color.BLACK);
        squares[0][3] = new Queen(Color.BLACK);
        squares[0][4] = new King(Color.BLACK);
        squares[0][5] = new Bishop(Color.BLACK);
        squares[0][6] = new Knight(Color.BLACK);
        squares[0][7] = new Rook(Color.BLACK);

        // Place black pawns (row 1)
        for (int col = 0; col < 8; col++) {
            squares[1][col] = new Pawn(Color.BLACK);
        }

        // Place white pawns (row 6)
        for (int col = 0; col < 8; col++) {
            squares[6][col] = new Pawn(Color.WHITE);
        }

        // Place white pieces (row 7)
        squares[7][0] = new Rook(Color.WHITE);
        squares[7][1] = new Knight(Color.WHITE);
        squares[7][2] = new Bishop(Color.WHITE);
        squares[7][3] = new Queen(Color.WHITE);
        squares[7][4] = new King(Color.WHITE);
        squares[7][5] = new Bishop(Color.WHITE);
        squares[7][6] = new Knight(Color.WHITE);
        squares[7][7] = new Rook(Color.WHITE);

        enPassantTarget = null;
    }

    public Piece getPieceAt(Position pos) {
        if (!pos.isValid()) return null;
        return squares[pos.getRow()][pos.getCol()];
    }

    public void setPieceAt(Position pos, Piece piece) {
        if (pos.isValid()) {
            squares[pos.getRow()][pos.getCol()] = piece;
        }
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position target) {
        this.enPassantTarget = target;
    }

    /**
     * Checks if a piece of the given color can move to the target position.
     * Returns true if the target is empty or contains an opponent's piece.
     */
    public boolean canMoveTo(Position from, Position to, Color color) {
        Piece targetPiece = getPieceAt(to);
        return targetPiece == null || targetPiece.getColor() != color;
    }

    /**
     * Finds the position of the king of the given color.
     */
    public Position findKing(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the king of the given color is in check.
     */
    public boolean isInCheck(Color color) {
        Position kingPos = findKing(color);
        if (kingPos == null) return false;

        return isSquareAttacked(kingPos, color.opposite());
    }

    /**
     * Checks if a square is attacked by any piece of the given color.
     */
    public boolean isSquareAttacked(Position pos, Color attackerColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && piece.getColor() == attackerColor) {
                    Position from = new Position(row, col);
                    // For King, we need to check basic attacks to avoid infinite recursion
                    if (piece instanceof King) {
                        int rowDiff = Math.abs(pos.getRow() - row);
                        int colDiff = Math.abs(pos.getCol() - col);
                        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0)) {
                            return true;
                        }
                    } else {
                        List<Position> moves = piece.getValidMoves(from, this);
                        if (moves.contains(pos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if moving a piece would leave the king in check.
     */
    public boolean wouldBeInCheck(Position from, Position to, Color color) {
        // Make a temporary move
        Piece movingPiece = getPieceAt(from);
        Piece capturedPiece = getPieceAt(to);

        squares[from.getRow()][from.getCol()] = null;
        squares[to.getRow()][to.getCol()] = movingPiece;

        boolean inCheck = isInCheck(color);

        // Restore the board
        squares[from.getRow()][from.getCol()] = movingPiece;
        squares[to.getRow()][to.getCol()] = capturedPiece;

        return inCheck;
    }

    /**
     * Gets all valid moves for a piece that don't leave the king in check.
     */
    public List<Position> getLegalMoves(Position from) {
        Piece piece = getPieceAt(from);
        if (piece == null) return new ArrayList<>();

        List<Position> validMoves = piece.getValidMoves(from, this);
        List<Position> legalMoves = new ArrayList<>();

        for (Position to : validMoves) {
            if (!wouldBeInCheck(from, to, piece.getColor())) {
                legalMoves.add(to);
            }
        }

        return legalMoves;
    }

    /**
     * Checks if the given color has any legal moves.
     */
    public boolean hasLegalMoves(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && piece.getColor() == color) {
                    Position from = new Position(row, col);
                    if (!getLegalMoves(from).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a copy of the board.
     */
    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                copy.squares[row][col] = this.squares[row][col];
            }
        }
        copy.enPassantTarget = this.enPassantTarget;
        return copy;
    }
}
