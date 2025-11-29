package com.chess.model;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class Piece {
    protected final Color color;
    protected final PieceType type;
    protected boolean hasMoved;

    public Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }

    /**
     * Returns all valid moves for this piece from the given position.
     * This method should check basic movement rules but not check/checkmate validation.
     */
    public abstract List<Position> getValidMoves(Position from, Board board);

    /**
     * Returns true if this piece can move from the source to the destination.
     */
    public boolean canMoveTo(Position from, Position to, Board board) {
        return getValidMoves(from, board).contains(to);
    }

    @Override
    public String toString() {
        return (color == Color.WHITE ? "W" : "B") + type.getSymbol();
    }
}
