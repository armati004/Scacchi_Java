package com.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Knight piece.
 */
public class Knight extends Piece {

    public Knight(Color color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public List<Position> getValidMoves(Position from, Board board) {
        List<Position> moves = new ArrayList<>();
        int[][] offsets = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] offset : offsets) {
            Position to = from.offset(offset[0], offset[1]);
            if (to.isValid() && board.canMoveTo(from, to, color)) {
                moves.add(to);
            }
        }

        return moves;
    }
}
