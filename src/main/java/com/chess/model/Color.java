package com.chess.model;

/**
 * Represents the color of a chess piece.
 */
public enum Color {
    WHITE,
    BLACK;

    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
