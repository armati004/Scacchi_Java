package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the King piece.
 */
public class King extends Piece {

    public King(Color color) {
        super(color, PieceType.KING);
    }

    @Override
    public List<Position> getValidMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},          {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            Position to = from.offset(dir[0], dir[1]);
            if (to.isValid() && board.canMoveTo(from, to, color)) {
                moves.add(to);
            }
        }

        // Castling
        if (!hasMoved && !board.isInCheck(color)) {
            // King-side castling
            Position rookKingSide = new Position(from.getRow(), 7);
            Piece rookK = board.getPieceAt(rookKingSide);
            if (rookK instanceof Rook && !rookK.hasMoved()) {
                boolean pathClear = true;
                for (int col = from.getCol() + 1; col < 7; col++) {
                    Position p = new Position(from.getRow(), col);
                    if (board.getPieceAt(p) != null) {
                        pathClear = false;
                        break;
                    }
                    // Check if king passes through check
                    if (col <= from.getCol() + 2 && board.wouldBeInCheck(from, p, color)) {
                        pathClear = false;
                        break;
                    }
                }
                if (pathClear) {
                    moves.add(new Position(from.getRow(), from.getCol() + 2));
                }
            }

            // Queen-side castling
            Position rookQueenSide = new Position(from.getRow(), 0);
            Piece rookQ = board.getPieceAt(rookQueenSide);
            if (rookQ instanceof Rook && !rookQ.hasMoved()) {
                boolean pathClear = true;
                for (int col = from.getCol() - 1; col > 0; col--) {
                    Position p = new Position(from.getRow(), col);
                    if (board.getPieceAt(p) != null) {
                        pathClear = false;
                        break;
                    }
                    // Check if king passes through check (only check the squares the king moves through)
                    if (col >= from.getCol() - 2 && board.wouldBeInCheck(from, p, color)) {
                        pathClear = false;
                        break;
                    }
                }
                if (pathClear) {
                    moves.add(new Position(from.getRow(), from.getCol() - 2));
                }
            }
        }

        return moves;
    }
}
