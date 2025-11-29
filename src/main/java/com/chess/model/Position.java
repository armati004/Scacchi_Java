package com.chess.model;

import java.util.Objects;

/**
 * Represents a position on the chess board.
 */
public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isValid() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public Position offset(int rowOffset, int colOffset) {
        return new Position(row + rowOffset, col + colOffset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    public static Position fromString(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid position notation: " + notation);
        }
        char file = notation.charAt(0);
        char rank = notation.charAt(1);
        int col = file - 'a';
        int row = 8 - (rank - '0');
        return new Position(row, col);
    }
}
